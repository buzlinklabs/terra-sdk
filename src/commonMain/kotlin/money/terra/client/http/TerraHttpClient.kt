package money.terra.client.http

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import money.terra.Network
import money.terra.client.http.api.AuthApi
import money.terra.client.http.api.BankApi
import money.terra.client.http.api.TransactionApi
import money.terra.client.http.api.WasmApi
import money.terra.wallet.PublicTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect

internal expect val ENGINE_FACTORY: EngineFactory<HttpClientEngineConfig>

internal expect val jsonSerializer: JsonSerializer

class EngineFactory<T : HttpClientEngineConfig>(
    val engine: HttpClientEngineFactory<T>,
    val configure: T.() -> Unit = {}
)

class TerraHttpClient(
    val network: Network,
    val serverUrl: String,
    val timeoutMillis: Long = 10000
) {

    val baseUrl: String = if (serverUrl.endsWith("/")) serverUrl.dropLast(1) else serverUrl

    val lcdServer = HttpClient(ENGINE_FACTORY.engine) {
        engine(ENGINE_FACTORY.configure)

        install(JsonFeature) {
            serializer = jsonSerializer
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    suspend inline fun <reified T> get(
        path: String,
        queryParam: Map<String, String?> = emptyMap()
    ): T {
        val query = if (queryParam.isEmpty()) {
            ""
        } else {
            queryParam.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
        }

        return lcdServer.get(baseUrl + path + query)
    }

    suspend inline fun <reified T> post(
        path: String,
        body: Any
    ): T = lcdServer.post(baseUrl + path) {
        contentType(ContentType.Application.Json)

        this.body = body
    }

    suspend fun wallet(address: String) = PublicTerraWallet(address).connect(this)

    suspend fun wallet(publicKey: ByteArray, privateKey: ByteArray) = TerraWallet(publicKey, privateKey).connect(this)

    fun auth() = AuthApi(this)

    fun bank() = BankApi(this)

    fun transaction() = TransactionApi(this)

    fun wasm() = WasmApi(this)
}