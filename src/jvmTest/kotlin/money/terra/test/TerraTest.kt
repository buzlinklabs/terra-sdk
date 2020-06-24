package money.terra.test

import kotlinx.coroutines.runBlocking
import kr.jadekim.common.util.encoder.asBase64String
import money.terra.ProvidedNetwork
import money.terra.Terra
import money.terra.client.http.api.TransactionApi
import money.terra.model.Coin
import money.terra.model.Fee
import money.terra.transaction.TransactionBuilder
import money.terra.transaction.message.ExecuteContract
import money.terra.transaction.message.InstantiateContract
import money.terra.transaction.message.SendMessage
import money.terra.util.jsonMapper
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
            terra = runBlocking { Terra.connect(TerraWallet.from(MNEMONIC), ProvidedNetwork.SOJU_0014) }
            transactionApi = terra.wallet.httpClient.transaction()
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

    @Nested
    @DisplayName("Contract 생성")
    inner class InstantiateContractTest : BaseBroadcastTest(terra) {

        val coins = listOf(Coin("uluna", "100000"))
        val fee = Fee("200000", listOf(Coin("uluna", "50")))

        override fun TransactionBuilder.setup() {
            fee = this@InstantiateContractTest.fee

            InstantiateContract(terra.wallet.address, "1", "e30=", coins).addThis()
        }
    }

    @Nested
    @DisplayName("Contract 실행")
    inner class ExecuteContractTest : BaseBroadcastTest(terra) {

        val fee = Fee("200000", listOf(Coin("uluna", "50")))

        override fun TransactionBuilder.setup() {
            fee = this@ExecuteContractTest.fee

            val msgObj = mapOf(
                "create_campaign" to mapOf(
                    "campaign_id" to "c1",
                    "redirect_url" to "https://url",
                    "reward_amount" to 142,
                    "fee" to 3,
                    "denom" to "uluna",
                    "distribute_ratio" to listOf(40, 30, 20, 10),
                    "meta" to "meta"
                )
            )
            val msg = jsonMapper.writeValueAsBytes(msgObj).asBase64String
            ExecuteContract(
                terra.wallet.address,
                "terra15gfn3tmx589z9l0h0n6mk0sunkn3864xw89v63",
                msg,
                emptyList()
            ).addThis()
        }
    }
}