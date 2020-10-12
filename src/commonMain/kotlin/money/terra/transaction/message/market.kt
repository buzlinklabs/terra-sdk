package money.terra.transaction.message

import money.terra.model.Coin

data class SwapMessage(
    val trader: String,
    val offerCoin: Coin,
    val askDenom: String
) : Message("market/MsgSwap")

data class SwapSendMessage(
    val fromAddress: String,
    val toAddress: String,
    val offerCoin: Coin,
    val askDenom: String
) : Message("market/MsgSwapSend")

