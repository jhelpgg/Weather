package fr.jhelp.weatherservice.tools.tasks.promise

class CancellationException(val reason: String) : Exception("Cancelled because : $reason")