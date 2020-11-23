package money.terra.client

import money.terra.Network
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.BroadcastTransactionAsyncResult
import money.terra.model.transaction.BroadcastTransactionBlockResult
import money.terra.model.transaction.BroadcastTransactionSyncResult
import money.terra.wallet.ConnectedPublicTerraWallet
import money.terra.wallet.ConnectedTerraWallet
import money.terra.wallet.PublicTerraWallet
import money.terra.wallet.TerraWallet

interface TerraClient {

    val network: Network

    suspend fun wallet(address: String): ConnectedPublicTerraWallet

    suspend fun wallet(publicKey: ByteArray, privateKey: ByteArray): ConnectedTerraWallet

    suspend fun connect(wallet: PublicTerraWallet): ConnectedPublicTerraWallet

    suspend fun connect(wallet: TerraWallet): ConnectedTerraWallet

    suspend fun getAccountBalances(address: String): List<Coin>

    suspend fun getAccountNumber(address: String): String

    suspend fun getSequence(address: String): Long

    suspend fun getByHash(transactionHash: String): TransactionQueryResult

    suspend fun estimateFee(transaction: Transaction<*>, gasAdjustment: String, gasPrices: List<Coin>): Fee

    suspend fun broadcastSync(transaction: Transaction<*>): BroadcastTransactionSyncResult

    suspend fun broadcastAsync(transaction: Transaction<*>): BroadcastTransactionAsyncResult

    suspend fun broadcastBlock(transaction: Transaction<*>): BroadcastTransactionBlockResult
}