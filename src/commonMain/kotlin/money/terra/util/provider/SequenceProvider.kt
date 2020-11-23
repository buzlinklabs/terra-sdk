package money.terra.util.provider

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import money.terra.client.TerraClient

interface SequenceProvider {

    suspend fun next(address: String): Long

    suspend fun refresh(address: String)
}

open class AlwaysFetchSequenceProvider(private val client: TerraClient) : SequenceProvider {

    override suspend fun next(address: String): Long = client.getSequence(address)

    override suspend fun refresh(address: String) {
        //do nothing
    }
}

abstract class CachedSequenceProvider(private val client: TerraClient) : SequenceProvider {

    private val semaphore = Semaphore(1)

    protected abstract suspend fun set(address: String, sequence: Long)

    protected abstract suspend fun get(address: String): Long?

    protected abstract suspend fun remove(address: String)

    protected abstract suspend fun removeAll()

    override suspend fun next(address: String): Long = semaphore.withPermit {
        val lastSequence = get(address)
        val currentSequence = lastSequence?.plus(1) ?: client.getSequence(address)

        set(address, currentSequence)

        return currentSequence
    }

    override suspend fun refresh(address: String) {
        semaphore.withPermit { remove(address) }
    }

    suspend fun clear() = removeAll()
}

open class LocalCachedSequenceProvider(client: TerraClient) : CachedSequenceProvider(client) {

    private var sequenceMap = mutableMapOf<String, Long>()

    override suspend fun set(address: String, sequence: Long) {
        sequenceMap[address] = sequence
    }

    override suspend fun get(address: String): Long? = sequenceMap[address]

    override suspend fun remove(address: String) {
        sequenceMap.remove(address)
    }

    override suspend fun removeAll() {
        sequenceMap.clear()
    }
}