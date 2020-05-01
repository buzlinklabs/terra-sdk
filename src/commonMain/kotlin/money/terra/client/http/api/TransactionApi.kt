package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.*

class TransactionApi(
    private val client: TerraHttpClient
) {

    suspend fun getByHash(hash: String): TransactionQueryResult {
        return client.get("/txs/$hash")
    }

    suspend fun broadcastSignedTransaction(body: BroadcastTransactionSyncRequest): BroadcastTransactionSyncResult {
        return client.post("/txs", body)
    }

    suspend fun broadcastSignedTransaction(body: BroadcastTransactionAsyncRequest): BroadcastTransactionAsyncResult {
        return client.post("/txs", body)
    }

    suspend fun broadcastSignedTransaction(body: BroadcastTransactionBlockRequest): BroadcastTransactionBlockResult {
        return client.post("/txs", body)
    }

//    @Deprecated("not tested")
//    @POST("txs/estimate_fee")
//    suspend fun estimateFeeAndGas(@Body body: EstimateRequest): EstimateFeeResult
}