package money.terra.signer

import kr.jadekim.common.util.hash.SHA_256
import money.terra.ProvidedNetwork
import money.terra.client.http.TerraHttpClient
import money.terra.bip.Bip32
import money.terra.model.Signature
import money.terra.model.Transaction
import money.terra.transaction.message.Message
import money.terra.util.toSortedJson
import money.terra.wallet.ConnectedTerraWallet

class MessageSigner(
    val wallet: ConnectedTerraWallet,
    httpClient: TerraHttpClient
) {

    val network = httpClient.network

    constructor(wallet: ConnectedTerraWallet, network: ProvidedNetwork) : this(wallet, TerraHttpClient(network))

    init {
        if (!wallet.isConnected) {
            throw IllegalStateException("Not connected")
        }
    }

    fun <T : Message> sign(transaction: Transaction<T>, sequence: String): Transaction<T> {
        val signMessage = SignMessage(
            sequence,
            wallet.accountNumber,
            network.chainId,
            transaction.fee,
            transaction.msg,
            transaction.memo
        )

        val json = signMessage.toSortedJson()
        val signMessageHash = SHA_256.hash(json)
        val signature = Bip32.sign(signMessageHash, wallet.privateKey)
        val signatureObject = Signature(signature, wallet.publicKey)

        return transaction.copy(signatures = listOf(signatureObject))
    }
}