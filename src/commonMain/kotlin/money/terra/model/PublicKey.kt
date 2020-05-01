package money.terra.model

data class PublicKey(
    val value: String,
    val type: String = "tendermint/PubKeySecp256k1"
)