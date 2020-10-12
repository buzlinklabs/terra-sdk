package money.terra.transaction.proposal

import money.terra.model.TypeWrapper

abstract class Proposal(
    private val typeName: String
) {

    fun wrapper() = TypeWrapper(typeName, this)
}