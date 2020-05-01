package money.terra.client

import money.terra.Network
import money.terra.ProvidedNetwork

interface TerraServer {
    val host: String
    val network: Network
}

@Suppress("FunctionName")
fun TerraServer(host: String, network: Network) = object: TerraServer {

    override val host: String = host

    override val network: Network = network
}

enum class ProvidedTerraServer(
    override val host: String,
    override val network: Network,
    val hasCache: Boolean = false
) : TerraServer {
    MAINNET("lcd.terra.dev", ProvidedNetwork.COLUMBUS_3),
    MAINNET_FCD("fcd.terra.dev", ProvidedNetwork.COLUMBUS_3, true),
    SOJU("soju-lcd.terra.dev", ProvidedNetwork.SOJU_0014),
    SOJU_FCD("soju-fcd.terra.dev", ProvidedNetwork.SOJU_0014, true),
    VODKA("vodka-lcd.terra.dev", ProvidedNetwork.VODKA_0001),
    VODKA_FCD("vodka-fcd.terra.dev", ProvidedNetwork.VODKA_0001, true);

    companion object {
        fun of(network: ProvidedNetwork, hasCache: Boolean = true) = values().first {
            it.network == network && it.hasCache == hasCache
        }
    }
}