package money.terra.transaction.proposal

import money.terra.model.Coin

data class CommunityPoolSpendProposal(
    val title: String,
    val description: String,
    val recipient: String,
    val amount: List<Coin>
) : Proposal("distribution/CommunityPoolSpendProposal")