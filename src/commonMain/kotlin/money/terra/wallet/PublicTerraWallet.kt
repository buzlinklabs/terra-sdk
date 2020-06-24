package money.terra.wallet

import money.terra.ProvidedNetwork
import money.terra.client.http.TerraHttpClient

interface PublicTerraWallet {

    val address: String
}

@Suppress("FunctionName")
fun TerraWallet(address: String) = PublicTerraWallet(address)

@Suppress("FunctionName")
fun PublicTerraWallet(address: String) = object : PublicTerraWallet {

    override val address: String = address
}

suspend fun PublicTerraWallet.connect(network: ProvidedNetwork) = ConnectedPublicTerraWallet(this, network)
    .apply { connect() }

suspend fun PublicTerraWallet.connect(httpClient: TerraHttpClient) = ConnectedPublicTerraWallet(this, httpClient)
    .apply { connect() }