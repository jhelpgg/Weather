package fr.jhelp.weatherservice.tools.tasks.dispacthers

import fr.jhelp.weatherservice.tools.queue.Queue
import fr.jhelp.weatherservice.tools.tasks.Mutex
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

internal class LimitedTaskInSameTimeDispatcher(maximumTaskInSameTime: Int = 8) :
        CoroutineDispatcher() {
    companion object {
        private val global = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        private fun execute(task: () -> Unit) {
            LimitedTaskInSameTimeDispatcher.global.launch {
                task()
            }
        }
    }

    private var numberFreeTask = max(2, min(16, maximumTaskInSameTime))
    private val waitingTasks = Queue<Runnable>()
    private val mutex = Mutex()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        this.mutex {
            this.waitingTasks.enqueue(block)

            if (this.numberFreeTask > 0) {
                this.numberFreeTask--
                LimitedTaskInSameTimeDispatcher.execute(this::nextTask)
            }
        }
    }

    private fun nextTask() {
        var task: Runnable? = null

        do {
            this.mutex {
                if (this.waitingTasks.empty) {
                    task = null
                } else {
                    task = this.waitingTasks.dequeue()
                }
            }

            if (task != null) {
                try {
                    task!!.run()
                } catch (_: Exception) {
                }
            }
        } while (task != null)

        this.mutex { this.numberFreeTask++ }
    }
}
