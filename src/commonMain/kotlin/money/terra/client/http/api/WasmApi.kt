package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.ResultWrapper
import money.terra.util.asUrlEncode
import money.terra.util.toJson

class WasmApi(
    val client: TerraHttpClient
) {

    suspend inline fun <T> getStoredData(contractAddress: String, query: Any): ResultWrapper<T> {
        return getStoredData(contractAddress, query.toJson().asUrlEncode)
    }

    suspend inline fun <T> getStoredData(contractAddress: String, queryMessage: String): ResultWrapper<T> {
        return client.get("/wasm/contracts/$contractAddress/store", mapOf("query_msg" to queryMessage))
    }
}