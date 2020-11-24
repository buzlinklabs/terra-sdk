package money.terra.util.provider

import kr.jadekim.common.util.currentTimeMillis
import money.terra.client.fcd.TerraFcdClient
import money.terra.model.Coin
import money.terra.model.Transaction

interface GasPriceProvider {

    suspend fun get(transaction: Transaction<*>? = null): List<Coin>
}

class FcdGasPriceProvider(
    private val fcdClient: TerraFcdClient
) : GasPriceProvider {

    var filterGasPrices: List<Coin>.(Transaction<*>?) -> List<Coin> = { this }

    var cacheLifeTimeMillis: Long = 60 * 1000 //a minute

    private var lastPrices: List<Coin>? = null
    private var lastUpdateTime: Long = 0

    override suspend fun get(transaction: Transaction<*>?): List<Coin> {
        var gasPrices = lastPrices

        if (lastUpdateTime + cacheLifeTimeMillis < currentTimeMillis || gasPrices == null) {
            gasPrices = fcdClient.getGasPrices()
            lastPrices = gasPrices
        }

        return gasPrices.filterGasPrices(transaction)
    }
}