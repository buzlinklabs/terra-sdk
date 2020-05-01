package money.terra.hash

import io.nayuki.bitcoin.crypto.Ripemd160
import kr.jadekim.common.util.hash.HashFunction

actual val RIPEMD160: HashFunction = object : HashFunction {

    override fun hash(data: ByteArray, key: ByteArray?): ByteArray = Ripemd160.getHash(data)
}