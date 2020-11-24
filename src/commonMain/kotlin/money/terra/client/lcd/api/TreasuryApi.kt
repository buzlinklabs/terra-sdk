package money.terra.client.lcd.api

import money.terra.client.HttpClient
import money.terra.model.ResultWrapper

class TreasuryApi(
    private val client: HttpClient
) {

    suspend fun getTaxRate(): ResultWrapper<String> {
        return client.get("/treasury/tax_rate")
    }

    suspend fun getTaxCapacity(denom: String): ResultWrapper<String> {
        return client.get("/treasury/tax_cap/$denom")
    }
}