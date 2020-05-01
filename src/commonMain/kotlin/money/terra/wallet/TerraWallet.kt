package money.terra.wallet

import kr.jadekim.common.util.encoder.asHex
import kr.jadekim.common.util.encoder.asHexString
import kr.jadekim.common.util.ext.bytes
import kr.jadekim.common.util.hash.SHA_256
import money.terra.ProvidedNetwork
import money.terra.bip.Bech32
import money.terra.bip.Bip32
import money.terra.bip.Mnemonic
import money.terra.client.http.TerraHttpClient
import money.terra.hash.RIPEMD160

open class TerraWallet(
    val publicKey: ByteArray,
    val privateKey: ByteArray
) {

    companion object {

        const val ACCOUNT_PREFIX = "terra"
        const val ACCOUNT_PUBLIC_KEY_PREFIX = "terrapub"
        const val VALIDATOR_PREFIX = "terravaloper"
        const val VALIDATOR_PUBLIC_KEY_PREFIX = "terravaloperpub"
        val BECH32_PUBLIC_KEY_DATA_PREFIX = "eb5ae98721".bytes

        const val COIN_TYPE = 330
        const val OLD_COIN_TYPE = 118

        private val Int.hard
            get() = this or -0x80000000

        fun create(account: Int = 0, index: Int = 0): Pair<TerraWallet, String> {
            val mnemonic = Mnemonic.generate()

            return from(mnemonic, account, index) to mnemonic
        }

        fun from(mnemonic: String, account: Int = 0, index: Int = 0, coinType: Int = COIN_TYPE): TerraWallet {
            val seed = Mnemonic.seedFrom(mnemonic)
            val hdPathLuna = intArrayOf(44.hard, coinType.hard, account.hard, 0, index)
            val keyPair = Bip32.keyPairFrom(seed, hdPathLuna)

            return TerraWallet(keyPair.publicKey, keyPair.privateKey)
        }
    }

    val privateKeyHex: String by lazy { privateKey.asHexString }
    val publicKeyHex: String by lazy { publicKey.asHexString }

    val address: String by lazy { accountAddress } // accountAddress alias
    val accountAddress: String by lazy { Bech32.encode(ACCOUNT_PREFIX, baseAddress) }
    val accountPublicKey: String by lazy {
        Bech32.encode(
            ACCOUNT_PUBLIC_KEY_PREFIX,
            BECH32_PUBLIC_KEY_DATA_PREFIX + publicKey.asHex
        )
    }
    val validatorAddress: String by lazy { Bech32.encode(VALIDATOR_PREFIX, baseAddress) }
    val validatorPublicKey: String by lazy {
        Bech32.encode(
            VALIDATOR_PUBLIC_KEY_PREFIX,
            BECH32_PUBLIC_KEY_DATA_PREFIX + publicKey.asHex
        )
    }

    private val baseAddress: ByteArray by lazy {
        val hashed = RIPEMD160.hash(SHA_256.hash(publicKey))

        Bech32.toWords(hashed)
    }

    suspend fun connect(network: ProvidedNetwork) = ConnectedTerraWallet(
        publicKey,
        privateKey,
        network
    ).apply {
        connect()
    }

    suspend fun connect(httpClient: TerraHttpClient) = ConnectedTerraWallet(
        publicKey,
        privateKey,
        httpClient
    ).apply {
        connect()
    }
}