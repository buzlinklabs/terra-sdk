package money.terra.model.bank

import money.terra.model.BaseRequest
import money.terra.model.Coin

data class SendCoinRequest(
    val baseReq: BaseRequest,
    val coins: List<Coin>
)