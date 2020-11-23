package money.terra.wallet

import money.terra.client.TerraClient
import money.terra.model.Transaction
import money.terra.signer.MessageSigner
import money.terra.transaction.message.Message

class ConnectedTerraWallet(
    private val wallet: TerraWallet,
    val accountNumber: String,
    client: TerraClient
) : ConnectedPublicTerraWallet(wallet, client), TerraWallet by wallet {

    override val address: String
        get() = wallet.address

    private val signer: MessageSigner = MessageSigner(this, client.network)

    fun <T : Message> sign(transaction: Transaction<T>, sequence: Long): Transaction<T> {
        return signer.sign(transaction, sequence.toString())
    }
}