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

    fun Message.addThis() = messages.add(this)
}

suspend inline fun Terra.broadcastSync(
    gasPrices: List<Coin> = Terra.DEFAULT_GAS_PRICES,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastSync(it.build(), gasPrices) }

suspend inline fun Terra.broadcastAsync(
    gasPrices: List<Coin> = Terra.DEFAULT_GAS_PRICES,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastAsync(it.build(), gasPrices) }

suspend inline fun Terra.broadcastBlock(
    gasPrices: List<Coin> = Terra.DEFAULT_GAS_PRICES,
    block: TransactionBuilder.() -> Unit
) = TransactionBuilder()
    .apply { block() }
    .let { broadcastBlock(it.build(), gasPrices) }