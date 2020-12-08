package money.terra

import io.ktor.client.features.*
import kotlinx.coroutines.delay
import money.terra.client.TerraClient
import money.terra.client.connect
import money.terra.client.lcd.TerraLcdClient
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.BroadcastTransactionResult
import money.terra.transaction.message.Message
import money.terra.util.provider.*
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect
import kotlin.math.ceil

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

    suspend fun <T : Message> broadcastSync(
        transaction: Transaction<T>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, withLock, client::broadcastSync)

    suspend fun <T : Message> broadcastAsync(
        transaction: Transaction<T>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, withLock, client::broadcastAsync)

    suspend fun <T : Message> broadcastBlock(
        transaction: Transaction<T>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, withLock, client::broadcastBlock)

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

    private suspend fun <T : Message, R : BroadcastTransactionResult> broadcast(
        transaction: Transaction<T>,
        gasAmount: Long?,
        gasPrices: List<Coin>?,
        withLock: Boolean,
        broadcaster: suspend (Transaction<*>) -> R
    ) = if (withLock) {
        semaphoreProvider.use(walletAddress) {
            broadcast(transaction, gasAmount, gasPrices, broadcaster)
        }
    } else {
        broadcast(transaction, gasAmount, gasPrices, broadcaster)
    }

    private suspend fun <T : Message, R : BroadcastTransactionResult> broadcast(
        transaction: Transaction<T>,
        gasAmount: Long?,
        gasPrices: List<Coin>?,
        broadcaster: suspend (Transaction<*>) -> R
    ) = try {
        val polishedTransaction = transaction.polish(gasAmount, gasPrices)
        val broadcastResult = broadcaster(polishedTransaction)

        if (broadcastResult.code != null || broadcastResult.code != 0) {
            sequenceProvider.refresh(walletAddress)
        }

        polishedTransaction to broadcastResult
    } catch (e: Exception) {
        sequenceProvider.refresh(walletAddress)

        throw e
    }

    private suspend fun <T : Message> Transaction<T>.polish(
        gasAmount: Long?,
        gasPrices: List<Coin>?
    ): Transaction<T> {
        if (fee == null) {
            val providedGasPrices = gasPrices ?: gasPriceProvider?.get(this)
            if (providedGasPrices.isNullOrEmpty()) {
                throw IllegalArgumentException("Required to set fee or gasPrices or gasPriceProvider")
            }


            val providedFee = gasAmount?.let { gas ->
                val feeAmount = providedGasPrices.map {
                    it.copy(amount = ceil(it.amount.toDouble() * gas).toLong().toString())
                }
                Fee(gas.toString(), feeAmount)
            } ?: estimateFee(this, providedGasPrices, gasAdjustment)

            return copy(fee = providedFee).sign()
        }

        return if (isSigned) this else sign()
    }

    private suspend fun <T : Message> Transaction<T>.sign() = wallet.sign(this, sequenceProvider.next(walletAddress))
}