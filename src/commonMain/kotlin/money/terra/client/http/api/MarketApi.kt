package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.Coin
import money.terra.model.ResultWrapper

class MarketApi(
    private val client: TerraHttpClient
) {

    suspend fun estimateSwapResult(quantity: String, offerDenom: String, askDenom: String): ResultWrapper<Coin> {
        return client.get("/market/swap", mapOf("offer_coin" to quantity + offerDenom, "ask_denom" to askDenom))
    }
}