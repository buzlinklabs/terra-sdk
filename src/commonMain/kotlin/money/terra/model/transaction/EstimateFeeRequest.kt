package money.terra.model.transaction

import money.terra.model.Coin
import money.terra.model.Transaction

data class EstimateFeeRequest(
    val tx: Transaction<*>,
    val gasAdjustment: String,
    val gasPrices: List<Coin>
)