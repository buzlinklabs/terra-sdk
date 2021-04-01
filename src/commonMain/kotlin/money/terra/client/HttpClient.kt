package money.terra.client

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*

internal expect val ENGINE_FACTORY: EngineFactory<HttpClientEngineConfig>

internal expect val jsonSerializer: JsonSerializer

class EngineFactory<T : HttpClientEngineConfig>(
    val engine: HttpClientEngineFactory<T>,
    val configure: T.() -> Unit = {}
)

class HttpClient(
    val serverUrl: String,
    val timeoutMillis: Long = 10000
) {

    val baseUrl: String = if (serverUrl.endsWith("/")) serverUrl.dropLast(1) else serverUrl

    val server = HttpClient(ENGINE_FACTORY.engine) {
        engine(ENGINE_FACTORY.configure)

        install(JsonFeature) {
            serializer = jsonSerializer
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
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

        return server.get(baseUrl + path + query)
    }

    suspend inline fun <reified T> post(
        path: String,
        body: Any
    ): T = server.post(baseUrl + path) {
        contentType(ContentType.Application.Json)

        this.body = body
    }
}