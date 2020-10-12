package money.terra.transaction.proposal

data class TaxRateUpdateProposal(
    val title: String,
    val description: String,
    val taxRate: String
) : Proposal("treasury/TaxRateUpdateProposal")

data class RewardWeightUpdateProposal(
    val title: String,
    val description: String,
    val rewardWeight: String
) : Proposal("treasury/RewardWeightUpdateProposal")