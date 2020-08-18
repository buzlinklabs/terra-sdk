package money.terra.model.auth

import money.terra.model.Coin
import money.terra.model.PublicKey

data class Account(
    val accountNumber: String,
    val address: String,
    val coins: List<Coin>,
    val publicKey: PublicKey?,
    val sequence: String
)