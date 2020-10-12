package money.terra.transaction.message

import money.terra.model.Coin

data class SetWithdrawAddressMessage(
    val delegatorAddress: String,
    val withdrawAddress: String
) : Message("distribution/MsgModifyWithdrawAddress")

data class WithdrawDelegatorRewardMessage(
    val delegatorAddress: String,
    val validatorAddress: String
) : Message("distribution/MsgWithdrawDelegationReward")

data class WithdrawValidatorCommissionMessage(
    val validatorAddress: String
) : Message("distribution/MsgWithdrawValidatorCommission")

data class FundCommunityPoolMessage(
    val amount: List<Coin>,
    val depositor: String
) : Message("distribution/MsgFundCommunityPool")