package money.terra.transaction.message

import money.terra.model.Coin

data class DelegateMessage(
    val delegatorAddress: String,
    val validatorAddress: String,
    val amount: Coin
) : Message("staking/MsgDelegate")

data class UndelegateMessage(
    val delegatorAddress: String,
    val validatorAddress: String,
    val amount: Coin
) : Message("staking/MsgUndelegate")

data class BeginRedelegateMessage(
    val delegatorAddress: String,
    val validatorSrcAddress: String,
    val validatorDstAddress: String,
    val amount: Coin
) : Message("staking/MsgBeginRedelegate")

@Deprecated("")
data class EditValidatorMessage(
    val Description: DescriptionData, //첫글자 대문자?
    val address: String,
    val commissionRate: String?,
    val minSelfDelegation: String?
) {

    data class DescriptionData(
        val moniker: String,
        val identity: String,
        val website: String,
        val details: String
    )
}

@Deprecated("")
data class CreateValidatorMessage(
    val Description: DescriptionData, //첫글자 대문자?
    val commission: Commission,
    val minSelfDelegation: String,
    val delegatorAddress: String,
    val validatorAddress: String,
    val pubkey: String,
    val value: Coin
) {

    data class DescriptionData(
        val moniker: String,
        val identity: String,
        val website: String,
        val details: String
    )

    data class Commission(
        val rate: String,
        val maxRate: String,
        val maxChangeRate: String
    )
}