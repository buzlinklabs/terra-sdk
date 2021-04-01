package money.terra.util.provider

import money.terra.client.TerraClient

interface SequenceProvider {

    suspend fun current(address: String): Long

    suspend fun increase(address: String)

    suspend fun refresh(address: String)
}

open class AlwaysFetchSequenceProvider(private val client: TerraClient) : SequenceProvider {

    override suspend fun current(address: String): Long = client.getSequence(address)

    override suspend fun increase(address: String) {
        //do nothing
    }

    override suspend fun refresh(address: String) {
        //do nothing
    }
}

abstract class CachedSequenceProvider(private val client: TerraClient) : SequenceProvider {

    protected abstract suspend fun set(address: String, sequence: Long)

    protected abstract suspend fun get(address: String): Long?

    protected abstract suspend fun refreshAll()

    override suspend fun current(address: String): Long {
        var currentSequence = get(address)

        if (currentSequence == null) {
            currentSequence = client.getSequence(address)
            set(address, currentSequence)
        }

        return currentSequence
    }

    override suspend fun increase(address: String) {
        get(address)
            ?.let { set(address, it.plus(1)) }
    }
}

open class LocalCachedSequenceProvider(client: TerraClient) : CachedSequenceProvider(client) {

    private var sequenceMap = mutableMapOf<String, Long>()

    override suspend fun set(address: String, sequence: Long) {
        sequenceMap[address] = sequence
    }

    override suspend fun get(address: String): Long? = sequenceMap[address]

    override suspend fun refresh(address: String) {
        sequenceMap.remove(address)
    }

    override suspend fun refreshAll() {
        sequenceMap.clear()
    }
}