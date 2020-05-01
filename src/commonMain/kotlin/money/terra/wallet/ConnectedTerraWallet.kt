package money.terra.wallet

import money.terra.Network
import money.terra.ProvidedNetwork
import money.terra.client.http.TerraHttpClient
import money.terra.model.Transaction
import money.terra.signer.MessageSigner
import money.terra.transaction.message.Message

class ConnectedTerraWallet(
    publicKey: ByteArray,
    privateKey: ByteArray,
    private val httpClient: TerraHttpClient
) : TerraWallet(publicKey, privateKey) {

    val network: Network = httpClient.network

    var isConnected = false
        private set

    lateinit var accountNumber: String
        private set

    private val authApi = httpClient.auth()

    private lateinit var signer: MessageSigner

    constructor(
        publicKey: ByteArray,
        privateKey: ByteArray,
        network: ProvidedNetwork
    ) : this(publicKey, privateKey, TerraHttpClient(network))

    suspend fun connect() {
        accountNumber = authApi.getAccountInfo(address).result.value.accountNumber

        isConnected = true

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