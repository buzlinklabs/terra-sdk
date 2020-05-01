package money.terra.model.transaction

data class BroadcastTransactionLog(
    val msgIndex: Int,
    val success: Boolean,
    val log: String,
    val events: List<BroadcastTransactionEvent>
)