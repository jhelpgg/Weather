package fr.jhelp.weatherservice.tools.tasks.dispacthers

import fr.jhelp.weatherservice.network.NetworkStatusQueue
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

internal object NetworkDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        NetworkStatusQueue.add(block::run)
    }
}
