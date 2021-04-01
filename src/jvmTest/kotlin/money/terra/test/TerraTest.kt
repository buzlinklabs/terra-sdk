package money.terra.test

import kotlinx.coroutines.runBlocking
import money.terra.Terra
import money.terra.client.fcd.TerraFcdClient
import money.terra.model.Coin
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.message.SendMessage
import money.terra.util.provider.FcdGasPriceProvider
import money.terra.wallet.TerraWallet
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TerraTest {

    companion object {

        private lateinit var terra: Terra

        @BeforeAll
        @JvmStatic
        fun init() {
            terra = runBlocking { Terra.connect(TerraWallet.from(MNEMONIC), HTTP_CLIENT) }
            terra.gasPriceProvider = FcdGasPriceProvider(TerraFcdClient(NETWORK, "https://tequila-fcd.terra.dev")).apply {
                filterGasPrices = {
                    filter { it.denom == "uluna" }
                }
            }
        }
    }

    @Test
    @DisplayName("Transaction 조회")
    fun testGetTransaction() {
        val transactionHash = "C716BF66B3CBFA771F6969F0215062B7AAE741DFA51CB97819B1F96485CEEFA6"

        runBlocking { terra.client.getByHash(transactionHash) }
    }

    @Nested
    @DisplayName("코인 송금")
    inner class SendCoinTest : BaseBroadcastTest(terra) {

        val coins = listOf(Coin("uluna", "1"))

        override fun TransactionBuilder.setup() {
            with { SendMessage(terra.wallet.address, terra.wallet.address, coins) }
        }
    }
}