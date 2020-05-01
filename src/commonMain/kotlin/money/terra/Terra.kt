package money.terra

import money.terra.client.TerraServer
import money.terra.client.http.TerraHttpClient
import money.terra.model.Transaction
import money.terra.model.transaction.*
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet

class Terra(
    val wallet: ConnectedTerraWallet,
    val httpClient: TerraHttpClient
) {

    companion object {

        suspend fun connect(wallet: TerraWallet, network: Network, server: TerraServer): Terra {
            val liteClient = TerraHttpClient(network, server)

            return Terra(wallet.connect(liteClient), liteClient)
        }

        suspend fun connect(wallet: TerraWallet, network: ProvidedNetwork): Terra {
            val liteClient = TerraHttpClient(network)

            return Terra(wallet.connect(liteClient), liteClient)
        }
    }

    private val transactionApi = httpClient.transaction()

    suspend fun broadcast(transaction: Transaction<*>) = broadcastAsync(transaction)

    suspend fun broadcastSync(transaction: Transaction<*>): BroadcastTransactionSyncResult {
        val signedTransaction = if (transaction.isSigned) transaction else wallet.sign(transaction).first
        val broadcastRequest = BroadcastTransactionSyncRequest(signedTransaction)

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastAsync(transaction: Transaction<*>): BroadcastTransactionAsyncResult {
        val signedTransaction = if (transaction.isSigned) transaction else wallet.sign(transaction).first
        val broadcastRequest = BroadcastTransactionAsyncRequest(signedTransaction)

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
    }

    suspend fun broadcastBlock(transaction: Transaction<*>): BroadcastTransactionBlockResult {
        val signedTransaction = if (transaction.isSigned) transaction else wallet.sign(transaction).first
        val broadcastRequest = BroadcastTransactionBlockRequest(signedTransaction)

        return transactionApi.broadcastSignedTransaction(broadcastRequest)
    }
}