package money.terra.client.lcd

import money.terra.Network
import money.terra.client.HttpClient
import money.terra.client.TerraClient
import money.terra.client.lcd.api.*
import money.terra.model.Coin
import money.terra.model.Transaction
import money.terra.model.transaction.BroadcastTransactionAsyncRequest
import money.terra.model.transaction.BroadcastTransactionBlockRequest
import money.terra.model.transaction.BroadcastTransactionSyncRequest
import money.terra.model.transaction.EstimateFeeRequest
import money.terra.wallet.*

open class TerraLcdClient(
    override val network: Network,
    serverUrl: String,
    timeoutMillis: Long = 10000
) : TerraClient {

    val lcdServer = HttpClient(serverUrl, timeoutMillis)

    val authApi = AuthApi(lcdServer)
    val bankApi = BankApi(lcdServer)
    val marketApi = MarketApi(lcdServer)
    val transactionApi = TransactionApi(lcdServer)
    val wasmApi = WasmApi(lcdServer)

    override suspend fun wallet(address: String) = PublicTerraWallet(address).connect(this)

    override suspend fun wallet(publicKey: ByteArray, privateKey: ByteArray) =
        TerraWallet(publicKey, privateKey).connect(this)

    override suspend fun connect(wallet: PublicTerraWallet): ConnectedPublicTerraWallet = wallet.connect(this)

    override suspend fun connect(wallet: TerraWallet): ConnectedTerraWallet = wallet.connect(this)

    override suspend fun getAccountBalances(address: String) = bankApi.getAccountBalances(address).result

    override suspend fun getAccountNumber(address: String) = authApi.getAccountInfo(address).result.value.accountNumber

    override suspend fun getSequence(address: String) = authApi.getAccountInfo(address).result.value.sequence.toLong()

    override suspend fun getByHash(transactionHash: String) = transactionApi.getByHash(transactionHash)

    override suspend fun estimateFee(
        transaction: Transaction<*>,
        gasAdjustment: String,
        gasPrices: List<Coin>
    ) = transactionApi
        .estimateFeeAndGas(EstimateFeeRequest(transaction, gasAdjustment, gasPrices))
        .result.asFee

    override suspend fun broadcastSync(transaction: Transaction<*>) = transactionApi
        .broadcastSignedTransaction(BroadcastTransactionSyncRequest(transaction))

    override suspend fun broadcastAsync(transaction: Transaction<*>) = transactionApi
        .broadcastSignedTransaction(BroadcastTransactionAsyncRequest(transaction))

    override suspend fun broadcastBlock(transaction: Transaction<*>) = transactionApi
        .broadcastSignedTransaction(BroadcastTransactionBlockRequest(transaction))
}