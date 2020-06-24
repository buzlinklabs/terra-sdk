package money.terra.model.transaction

import money.terra.model.Coin
import money.terra.model.Fee

data class EstimateFeeResult(
    val height: String,
    val result: EstimateFee
)

data class EstimateFee(
    val fees: List<Coin>,
    val gas: String
) {

    val asFee: Fee
        get() = Fee(gas, fees)
}