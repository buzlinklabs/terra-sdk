package money.terra.transaction.proposal

data class TextProposal(
    val title: String,
    val description: String
) : Proposal("gov/TextProposal")