package money.terra.util

import kotlinx.coroutines.sync.Semaphore

suspend inline fun <reified T> Semaphore.use(block: () -> T): T {
    acquire()

    return try {
        block()
    } finally {
        release()
    }
}