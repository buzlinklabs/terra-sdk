package money.terra.model.transaction

import money.terra.model.Coin

data class EstimateFeeResult(
    val fees: List<Coin>,
    val gas: Int
)