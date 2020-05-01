package money.terra.model

data class BaseRequest(
    val chainId: String,
    val from: String,
    val accountNumber: String,
    val sequence: String,
    val fees: List<Coin>,
    val memo: String? = null,
    val gas: String = "200000",
    val gasAdjustment: String? = null,
    val simulate: Boolean = false
)