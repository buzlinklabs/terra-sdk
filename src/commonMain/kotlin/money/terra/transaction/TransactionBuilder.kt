package money.terra.transaction

import money.terra.Terra
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.transaction.message.Message

class TransactionBuilder {

    var fee: Fee? = null
    var memo: String = ""
    var messages: MutableList<Message> = mutableListOf()

    fun build() = Transaction(messages.map { it.wrapper() }, fee, memo)

    fun with(message: Message) = messages.add(message)

    inline fun with(block: () -> Message) = with(block())

    fun Message.addThis() = with(this)
}

suspend inline fun Terra.broadcastSync(
    gasAmount: Long? = null,
    gasPrices: List<Coin>? = null,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastSync(it.build(), gasAmount, gasPrices) }

suspend inline fun Terra.broadcastAsync(
    gasAmount: Long? = null,
    gasPrices: List<Coin>? = null,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastAsync(it.build(), gasAmount, gasPrices) }

suspend inline fun Terra.broadcastBlock(
    gasAmount: Long? = null,
    gasPrices: List<Coin>? = null,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastBlock(it.build(), gasAmount, gasPrices) }