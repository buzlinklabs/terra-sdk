package money.terra.bip

expect object Bech32 {

    fun encode(hrp: String, data: ByteArray): String

    fun toWords(data: ByteArray): ByteArray
}