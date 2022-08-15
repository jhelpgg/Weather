package fr.jhelp.weatherservice.extensions

import fr.jhelp.weatherservice.tools.Cancelable
import fr.jhelp.weatherservice.tools.tasks.TaskContext
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult

fun <T> (() -> T).cancelable() =
        object : Cancelable {
            override fun cancel() {
                this@cancelable()
            }
        }

fun <R : Any> (() -> R).parallel(taskContext: TaskContext = TaskContext.SHORT): FutureResult<R> =
        taskContext.launch(this)

fun <P, R : Any> ((P) -> R).parallel(parameter: P, taskContext: TaskContext = TaskContext.SHORT): FutureResult<R> =
        taskContext.launch(parameter, this)
