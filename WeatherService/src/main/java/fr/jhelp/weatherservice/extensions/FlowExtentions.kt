package fr.jhelp.weatherservice.extensions

import fr.jhelp.weatherservice.tools.tasks.TaskContext
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun <P, R> Flow<P>.observedBy(taskContext: TaskContext = TaskContext.SHORT, action: (P) -> R): FutureResult<Unit> =
        taskContext.launch { this.collect { value -> action(value) } }

fun <P, R> Flow<P>.then(taskContext: TaskContext = TaskContext.SHORT, action: (P) -> R): Flow<R> =
        this.map(action).flowOn(taskContext.coroutineContext)