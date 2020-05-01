package money.terra.transaction

import money.terra.Terra
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.transaction.message.Message

class TransactionBuilder {

    var fee: Fee? = null
    var memo: String = ""
    var messages: MutableList<Message> = mutableListOf()

    fun build() = Transaction(messages.map { it.wrapper() }, fee!!, memo)

    fun Message.addThis() = messages.add(this)
}

suspend inline fun Terra.broadcast(block: TransactionBuilder.() -> Unit) = TransactionBuilder()
    .apply { block() }
    .let { broadcast(it.build()) }

suspend inline fun Terra.broadcastSync(block: TransactionBuilder.() -> Unit) = TransactionBuilder()
    .apply { block() }
    .let { broadcastSync(it.build()) }

suspend inline fun Terra.broadcastAsync(block: TransactionBuilder.() -> Unit) = TransactionBuilder()
    .apply { block() }
    .let { broadcastAsync(it.build()) }

suspend inline fun Terra.broadcastBlock(block: TransactionBuilder.() -> Unit) = TransactionBuilder()
    .apply { block() }
    .let { broadcastBlock(it.build()) }