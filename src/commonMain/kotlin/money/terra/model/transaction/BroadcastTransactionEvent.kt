package money.terra.model.transaction

data class BroadcastTransactionEvent(
    val type: String,
    val attributes: List<Attribute>
) {

    data class Attribute(
        val key: String,
        val value: String
    )
}