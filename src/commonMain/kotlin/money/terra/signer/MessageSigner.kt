package money.terra.signer

import kr.jadekim.common.util.hash.SHA_256
import money.terra.Network
import money.terra.bip.Bip32
import money.terra.model.Signature
import money.terra.model.Transaction
import money.terra.transaction.message.Message
import money.terra.util.toSortedJson
import money.terra.wallet.ConnectedTerraWallet

class MessageSigner(
    val wallet: ConnectedTerraWallet,
    val network: Network
) {

    fun <T : Message> sign(transaction: Transaction<T>, sequence: String): Transaction<T> {
        if (transaction.fee == null) {
            throw IllegalArgumentException("non-null field fee")
        }

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