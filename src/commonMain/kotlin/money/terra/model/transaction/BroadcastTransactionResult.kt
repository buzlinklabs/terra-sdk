package money.terra.model.transaction

interface BroadcastTransactionResult {
    val height: String
    val txhash: String
    val code: Int?
}

class BroadcastTransactionSyncResult(
    override val height: String,
    override val txhash: String,
    override val code: Int?,
    val rawLog: String,
    val logs: List<BroadcastTransactionLog>?
) : BroadcastTransactionResult

class BroadcastTransactionAsyncResult(
    override val height: String,
    override val txhash: String,
    override val code: Int?
) : BroadcastTransactionResult

class BroadcastTransactionBlockResult(
    override val height: String,
    override val txhash: String,
    override val code: Int?,
    val rawLog: String,
    val logs: List<BroadcastTransactionLog>?,
    val gasUsed: String,
    val gasWanted: String?,
    val events: List<BroadcastTransactionEvent>?
) : BroadcastTransactionResult