package money.terra.wallet

import money.terra.Network
import money.terra.client.TerraClient
import money.terra.model.Coin

open class ConnectedPublicTerraWallet(
    wallet: PublicTerraWallet,
    internal val client: TerraClient
) : PublicTerraWallet by wallet {

    val network: Network = client.network

    suspend fun getBalances(): List<Coin> = client.getAccountBalances(address)
}