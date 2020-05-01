package money.terra.model.transaction

import money.terra.model.DecCoin
import money.terra.model.Transaction

data class EstimateRequest(
    val tx: Transaction<*>,
    val gasAdjustment: String,
    val gasPrices: List<DecCoin>
)