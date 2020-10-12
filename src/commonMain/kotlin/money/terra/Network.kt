package money.terra

interface Network {
    val chainId: String
}

@Suppress("FunctionName")
fun Network(chainId: String) = object: Network {

    override val chainId: String = chainId
}