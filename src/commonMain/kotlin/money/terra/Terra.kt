package money.terra

import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.delay
import money.terra.client.TerraServer
import money.terra.client.http.TerraHttpClient
import money.terra.model.Coin
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.*
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect

class Terra(
    val wallet: ConnectedTerraWallet
) {

    companion object {

        suspend fun connect(wallet: TerraWallet, network: Network, server: TerraServer): Terra {
            return connect(wallet, TerraHttpClient(network, server))
        }

        suspend fun connect(wallet: TerraWallet, network: ProvidedNetwork): Terra {
            return connect(wallet, TerraHttpClient(network))
        }

        suspend fun connect(wallet: TerraWallet, liteClient: TerraHttpClient): Terra {
            return Terra(wallet.connect(liteClient))
        }

        val DEFAULT_GAS_PRICES = listOf(Coin("uluna", "50"))
    }

    private val httpClient = wallet.httpClient

    private val transactionApi = httpClient.transaction()

    suspend fun broadcastSync(
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES,
        transaction: Transaction<*>
    ): BroadcastTransactionSyncResult {
        val broadcastRequest = BroadcastTransactionSyncRequest(transaction.polish(gasPrices))

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastAsync(
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES,
        transaction: Transaction<*>
    ): BroadcastTransactionAsyncResult {
        val broadcastRequest = BroadcastTransactionAsyncRequest(transaction.polish(gasPrices))

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastBlock(
        gasPrices: List<Coin> = DEFAULT_GAS_PRICES,
        transaction: Transaction<*>
    ): BroadcastTransactionBlockResult {
        val broadcastRequest = BroadcastTransactionBlockRequest(transaction.polish(gasPrices))

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
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

    suspend fun wait(transactionHash: String, intervalMillis: Long = 1000): TransactionQueryResult {
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

            return wallet.sign(transaction).first
        }

        return if (isSigned) this else wallet.sign(this).first
    }
}