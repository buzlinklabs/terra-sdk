package money.terra

import io.ktor.client.features.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import money.terra.client.TerraClient
import money.terra.client.lcd.TerraLcdClient
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.BroadcastTransactionAsyncResult
import money.terra.model.transaction.BroadcastTransactionBlockResult
import money.terra.model.transaction.BroadcastTransactionSyncResult
import money.terra.util.provider.*
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect

class Terra(
    val wallet: ConnectedTerraWallet
) {

    companion object {

        suspend fun connect(wallet: TerraWallet, chainId: String, lcdUrl: String): Terra {
            return Terra(wallet.connect(TerraLcdClient(Network(chainId), lcdUrl)))
        }

        suspend fun connect(wallet: TerraWallet, client: TerraClient): Terra {
            return Terra(client.connect(wallet))
        }
    }

    val client = wallet.client

    var gasAdjustment: String = "1.4"

    var semaphoreProvider: SemaphoreProvider = LocalSemaphoreProvider
    var sequenceProvider: SequenceProvider = AlwaysFetchSequenceProvider(client)
    var gasPriceProvider: GasPriceProvider? = null

    private val walletAddress = wallet.address

    private val semaphore = Semaphore(1)

    suspend fun broadcastSync(
        transaction: Transaction<*>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null
    ): BroadcastTransactionSyncResult = semaphoreProvider.use(walletAddress) {
        client.broadcastSync(transaction.polish(gasAmount, gasPrices))
    }

    suspend fun broadcastAsync(
        transaction: Transaction<*>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null
    ): BroadcastTransactionAsyncResult = semaphoreProvider.use(walletAddress) {
        client.broadcastAsync(transaction.polish(gasAmount, gasPrices))
    }

    suspend fun broadcastBlock(
        transaction: Transaction<*>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null
    ): BroadcastTransactionBlockResult = semaphoreProvider.use(walletAddress) {
        client.broadcastBlock(transaction.polish(gasAmount, gasPrices))
    }

    suspend fun estimateFee(
        transaction: Transaction<*>,
        gasPrices: List<Coin>,
        gasAdjustment: String = this.gasAdjustment
    ): Fee = client.estimateFee(transaction, gasAdjustment, gasPrices)

    suspend fun getTransaction(transactionHash: String): TransactionQueryResult {
        return client.getByHash(transactionHash)
    }

    suspend fun wait(
        transactionHash: String,
        intervalMillis: Int = 1000,
        initialDelayMillis: Int = 5000,
        maxCheckCount: Int = 5
    ): TransactionQueryResult {
        delay(initialDelayMillis.toLong())
        val intervalDelay = intervalMillis.toLong()
        var tryCount = 0
        while (true) {
            try {
                tryCount += 1
                return client.getByHash(transactionHash)
            } catch (e: ClientRequestException) {
                if (tryCount >= maxCheckCount) {
                    throw IllegalStateException("Reach maximum check count", e)
                }

                delay(intervalDelay)
            }
        }
    }

    private suspend fun Transaction<*>.polish(
        gasAmount: Long?,
        gasPrices: List<Coin>?
    ): Transaction<*> {
        if (fee == null) {
            val providedGasPrices = gasPrices ?: gasPriceProvider?.get(this)
            if (providedGasPrices.isNullOrEmpty()) {
                throw IllegalArgumentException("Required to set fee or gasPrices or gasPriceProvider")
            }

            val providedFee = gasAmount?.let { Fee(it.toString(), providedGasPrices) }
                ?: estimateFee(this, providedGasPrices, gasAdjustment)

            return copy(fee = providedFee).sign()
        }

        return if (isSigned) this else sign()
    }

    private suspend fun Transaction<*>.sign() = wallet.sign(this, sequenceProvider.get(walletAddress))
}