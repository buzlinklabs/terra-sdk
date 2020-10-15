package money.terra.transaction.message

import money.terra.model.Coin
import money.terra.model.TypeWrapper

abstract class Authorization(
    private val typeName: String
) {

    fun wrapper() = TypeWrapper(typeName, this)
}

data class SendAuthorization(
    val spendLimit: List<Coin>
) : Authorization("msgauth/SendAuthorization")

class GrantAuthorizationMessage(
    val granter: String,
    val grantee: String,
    authorization: Authorization,
    val period: String
) : Message("msgauth/MsgGrantAuthorization") {

    val authorization: TypeWrapper<Authorization> = authorization.wrapper()
}

data class RevokeAuthorizationMessage(
    val granter: String,
    val grantee: String,
    val authorizationMsgType: String
) : Message("msgauth/MsgRevokeAuthorization")

class ExecuteAuthorizedMessage(
    val grantee: String,
    messages: List<Message>
) : Message("msgauth/MsgExecAuthorized") {

    val msgs: List<TypeWrapper<Message>> = messages.map { it.wrapper() }
}