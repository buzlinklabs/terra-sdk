# Terra Kotlin SDK

## Support platform
* JVM

## Implemented feature
* Create wallet
* Get transaction info
* Get account info
* Get account balance
* Create transaction
  * Send coins
* Sign transaction
* Broadcast transaction
  * Async mode
  * Sync mode
  * Block mode

## Install
### JVM (Java, Kotlin, Android, ...)
```
repositories {
    jcenter()
}

dependencies {
    implementation("money.terra:terra-sdk:0.0.1")
}
```

## How to use
### Create wallet
```
val mnemonic: String = Mnemonic.generate()
val wallet: TerraWallet = TerraWallet.from(mnemonic)

or

val wallet: TerraWallet = TerraWallet.create()
```
### Connect terra network using http
```
val network = Network("soju-0014")
val server = TerraServer("soju-lcd.terra.dev", network)

or 

val network = ProvidedNetwork.SOJU_0014
val server = ProvidedTerraServer.SOJU

val terra = Terra.connect(wallet, network, server)
```
### Make transaction : send coins
```
val transactionFee = Fee("200000", listOf(Coin("uluna", "50")))
val coins = listOf(Coin("uluna", "100000"))
val targetAddress = "..."

//async mode
val result = terra.broadcastAsync {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
}

//sync mode
val result = terra.broadcastSync {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
}

//block mode
val result = terra.broadcastBlock {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
}

val txHash = result.txhash //txHash = 82D5440A4C4CAB5B74EE3C98CE7F755372CD92E945425A572654179A4A0EE678
```