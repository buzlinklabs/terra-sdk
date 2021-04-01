@file:Suppress("UNCHECKED_CAST")

package money.terra.client

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import money.terra.util.jsonMapper

internal actual val ENGINE_FACTORY: EngineFactory<HttpClientEngineConfig> = EngineFactory(OkHttp) {
    config {
        this.retryOnConnectionFailure(true)
    }
} as EngineFactory<HttpClientEngineConfig>

internal actual val jsonSerializer: JsonSerializer = JacksonSerializer(jsonMapper)