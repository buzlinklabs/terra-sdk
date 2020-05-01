package money.terra.model

data class Fee(
    val gas: String,
    val amount: List<Coin>
)