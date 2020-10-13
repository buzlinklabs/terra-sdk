# Terra Kotlin SDK
[![Download](https://api.bintray.com/packages/terra-money/maven/terra-sdk/images/download.svg)](https://bintray.com/terra-money/maven/terra-sdk/_latestVersion)

## Support Platform
* JVM

## Install
### JVM
```
repositories {
    jcenter()

    // If can't find
    maven("https://dl.bintray.com/terra-money/maven")
}

dependencies {
    implementation("money.terra:terra-sdk:$terraSdkVersion")
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
val chainId = "tequila-0004"
val lcdUrl = "https://tequila-lcd.terra.dev"
val terra = Terra.connect(wallet, chainId, lcdUrl)
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
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

//sync mode
val result = terra.broadcastSync {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

//block mode
val result = terra.broadcastBlock {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

val txHash = result.txhash //txHash = 82D5440A4C4CAB5B74EE3C98CE7F755372CD92E945425A572654179A4A0EE678
```
