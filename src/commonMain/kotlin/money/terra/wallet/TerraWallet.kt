package money.terra.wallet

import kr.jadekim.common.util.encoder.HEX
import kr.jadekim.common.util.encoder.asHex
import kr.jadekim.common.util.encoder.asHexString
import kr.jadekim.common.util.ext.bytes
import kr.jadekim.common.util.hash.SHA_256
import money.terra.bip.Bech32
import money.terra.bip.Bip32
import money.terra.bip.Mnemonic
import money.terra.client.TerraClient
import money.terra.hash.RIPEMD160
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface TerraWallet : PublicTerraWallet {

    companion object {

        const val ACCOUNT_HRP = "terra"
        const val ACCOUNT_PUBLIC_KEY_HRP = "terrapub"
        const val VALIDATOR_HRP = "terravaloper"
        const val VALIDATOR_PUBLIC_KEY_HRP = "terravaloperpub"

        const val COIN_TYPE = 330
        const val OLD_COIN_TYPE = 118

        internal val BECH32_PUBLIC_KEY_DATA_PREFIX = "eb5ae98721".bytes
        internal val Int.hard
            get() = this or -0x80000000

        @JvmStatic
        @JvmOverloads
        fun create(account: Int = 0, index: Int = 0): Pair<TerraWallet, String> {
            val mnemonic = Mnemonic.generate()

            return from(mnemonic, account, index) to mnemonic
        }

        @JvmStatic
        @JvmOverloads
        fun from(mnemonic: String, account: Int = 0, index: Int = 0, coinType: Int = COIN_TYPE): TerraWallet {
            val seed = Mnemonic.seedFrom(mnemonic)
            val hdPathLuna = intArrayOf(44.hard, coinType.hard, account.hard, 0, index)
            val keyPair = Bip32.keyPairFrom(seed, hdPathLuna)

            return TerraWalletImpl(keyPair.publicKey, keyPair.privateKey)
        }

        @JvmStatic
        fun isValidAccountAddress(address: String): Boolean {
            return try {
                val (hrp, _) = Bech32.decode(address)
                hrp == ACCOUNT_HRP
            } catch (e: Exception) {
                false
            }
        }
    }

    val publicKey: ByteArray
    val privateKey: ByteArray

    val privateKeyHex: String
    val publicKeyHex: String

    val accountAddress: String
    val accountPublicKey: String
    val validatorAddress: String
    val validatorPublicKey: String
}

open class TerraWalletImpl(
    override val publicKey: ByteArray,
    override val privateKey: ByteArray
) : TerraWallet {

    override val privateKeyHex: String by lazy { privateKey.asHexString }
    override val publicKeyHex: String by lazy { publicKey.asHexString }

    override val address: String by lazy { accountAddress } // accountAddress alias
    override val accountAddress: String by lazy { Bech32.encode(TerraWallet.ACCOUNT_HRP, baseAddress) }
    override val accountPublicKey: String by lazy {
        Bech32.encode(
            TerraWallet.ACCOUNT_PUBLIC_KEY_HRP,
            TerraWallet.BECH32_PUBLIC_KEY_DATA_PREFIX + publicKey.asHex
        )
    }
    override val validatorAddress: String by lazy { Bech32.encode(TerraWallet.VALIDATOR_HRP, baseAddress) }
    override val validatorPublicKey: String by lazy {
        Bech32.encode(
            TerraWallet.VALIDATOR_PUBLIC_KEY_HRP,
            TerraWallet.BECH32_PUBLIC_KEY_DATA_PREFIX + publicKey.asHex
        )
    }

    private val baseAddress: ByteArray by lazy {
        val hashed = RIPEMD160.hash(SHA_256.hash(publicKey))

        Bech32.toWords(hashed)
    }
}

@Suppress("FunctionName")
fun TerraWallet(publicKey: String, privateKey: String): TerraWallet {
    return TerraWallet(HEX.decode(publicKey), HEX.decode(privateKey))
}

@Suppress("FunctionName")
fun TerraWallet(publicKey: ByteArray, privateKey: ByteArray): TerraWallet {
    val publicKeyBytes = ByteArray(33)
    val privateKeyBytes = ByteArray(33)

    publicKey.copyInto(publicKeyBytes)
    privateKey.copyInto(privateKeyBytes)

    return TerraWalletImpl(publicKeyBytes, privateKeyBytes)
}

suspend fun TerraWallet.connect(client: TerraClient) =
    ConnectedTerraWallet(this, client.getAccountNumber(address), client)