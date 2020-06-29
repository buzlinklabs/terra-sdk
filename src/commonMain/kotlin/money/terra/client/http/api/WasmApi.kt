package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.ResultWrapper
import money.terra.util.asUrlEncode
import money.terra.util.toJson

class WasmApi(
    private val client: TerraHttpClient
) {

    suspend fun getStoredData(contractAddress: String, query: Any): ResultWrapper<String> {
        return getStoredData(contractAddress, query.toJson().asUrlEncode)
    }

    suspend fun getStoredData(contractAddress: String, queryMessage: String): ResultWrapper<String> {
        return client.get("/wasm/contract/$contractAddress/store", mapOf("query_msg" to queryMessage))
    }
}