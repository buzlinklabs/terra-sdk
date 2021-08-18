# Terra Kotlin SDK
<b>Deprecated this repository. Use https://github.com/jdekim43/terra-kotlin-sdk</b>

## Support Platform
* JVM

## Install
### JVM
```
repositories {
    maven("https://jadekim.jfrog.io/artifactory/maven")
}

dependencies {
    implementation("money.terra:terra-sdk-jvm:0.3.11")
}
```

## How to use in kotlin
### Create wallet
```
val mnemonic: String = Mnemonic.generate()
val wallet: TerraWallet = TerraWallet.from(mnemonic)

or

val wallet: Pair<TerraWallet, String> = TerraWallet.create()
```
### Connect terra network using http
```
val chainId = "tequila-0004"
val lcdUrl = "https://tequila-lcd.terra.dev"
val terra = Terra.connect(wallet, chainId, lcdUrl)
```
### Sign transaction
```
val signedTransaction: Transaction<Message> = connectedWallet.sign(builder.build(), client.getSequence(wallet.address));


If want get only signature
val text = "....";
val signature: Signature = connectedWallet.getSignature(text);
```
### Make transaction : send coins
```
val transactionFee = Fee("200000", listOf(Coin("uluna", "50")))
val coins = listOf(Coin("uluna", "100000"))
val targetAddress = "..."

//async mode
val (transaction, result) = terra.broadcastAsync {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

//sync mode
val (transaction, result) = terra.broadcastSync {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

//block mode
val (transaction, result) = terra.broadcastBlock {
    fee = transactionFee

    SendMessage(wallet.address, targetAddress, coins).addThis()
    
    or

    with { SendMessage(wallet.address, targetAddress, coins) }
}

val txHash = result.txhash //txHash = 82D5440A4C4CAB5B74EE3C98CE7F755372CD92E945425A572654179A4A0EE678
```

## How to use in Java
### Create wallet
```
String mnemonic = Mnemonic.generate();
TerraWallet wallet = TerraWallet.from(mnemonic);
final String walletAddress = wallet.getAddress();

or
Pair<TerraWallet, String> wallet = TerraWallet.create();
```
### Connect terra network using http
```
Network network = NetworkKt.Network("tequila-0004");
String lcdUrl = "https://tequila-lcd.terra.dev";
TerraClient client = new TerraLcdClient(network);

String accountNumber = CoroutineUtils.<String>toFuture(c -> client.getAccountNumber(walletAddress, c)).get();
ConnectedTerraWallet connectedWallet = new ConnectedTerraWallet(wallet, accountNumber, client);

Terra terra = Terra(connectedWallet);
```
### Sign transaction
```
Long sequence = CoroutineUtils.<Long>toFuture(c -> client.getSequence(walletAddress, c)).get();
Transaction<Message> signedTransaction = connectedWallet.sign(builder.build(), sequence);


If want get only signature
String text = "....";
Signature signature = connectedWallet.getSignature(text);
```
### Make transaction : send coins
```
List<Coin> feeCoins = new ArrayList<Coin>();
feeCoins.add(new Coin("uluna", "50"));
Fee transactionFee = new Fee("200000", feeCoins);

List<Coin> coins = new ArrayList<Coin>();
coins.add(new Coin("uluna", "100000"));

val targetAddress = "..."

TransactionBuilder builder = new TransactionBuilder();
builder.setFee(transactionFee);
builder.with(new SendMessage(walletAddress, targetAddress, coins);
Transaction<Message> transaction = builder.build();

//async mode
CompletableFuture<?> broadcastFuture = CoroutineUtils.toFuture(c -> terra.broadcastAsync(transaction, c));

//sync mode
CompletableFuture<?> broadcastFuture = CoroutineUtils.toFuture(c -> terra.broadcastSync(transaction, c));

//block mode
CompletableFuture<Pair<? extends Transaction<Message>, ? extends BroadcastTransactionBlockResult>> broadcastFuture 
    = CoroutineUtils.toFuture(c -> terra.broadcastBlock(transaction, c));

BroadcastTransactionBlockResult result = broadcastFuture.get().getSecond();
String txHash = result.getTxhash(); //txHash = 82D5440A4C4CAB5B74EE3C98CE7F755372CD92E945425A572654179A4A0EE678
```
