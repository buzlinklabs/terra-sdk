package money.terra.model.transaction

import money.terra.model.Transaction

interface BroadcastTransactionRequest {
    val tx: Transaction<*>
    val mode: String
}

class BroadcastTransactionSyncRequest(
    override val tx: Transaction<*>
) : BroadcastTransactionRequest {
    override val mode = "sync"
}

class BroadcastTransactionAsyncRequest(
    override val tx: Transaction<*>
) : BroadcastTransactionRequest {
    override val mode = "async"
}

class BroadcastTransactionBlockRequest(
    override val tx: Transaction<*>
) : BroadcastTransactionRequest {
    override val mode = "block"
}