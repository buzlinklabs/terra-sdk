package money.terra.client.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import money.terra.Network
import money.terra.ProvidedNetwork
import money.terra.client.ProvidedTerraServer
import money.terra.client.TerraServer
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
    val server: TerraServer,
    val timeoutMillis: Long = 10000,
    val protocol: String = "https"
) {

    internal val baseUrl: String = "$protocol://${server.host}"

    private val client = HttpClient(ENGINE_FACTORY.engine) {
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

    constructor(
        network: ProvidedNetwork,
        timeoutMillis: Long = 10000,
        protocol: String = "https"
    ) : this(network, ProvidedTerraServer.of(network), timeoutMillis, protocol)

    internal suspend inline fun <reified T> get(
        path: String,
        queryParam: Map<String, String?> = emptyMap()
    ): T {
        val query = if (queryParam.isEmpty()) {
            ""
        } else {
            queryParam.entries.joinToString("&", "?") { "${it.key}=${it.value}" }
        }

        return client.get(baseUrl + path + query)
    }

    internal suspend inline fun <reified T> post(
        path: String,
        body: Any
    ): T = client.post(baseUrl + path) {
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