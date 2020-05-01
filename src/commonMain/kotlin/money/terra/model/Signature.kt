package money.terra.model

import kr.jadekim.common.util.encoder.asBase64String

data class Signature(
    val signature: String,
    val pubKey: PublicKey,
    val accountNumber: String? = null,
    val sequence: String? = null
) {

    constructor(
        signature: ByteArray,
        publicKey: ByteArray,
        accountNumber: String? = null,
        sequence: String? = null
    ) : this(
        signature.asBase64String,
        PublicKey(publicKey.asBase64String),
        accountNumber,
        sequence
    )
}