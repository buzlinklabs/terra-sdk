package money.terra.transaction.proposal

@Deprecated("")
data class ParameterChangeProposal(
    val title: String,
    val description: String,
    val changes: List<Change> //TypeWrapper 필요?
) : Proposal("gov/ParameterChangeProposal") {

    data class Change(
        val subspace: String,
        val key: String,
        val subkey: String,
        val value: String
    )
}