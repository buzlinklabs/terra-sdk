package money.terra.test

import kotlinx.coroutines.runBlocking
import money.terra.Terra
import money.terra.model.Coin
import money.terra.model.transaction.BroadcastTransactionResult
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.broadcastSync
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

abstract class BaseBroadcastTest(private val terra: Terra) {

    abstract fun TransactionBuilder.setup()

    open fun BroadcastTransactionResult.validate() {
        assert(code == null)
    }

    @Test
    @DisplayName("broadcast")
    fun testBroadcastSync() {
        val result = runBlocking { terra.broadcastSync(gasPrices = listOf(Coin("uluna", "50"))) { setup() } }

        result.validate()

        waitTransactionComplete(result.txhash)
    }

    private fun waitTransactionComplete(hash: String) {
        runBlocking {
            val result = terra.wait(hash)
            println("success get transaction : $hash - ${result.rawLog}")
        }
    }
}