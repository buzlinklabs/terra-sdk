package money.terra.model.auth

import money.terra.model.Coin

data class Account(
    val accountNumber: String,
    val address: String,
    val coins: List<Coin>,
    val publicKey: String?,
    val sequence: String
)