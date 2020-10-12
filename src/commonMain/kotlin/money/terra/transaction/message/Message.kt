package money.terra.transaction.message

import money.terra.model.TypeWrapper

abstract class Message(
    private val typeName: String
) {

    fun wrapper() = TypeWrapper(typeName, this)
}