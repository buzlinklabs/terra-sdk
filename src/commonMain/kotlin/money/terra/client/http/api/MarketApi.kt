package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.Coin
import money.terra.model.ResultWrapper
import money.terra.model.Transaction
import money.terra.model.TypeWrapper
import money.terra.model.bank.SendCoinRequest
import money.terra.transaction.message.SendMessage

class MarketApi(
    private val client: TerraHttpClient
) {

    suspend fun estimateSwapResult(denom: String, amount: String): ResultWrapper<List<Coin>> {
        return client.get("/market/swap", mapOf("denom" to denom, "amount" to amount))
    }
}