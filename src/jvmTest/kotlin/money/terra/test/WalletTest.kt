package money.terra.test

import kotlinx.coroutines.runBlocking
import money.terra.bip.Mnemonic
import money.terra.wallet.TerraWallet
import money.terra.wallet.connect
import org.junit.jupiter.api.Test

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
        val wallet = runBlocking { TerraWallet.from(MNEMONIC).connect(HTTP_CLIENT) }

        assert(wallet.accountNumber.isNotBlank())
    }
}