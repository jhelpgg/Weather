package fr.jhelp.weatherservice.tools.tasks

import java.util.concurrent.Semaphore

class Mutex {
    private val mutex = Semaphore(1, true)

    operator fun <R : Any> invoke(task: () -> R): R {
        var result: R? = null
        var error: Throwable? = null
        this.mutex.acquire()

        try {
            result = task()
        } catch (throwable: Throwable) {
            error = throwable
        } finally {
            this.mutex.release()
        }

        if (error != null) {
            throw error
        }

        return result!!
    }
}