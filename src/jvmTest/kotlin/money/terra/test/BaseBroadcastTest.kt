package money.terra.test

import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.terra.Terra
import money.terra.client.http.TerraHttpClient
import money.terra.model.transaction.BroadcastTransactionResult
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.broadcastAsync
import money.terra.transaction.broadcastSync
import org.junit.jupiter.api.*
import java.net.SocketTimeoutException

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
abstract class BaseBroadcastTest(private val terra: Terra) {

    private val hashCheckApi = TerraHttpClient(
        terra.httpClient.network,
        terra.httpClient.server
    ).transaction()

    open fun init() {
        //do nothing
    }

    abstract fun TransactionBuilder.setup()

    open fun BroadcastTransactionResult.validate() {
        assert(code == null)
    }

    @Test
    @DisplayName("broadcastAsync")
    @Order(1)
    fun testBroadcastAsync() {
        val result = runBlocking { terra.broadcastAsync { setup() } }

        result.validate()

        waitTransactionComplete(result.txhash)
    }

    @Test
    @DisplayName("broadcastSync")
    @Order(2)
    fun testBroadcastSync() {
        val result = runBlocking { terra.broadcastSync { setup() } }

        result.validate()

        waitTransactionComplete(result.txhash)
    }

    @Test
    @DisplayName("broadcastBlock")
    @Order(3)
    fun testBroadcastBlock() {
        val result = try {
            runBlocking { terra.broadcastSync { setup() } }
        } catch (e: ResponseException) {
            if (e.response.status == HttpStatusCode.BadGateway) { //block 생성까지 오래걸릴 경우 timeout 이 발생할 수 있어 예외 처리
                return
            }

            throw e
        } catch (e: SocketTimeoutException) {
            return
        }

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