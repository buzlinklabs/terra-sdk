@file:Suppress("UNCHECKED_CAST")

package money.terra.client.http

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonSerializer
import money.terra.util.jsonMapper

internal actual val ENGINE_FACTORY: EngineFactory<HttpClientEngineConfig> = EngineFactory(OkHttp) {
    config {
        this.retryOnConnectionFailure(true)
    }
} as EngineFactory<HttpClientEngineConfig>

internal actual val jsonSerializer: JsonSerializer = JacksonSerializer(jsonMapper)