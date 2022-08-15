package fr.jhelp.weatherservice.tools.queue

internal class QueueElement<T>(val element: T, var next: QueueElement<T>? = null)
