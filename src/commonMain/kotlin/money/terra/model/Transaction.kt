package money.terra.model

import money.terra.transaction.message.Message

data class Transaction<T : Message>(
    val msg: List<TypeWrapper<T>>,
    val fee: Fee,
    val memo: String = "",
    val signatures: List<Signature>? = null
) {
    val isSigned: Boolean
        get() = !signatures.isNullOrEmpty()
}