package money.terra

interface Network {
    val chainId: String
}

@Suppress("FunctionName")
fun Network(chainId: String) = object: Network {

    override val chainId: String = chainId
}

enum class ProvidedNetwork(override val chainId: String) : Network {
    COLUMBUS_3("columbus-3"),
    VODKA_0001("vodka-0001"),
    SOJU_0014("soju-0014")
}