package money.terra.bip

import org.bitcoinj.core.Bech32 as Bech32Lib

actual object Bech32 {

    actual fun encode(hrp: String, data: ByteArray): String = Bech32Lib.encode(hrp, data)

    actual fun toWords(data: ByteArray): ByteArray = Bech32Lib.toWords(data)
}