package money.terra.client.fcd

import money.terra.Network
import money.terra.client.HttpClient
import money.terra.client.TerraClient
import money.terra.client.fcd.api.TransactionApi
import money.terra.client.lcd.TerraLcdClient
import money.terra.model.Coin

class TerraFcdClient(
    network: Network,
    serverUrl: String,
    timeoutMillis: Long = 10000,
    val lcdClient: TerraLcdClient = TerraLcdClient(network, serverUrl, timeoutMillis)
) : TerraClient by lcdClient {

    val fcdServer = HttpClient(serverUrl, timeoutMillis)

    val transactionApi = TransactionApi(fcdServer)

    suspend fun getGasPrices(): List<Coin> = transactionApi.getGasPrices().map { Coin(it.key, it.value) }
}