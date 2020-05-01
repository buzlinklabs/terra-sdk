package money.terra.test

import kotlinx.coroutines.runBlocking
import money.terra.bip.Mnemonic
import org.junit.jupiter.api.Test
import money.terra.wallet.TerraWallet

class WalletTest {

    @Test
    fun testCreateMnemonic() {
        val mnemonic = Mnemonic.generate()

        assert(mnemonic.isNotBlank())
        assert(mnemonic.split(" ").size == 24)
        assert(mnemonic.all { (it.isLetter() && it.isLowerCase()) || it == ' ' })
    }

    @Test
    fun testCreateWallet() {
        val wallet = TerraWallet.from(MNEMONIC)

        assert(wallet.privateKeyHex.equals(PRIVATE_KEY, true))
        assert(wallet.publicKeyHex.equals(PUBLIC_KEY, true))
        assert(wallet.address == ADDRESS)
    }

    @Test
    fun testConnectWallet() {
        val wallet = runBlocking {
            TerraWallet.from(MNEMONIC).connect(
                NETWORK
            )
        }

        assert(wallet.isConnected)
        assert(wallet.accountNumber == SOJU_ACCOUNT_NUMBER)
    }
}