package money.terra.transaction.message

import money.terra.model.Coin
import money.terra.model.TypeWrapper

abstract class Message(
    private val typeName: String
) {

    fun wrapper() = TypeWrapper(typeName, this)
}

data class SendMessage(
    val fromAddress: String,
    val toAddress: String,
    val amount: List<Coin>
) : Message("bank/MsgSend")