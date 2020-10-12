package money.terra.test

import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.terra.Terra
import money.terra.client.http.TerraHttpClient
import money.terra.model.transaction.BroadcastTransactionResult
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.broadcastSync
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

abstract class BaseBroadcastTest(private val terra: Terra) {

    private val hashCheckApi = TerraHttpClient(
        terra.wallet.httpClient.network,
        terra.wallet.httpClient.serverUrl
    ).transaction()

    abstract fun TransactionBuilder.setup()

    open fun BroadcastTransactionResult.validate() {
        assert(code == null)
    }

    @Test
    @DisplayName("broadcast")
    fun testBroadcastSync() {
        val result = runBlocking { terra.broadcastSync { setup() } }

        result.validate()

        waitTransactionComplete(result.txhash)
    }

    private fun waitTransactionComplete(hash: String) {
        runBlocking {
            while (true) {
                try {
                    val result = hashCheckApi.getByHash(hash)
                    println("success get transaction : $hash - ${result.rawLog}")
                    break
                } catch (e: ClientRequestException) {
                    delay(1000)
                }
            }
        }
    }
}