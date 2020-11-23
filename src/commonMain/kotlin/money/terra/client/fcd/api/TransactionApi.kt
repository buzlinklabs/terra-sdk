package money.terra.client.fcd.api

import money.terra.client.HttpClient

class TransactionApi(
    private val client: HttpClient
) {

    suspend fun getGasPrices(): Map<String, String> = client.get("/v1/txs/gas_prices")
}