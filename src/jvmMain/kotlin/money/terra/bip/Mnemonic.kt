package money.terra.bip

import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom

actual object Mnemonic {

    private val RANDOM = SecureRandom()

    actual fun generate(): String = ByteArray(32)
        .apply { RANDOM.nextBytes(this) }
        .let { MnemonicUtils.generateMnemonic(it) }

    actual fun seedFrom(mnemonic: String, passphrase: String?): ByteArray {
        return MnemonicUtils.generateSeed(mnemonic, passphrase)
    }
}