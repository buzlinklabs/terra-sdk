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

data class InstantiateContract(
    val sender: String,
    val codeId: String,
    val initMsg: String,
    val initCoins: List<Coin>
) : Message("wasm/MsgInstantiateContract")

data class ExecuteContract(
    val sender: String,
    val contract: String,
    val msg: String,
    val coins: List<Coin>
) : Message("wasm/MsgExecuteContract")