package money.terra.client.lcd.api

import money.terra.client.HttpClient
import money.terra.model.Coin
import money.terra.model.ResultWrapper

class MarketApi(
    private val client: HttpClient
) {

    suspend fun estimateSwapResult(quantity: String, offerDenom: String, askDenom: String): ResultWrapper<Coin> {
        return client.get("/market/swap", mapOf("offer_coin" to quantity + offerDenom, "ask_denom" to askDenom))
    }
}