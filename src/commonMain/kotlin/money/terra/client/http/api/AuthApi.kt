package money.terra.client.http.api

import money.terra.client.http.TerraHttpClient
import money.terra.model.ResultWrapper
import money.terra.model.TypeWrapper
import money.terra.model.auth.Account

class AuthApi(
    private val client: TerraHttpClient
) {

    suspend fun getAccountInfo(address: String): ResultWrapper<TypeWrapper<Account>> {
        return client.get("/auth/accounts/$address")
    }
}