package money.terra.transaction.message

import money.terra.model.Coin

data class SendMessage(
    val fromAddress: String,
    val toAddress: String,
    val amount: List<Coin>
) : Message("bank/MsgSend")

data class MultipleSendMessage(
    val inputs: List<Amount>,
    val outputs: List<Amount>
) : Message("bank/MsgMultiSend"){

    data class Amount(
        val address: String,
        val coins: List<Coin>
    )
}