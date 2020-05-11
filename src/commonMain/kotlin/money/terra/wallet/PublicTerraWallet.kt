package money.terra.wallet

interface PublicTerraWallet {

    val address: String
}

@Suppress("FunctionName")
fun PublicTerraWallet(address: String) = object : PublicTerraWallet {

    override val address: String = address
}