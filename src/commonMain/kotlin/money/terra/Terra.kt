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

        var printLog: ((String) -> Unit)? = null
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
        sequence: Long? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, sequence, withLock, client::broadcastSync)

    suspend fun <T : Message> broadcastAsync(
        transaction: Transaction<T>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null,
        sequence: Long? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, sequence, withLock, client::broadcastAsync)

    suspend fun <T : Message> broadcastBlock(
        transaction: Transaction<T>,
        gasAmount: Long? = null,
        gasPrices: List<Coin>? = null,
        sequence: Long? = null,
        withLock: Boolean = true
    ) = broadcast(transaction, gasAmount, gasPrices, sequence, withLock, client::broadcastBlock)

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
        maxCheckCount: Int? = 5,
    ): TransactionQueryResult {
        val intervalDelay = intervalMillis.toLong()
        var checkCount = 0
        while (true) {
            try {
                checkCount += 1
                return client.getByHash(transactionHash)
            } catch (e: ClientRequestException) {
                if (maxCheckCount != null && checkCount >= maxCheckCount) {
                    throw IllegalStateException("Reach maximum check count", e)
                }

                if (checkCount == 1) {
                    delay(initialDelayMillis.toLong())
                } else {
                    delay(intervalDelay)
                }
            }
        }
    }

    private suspend fun <T : Message, R : BroadcastTransactionResult> broadcast(
        transaction: Transaction<T>,
        gasAmount: Long?,
        gasPrices: List<Coin>?,
        sequence: Long?,
        withLock: Boolean,
        broadcaster: suspend (Transaction<*>) -> R
    ) = if (withLock) {
        semaphoreProvider.use(walletAddress) {
            broadcast(transaction, gasAmount, gasPrices, sequence, broadcaster)
        }
    } else {
        broadcast(transaction, gasAmount, gasPrices, sequence, broadcaster)
    }

    private suspend fun <T : Message, R : BroadcastTransactionResult> broadcast(
        transaction: Transaction<T>,
        gasAmount: Long?,
        gasPrices: List<Coin>?,
        sequence: Long?,
        broadcaster: suspend (Transaction<*>) -> R
    ): Pair<Transaction<T>, R> {
        if (transaction.isSigned) {
            return transaction to broadcaster(transaction)
        }

        try {
            val seq = sequence ?: sequenceProvider.current(walletAddress)
            val signedTransaction = transaction.sign(gasAmount, gasPrices, seq)
            val broadcastResult = broadcaster(signedTransaction)

            if (sequence == null) {
                when(broadcastResult.code) {
                    null, 0 -> sequenceProvider.increase(walletAddress)
                    4 -> sequenceProvider.refresh(walletAddress)
                }
            }

            return signedTransaction to broadcastResult
        } catch (e: Exception) {
            if (sequence == null) {
                sequenceProvider.refresh(walletAddress)
            }

            throw e
        }
    }

    private suspend fun <T : Message> Transaction<T>.sign(
        gasAmount: Long?,
        gasPrices: List<Coin>?,
        sequence: Long
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

            return copy(fee = providedFee).sign(sequence)
        }

        return sign(sequence)
    }

    private fun <T : Message> Transaction<T>.sign(sequence: Long) : Transaction<T> {
        printLog?.invoke("Sign Transaction (wallet=${walletAddress}, sequence=$sequence)")
        return wallet.sign(this, sequence)
    }
}