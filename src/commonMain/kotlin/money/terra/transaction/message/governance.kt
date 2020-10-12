package money.terra.transaction.message

import money.terra.model.Coin
import money.terra.model.TypeWrapper
import money.terra.transaction.proposal.Proposal

data class SubmitProposalMessage(
    val content: TypeWrapper<Proposal>,
    val initialDeposit: List<Coin>,
    val proposer: String
) : Message("gov/MsgSubmitProposal") {

    constructor(
        content: Proposal,
        initialDeposit: List<Coin>,
        proposer: String
    ) : this(
        content.wrapper(),
        initialDeposit,
        proposer
    )
}

data class DepositProposalMessage(
    val proposalId: String,
    val depositor: String,
    val amount: List<Coin>
) : Message("gov/MsgDeposit")

data class VoteProposalMessage(
    val proposalId: String,
    val voter: String,
    val option: String
) : Message("gov/MsgVote")

