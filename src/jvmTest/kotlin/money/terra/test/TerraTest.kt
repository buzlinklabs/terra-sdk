package money.terra.test

import kotlinx.coroutines.runBlocking
import money.terra.Terra
import money.terra.client.http.api.TransactionApi
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.message.SendMessage
import money.terra.wallet.TerraWallet
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TerraTest {

    companion object {

        private lateinit var terra: Terra
        private lateinit var transactionApi: TransactionApi

        @BeforeAll
        @JvmStatic
        fun init() {
            terra = runBlocking { Terra.connect(TerraWallet.from(MNEMONIC), NETWORK) }
            transactionApi = terra.httpClient.transaction()
        }
    }

    @Test
    @DisplayName("Transaction 조회")
    fun testGetTransaction() {
        val transactionHash = "15B7E25FA9DD63A241673B587215C1166BE359D3AEB30C7F32855D5D61987042"

        runBlocking { transactionApi.getByHash(transactionHash) }
    }

    @Nested
    @DisplayName("코인 송금")
    inner class SendCoinTest : BaseBroadcastTest(terra) {

        val coins = listOf(Coin("uluna", "100000"))
        val fee = Fee("200000", listOf(Coin("uluna", "50")))

        override fun TransactionBuilder.setup() {
            fee = this@SendCoinTest.fee

            SendMessage(ADDRESS, TEST_ADDRESS, coins).addThis()
        }
    }
}