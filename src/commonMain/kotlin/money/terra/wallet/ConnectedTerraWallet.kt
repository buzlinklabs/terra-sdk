package money.terra.wallet

import money.terra.client.http.TerraHttpClient
import money.terra.model.Transaction
import money.terra.signer.MessageSigner
import money.terra.transaction.message.Message

class ConnectedTerraWallet(
    private val wallet: TerraWallet,
    httpClient: TerraHttpClient
) : ConnectedPublicTerraWallet(wallet, httpClient), TerraWallet by wallet {

    override val address: String
        get() = wallet.address

    lateinit var accountNumber: String
        private set

    private val authApi = httpClient.auth()

    private lateinit var signer: MessageSigner

    override suspend fun connect() {
        accountNumber = authApi.getAccountInfo(address).result.value.accountNumber

        super.connect()

        signer = MessageSigner(this, httpClient)
    }

    fun <T : Message> sign(transaction: Transaction<T>, sequence: String): Transaction<T> {
        if (!isConnected) {
            throw IllegalStateException("Not connected")
        }

        return signer.sign(transaction, sequence)
    }

    suspend fun <T : Message> sign(transaction: Transaction<T>): Pair<Transaction<T>, String> {
        val sequence = fetchSequence()

        return sign(transaction, sequence) to sequence
    }

    suspend fun fetchSequence(): String = authApi.getAccountInfo(address).result.value.sequence
}