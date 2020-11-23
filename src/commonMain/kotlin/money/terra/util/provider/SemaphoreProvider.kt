package money.terra.util.provider

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

interface SemaphoreProvider {

    suspend fun acquire(address: String)

    suspend fun release(address: String)
}

suspend inline fun <T> SemaphoreProvider.use(address: String, block: () -> T): T {
    acquire(address)
    try {
        return block()
    } finally {
        release(address)
    }
}

object LocalSemaphoreProvider : SemaphoreProvider {

    private val acquireSemaphore = Semaphore(1)
    private var semaphores = mutableMapOf<String, Semaphore>()

    override suspend fun acquire(address: String) {
        val semaphore = acquireSemaphore.withPermit {
            semaphores.getOrPut(address, { Semaphore(1) })
        }
        semaphore.acquire()
    }

    override suspend fun release(address: String) {
        semaphores[address]?.release()
    }

    suspend fun clean() {
        acquireSemaphore.withPermit {
            semaphores = semaphores.filterValues { !it.tryAcquire() }.toMutableMap()
        }
    }
}