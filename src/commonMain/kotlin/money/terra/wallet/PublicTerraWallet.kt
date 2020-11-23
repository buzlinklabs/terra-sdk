package money.terra.wallet

import money.terra.client.TerraClient

interface PublicTerraWallet {

    val address: String
}

@Suppress("FunctionName")
fun TerraWallet(address: String) = PublicTerraWallet(address)

@Suppress("FunctionName")
fun PublicTerraWallet(address: String) = object : PublicTerraWallet {

    override val address: String = address
}

fun PublicTerraWallet.connect(client: TerraClient) = ConnectedPublicTerraWallet(this, client)