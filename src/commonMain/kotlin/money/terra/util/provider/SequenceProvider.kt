package money.terra.util.provider

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import money.terra.client.TerraClient

interface SequenceProvider {

    suspend fun get(address: String): Long
}

open class AlwaysFetchSequenceProvider(private val client: TerraClient) : SequenceProvider {

    override suspend fun get(address: String): Long = client.getSequence(address)
}

open class LocalCachedSequenceProvider(private val client: TerraClient) : SequenceProvider {

    private val semaphore = Semaphore(1)
    private var sequenceMap = mutableMapOf<String, Long>()

    override suspend fun get(address: String): Long = semaphore.withPermit {
        val lastSequence = sequenceMap[address]

        if (lastSequence == null) {
            val current = client.getSequence(address)
            sequenceMap[address] = current
            return current
        }

        return lastSequence + 1
    }
}