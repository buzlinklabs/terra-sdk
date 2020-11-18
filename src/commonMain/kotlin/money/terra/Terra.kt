package money.terra

import io.ktor.client.features.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import money.terra.client.http.TerraHttpClient
import money.terra.model.Coin
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.*
import money.terra.util.use
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect

class Terra(
    val wallet: ConnectedTerraWallet
) {

    companion object {

        suspend fun connect(wallet: TerraWallet, chainId: String, lcdUrl: String): Terra {
            return Terra(wallet.connect(TerraHttpClient(Network(chainId), lcdUrl)))
        }

        suspend fun connect(wallet: TerraWallet, liteClient: TerraHttpClient): Terra {
            return Terra(wallet.connect(liteClient))
        }

        val DEFAULT_GAS_PRICES = listOf(Coin("uluna", "50"))
    }

    var sequenceProvider: (suspend () -> Long)? = null

    private val httpClient = wallet.httpClient

    private val transactionApi = httpClient.transaction()

    private val semaphore = Semaphore(1)

    suspend fun broadcastSync(
        transaction: Transaction<*>,
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES
    ): BroadcastTransactionSyncResult = semaphore.use {
        val broadcastRequest = BroadcastTransactionSyncRequest(transaction.polish(gasPrices))

        transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastAsync(
        transaction: Transaction<*>,
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES
    ): BroadcastTransactionAsyncResult = semaphore.use {
        val broadcastRequest = BroadcastTransactionAsyncRequest(transaction.polish(gasPrices))

        transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastBlock(
        transaction: Transaction<*>,
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES
    ): BroadcastTransactionBlockResult = semaphore.use {
        val broadcastRequest = BroadcastTransactionBlockRequest(transaction.polish(gasPrices))

        transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun estimateFee(
        transaction: Transaction<*>,
        gasAdjustment: String = "1.4",
        gasPrices: List<Coin> = listOf(Coin("uluna", "50"))
    ): EstimateFee {
        val request = EstimateFeeRequest(transaction, gasAdjustment, gasPrices)

        return transactionApi.estimateFeeAndGas(request).result
    }

    suspend fun getTransaction(transactionHash: String): TransactionQueryResult {
        return transactionApi.getByHash(transactionHash)
    }

    suspend fun wait(
        transactionHash: String,
        intervalMillis: Long = 1000,
        preDelayMillis: Long = 5000
    ): TransactionQueryResult {
        delay(preDelayMillis)
        while (true) {
            try {
                return transactionApi.getByHash(transactionHash)
            } catch (e: ClientRequestException) {
                delay(intervalMillis)
            }
        }
    }

    private suspend fun Transaction<*>.polish(gasPrices: List<Coin>): Transaction<*> {
        if (fee == null) {
            val transaction = copy(fee = estimateFee(this, gasPrices = gasPrices).asFee)

            return transaction.sign()
        }

        return if (isSigned) this else sign()
    }

    private suspend fun Transaction<*>.sign() = if (sequenceProvider == null) {
        wallet.sign(this).first
    } else {
        wallet.sign(this, sequenceProvider!!.invoke())
    }
}