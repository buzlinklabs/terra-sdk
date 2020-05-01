package money.terra.signer

import money.terra.model.Fee
import money.terra.model.TypeWrapper
import money.terra.transaction.message.Message

data class SignMessage<T : Message>(
    val sequence: String,
    val accountNumber: String,
    val chainId: String,
    val fee: Fee,
    val msgs: List<TypeWrapper<T>>,
    val memo: String
)