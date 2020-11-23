package money.terra.client.lcd.api

import money.terra.client.HttpClient
import money.terra.model.ResultWrapper
import money.terra.model.TypeWrapper
import money.terra.model.auth.Account

class AuthApi(
    private val client: HttpClient
) {

    suspend fun getAccountInfo(address: String): ResultWrapper<TypeWrapper<Account>> {
        return client.get("/auth/accounts/$address")
    }
}