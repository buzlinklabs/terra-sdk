package money.terra

import money.terra.client.TerraServer
import money.terra.client.http.TerraHttpClient
import money.terra.model.Transaction
import money.terra.model.transaction.*
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect

class Terra(
    val wallet: ConnectedTerraWallet
) {

    companion object {

        suspend fun connect(wallet: TerraWallet, network: Network, server: TerraServer): Terra {
            return connect(wallet, TerraHttpClient(network, server))
        }

        suspend fun connect(wallet: TerraWallet, network: ProvidedNetwork): Terra {
            return connect(wallet, TerraHttpClient(network))
        }

        suspend fun connect(wallet: TerraWallet, liteClient: TerraHttpClient): Terra {
            return Terra(wallet.connect(liteClient))
        }
    }

    private val httpClient = wallet.httpClient

    private val transactionApi = httpClient.transaction()

    suspend fun broadcast(transaction: Transaction<*>) = broadcastSync(transaction)

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