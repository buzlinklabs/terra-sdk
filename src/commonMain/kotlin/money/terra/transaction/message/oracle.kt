package money.terra.transaction.message

data class ExchangeRatePrevoteMessage(
    val hash: String,
    val denom: String,
    val feeder: String,
    val validator: String
) : Message("oracle/MsgExchangeRatePrevote")

data class ExchangeRateVoteMessage(
    val exchangeRate: String,
    val salt: String,
    val denom: String,
    val feeder: String,
    val validator: String
) : Message("oracle/MsgExchangeRateVote")

data class DelegateFeedConsentMessage(
    val operator: String,
    val delegate: String
) : Message("oracle/MsgDelegateFeedConsent")

data class AggregateExchangeRatePrevoteMessage(
    val hash: String,
    val feeder: String,
    val validator: String
) : Message("oracle/MsgAggregateExchangeRatePrevote")

data class AggregateExchangeRateVote(
    val salt: String,
    val exchangeRates: String,
    val feeder: String,
    val validator: String
) : Message("oracle/MsgAggregateExchangeRateVote")

