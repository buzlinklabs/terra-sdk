package money.terra.model

import money.terra.model.transaction.BroadcastTransactionEvent
import money.terra.model.transaction.BroadcastTransactionLog

data class TransactionQueryResult(
    val height: Long,
    val txhash: String,
    val rawLog: String,
    val logs: List<BroadcastTransactionLog>,
    val gasWanted: String,
    val gasUsed: String,
    val tx: TypeWrapper<Transaction<*>>,
    val timestamp: String,
    val events: List<BroadcastTransactionEvent>
)