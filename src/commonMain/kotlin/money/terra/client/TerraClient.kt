package money.terra.client

import money.terra.Network
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.model.Transaction
import money.terra.model.TransactionQueryResult
import money.terra.model.transaction.BroadcastTransactionAsyncResult
import money.terra.model.transaction.BroadcastTransactionBlockResult
import money.terra.model.transaction.BroadcastTransactionSyncResult
import money.terra.wallet.*

interface TerraClient {

    val network: Network

    suspend fun getTaxCapacity(denom: String): Long

    suspend fun getTaxRate(): String

    suspend fun getAccountBalances(address: String): List<Coin>

    suspend fun getAccountNumber(address: String): String

    suspend fun getSequence(address: String): Long

    suspend fun getByHash(transactionHash: String): TransactionQueryResult

    suspend fun estimateFee(transaction: Transaction<*>, gasAdjustment: String, gasPrices: List<Coin>): Fee

    suspend fun broadcastSync(transaction: Transaction<*>): BroadcastTransactionSyncResult

    suspend fun broadcastAsync(transaction: Transaction<*>): BroadcastTransactionAsyncResult

    suspend fun broadcastBlock(transaction: Transaction<*>): BroadcastTransactionBlockResult
}

fun TerraClient.wallet(address: String) = PublicTerraWallet(address).connect(this)

fun TerraClient.connect(wallet: PublicTerraWallet) = wallet.connect(this)

suspend fun TerraClient.wallet(
    publicKey: ByteArray,
    privateKey: ByteArray
) = TerraWallet(publicKey, privateKey).connect(this)

suspend fun TerraClient.connect(wallet: TerraWallet) = wallet.connect(this)