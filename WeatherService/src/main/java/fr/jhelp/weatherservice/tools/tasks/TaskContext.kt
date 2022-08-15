package fr.jhelp.weatherservice.tools.tasks

import fr.jhelp.weatherservice.tools.tasks.dispacthers.LimitedTaskInSameTimeDispatcher
import fr.jhelp.weatherservice.tools.tasks.dispacthers.NetworkDispatcher
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult
import fr.jhelp.weatherservice.tools.tasks.promise.Promise
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Task context to play task in specific context
 */
enum class TaskContext(val coroutineScope: CoroutineScope, val coroutineContext: CoroutineContext)
{
    /** Reserved to all UI operation */
    UI(MainScope(), Dispatchers.Main),

    /**For read/write in disk like file, database  */
    IO(CoroutineScope(LimitedTaskInSameTimeDispatcher(4)), Dispatchers.IO),

    /** For task that take short time and not too much memory  */
    SHORT(CoroutineScope(LimitedTaskInSameTimeDispatcher(8)), Dispatchers.Default),

    /** For heavy tasks in time or memory */
    HEAVY(CoroutineScope(LimitedTaskInSameTimeDispatcher(4)), Dispatchers.Default),

    /**
     * For do operation when network is present.
     *
     * Task are guarantee to be played when network is there. There are stored in waiting queue on offline
     *  */
    NETWORK(CoroutineScope(NetworkDispatcher), Dispatchers.Default)
    ;

    fun <R : Any> launch(task: suspend () -> R): FutureResult<R>
    {
        val promise = Promise<R>()
        val cancelled = AtomicBoolean(false)

        val job = this.coroutineScope.launch {
            withContext(this@TaskContext.coroutineContext)
            {
                if (cancelled.get())
                {
                    return@withContext
                }

                try
                {
                    promise.result(task())
                }
                catch (exception: Exception)
                {
                    promise.error(exception)
                }
            }
        }

        promise.onCancel { reason ->
            cancelled.set(true)
            job.cancel(CancellationException(reason))
        }

        return promise.future
    }

    fun <P, R : Any> launch(parameter: P, task: suspend (P) -> R): FutureResult<R>
    {
        val promise = Promise<R>()
        val cancelled = AtomicBoolean(false)

        val job = this.coroutineScope.launch {
            withContext(this.coroutineContext)
            {
                if (cancelled.get())
                {
                    return@withContext
                }

                try
                {
                    promise.result(task(parameter))
                }
                catch (exception: Exception)
                {
                    promise.error(exception)
                }
            }
        }

        promise.onCancel { reason ->
            cancelled.set(true)
            job.cancel(CancellationException(reason))
        }

        return promise.future
    }
}