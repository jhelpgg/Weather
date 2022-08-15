package fr.jhelp.weatherservice.network

import fr.jhelp.weatherservice.extensions.cancelable
import fr.jhelp.weatherservice.extensions.observedBy
import fr.jhelp.weatherservice.extensions.parallel
import fr.jhelp.weatherservice.tools.Cancelable
import fr.jhelp.weatherservice.tools.queue.Queue
import fr.jhelp.weatherservice.tools.tasks.TaskContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Network tasks queue
 */
internal object NetworkStatusQueue {
    private val alive = AtomicBoolean(false)
    private val tasks = Queue<() -> Unit>()

    init {
        NetworkStatusCallback.availableFlow.observedBy(action = this::networkAvailable)
    }

    fun add(task: () -> Unit): Cancelable {
        synchronized(this.tasks)
        {
            this.tasks.enqueue(task)
        }

        this.wakeup(false)

        return {
            synchronized(this.tasks)
            {
                this.tasks.removeIf { current -> current == task }
            }
        }.cancelable()
    }

    private fun wakeup(networkStatus: Boolean) {
        if ((networkStatus || NetworkStatusCallback.availableFlow.value)
                && this.alive.compareAndSet(false, true)) {
            synchronized(this.tasks)
            {
                if (this.tasks.notEmpty) {
                    this::run.parallel(TaskContext.HEAVY)
                }
            }
        }
    }

    private fun run() {
        while (this.alive.get()) {
            val task =
                    synchronized(this.tasks)
                    {
                        if (this.tasks.empty) null
                        else this.tasks.dequeue()
                    }

            if (task != null) {
                try {
                    task()
                } catch (_: Exception) {
                    // If exception happen and connection was lost,
                    // the failure is probably due network lost while doing the task.
                    // In this case we will retry do the action next time network comes back
                    if (!NetworkStatusCallback.availableFlow.value) {
                        synchronized(this.tasks)
                        {
                            this.tasks.ahead(task)
                        }
                    }
                }
            } else {
                this.alive.set(false)
            }
        }
    }

    private fun networkAvailable(available: Boolean) {
        if (available) {
            this.wakeup(true)
        } else {
            this.alive.set(false)
        }
    }

    internal fun stop() {
        synchronized(this.tasks)
        {
            this.tasks.clear()
        }

        this.alive.set(false)
    }
}