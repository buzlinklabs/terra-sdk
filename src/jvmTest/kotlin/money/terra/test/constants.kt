package money.terra.test

import money.terra.Network
import money.terra.client.lcd.TerraLcdClient

val MNEMONIC = "police head unfair frozen animal sketch peace budget orange foot " +
        "fault quantum caution make reject fruit minimum east stuff leisure " +
        "seminar ocean credit ridge"

val PRIVATE_KEY = "0099b555956f56a2889c78594cfac8d8aa6d0a6e75bd3ccfefb5248b6b83d8096c"
val PUBLIC_KEY = "0352105a7248e226cbb913aad4d5997cf03db9e6caf03dd9a1d168442325d4ff1f"
val ADDRESS = "terra14aqr0fwhsh334qpeu39wuzdt9hkw2pwvwnyvh6"
val TEST_ADDRESS = "terra1c9q3dtvzfw2hmlkss75vx7dl7k0h3eshvescrw"

val NETWORK = Network("tequila-0004")
val HTTP_CLIENT = TerraLcdClient(NETWORK, "https://tequila-lcd.terra.dev")
