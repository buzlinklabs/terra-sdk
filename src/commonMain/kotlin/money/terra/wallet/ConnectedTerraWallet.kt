package money.terra.wallet

import money.terra.ProvidedNetwork
import money.terra.client.http.TerraHttpClient
import money.terra.model.Transaction
import money.terra.signer.MessageSigner
import money.terra.transaction.message.Message

class ConnectedTerraWallet(
    private val wallet: TerraWallet,
    private val httpClient: TerraHttpClient
) : ConnectedPublicTerraWallet(wallet, httpClient), TerraWallet by wallet {

    override val address: String
        get() = wallet.address

    lateinit var accountNumber: String
        private set

    private val authApi = httpClient.auth()

    private lateinit var signer: MessageSigner

    constructor(
        wallet: TerraWallet,
        network: ProvidedNetwork
    ) : this(wallet, TerraHttpClient(network))

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
        val sequence = getSequence()

        return sign(transaction, sequence) to sequence
    }

    private suspend fun getSequence(): String = authApi.getAccountInfo(address).result.value.sequence
}