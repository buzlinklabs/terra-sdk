package money.terra.transaction.message

data class UnjailMessage(
    val address: String
) : Message("cosmos/MsgUnjail")