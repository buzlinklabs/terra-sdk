@file:JvmName("CoroutineUtils")

package money.terra.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

@JvmOverloads
fun <R> getContinuation(
    onFinish: BiConsumer<R?, Throwable?>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) = object : Continuation<R> {
    override val context: CoroutineContext
        get() = dispatcher

    override fun resumeWith(result: Result<R>) {
        onFinish.accept(result.getOrNull(), result.exceptionOrNull())
    }
}

@JvmOverloads
fun <R> toFuture(
    function: (Continuation<R>) -> Any,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): CompletableFuture<R> {
    val future = CompletableFuture<R>()
    val continuation = getContinuation<R>({ result, error ->
        if (error == null) {
            future.completeExceptionally(error)
        } else {
            future.complete(result)
        }
    }, dispatcher)

    function(continuation)

    return future
}