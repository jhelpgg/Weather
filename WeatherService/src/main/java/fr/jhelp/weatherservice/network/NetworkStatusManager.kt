package fr.jhelp.weatherservice.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import fr.jhelp.weatherservice.network.NetworkStatusManager.destroy
import fr.jhelp.weatherservice.network.NetworkStatusManager.initialize
import fr.jhelp.weatherservice.provider.provided
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Network manager, for make work [TaskContext.NETWORK]
 *
 * It will requires the permissions:
 * * android.Manifest.permission.INTERNET
 * * android.Manifest.permission.ACCESS_NETWORK_STATE
 *
 * To start the management, call [initialize], to stop it call [destroy]
 */
internal object NetworkStatusManager {
    private val context: Context by provided<Context>()
    private val initialized = AtomicBoolean(false)

    /**
     * Initialize the manager.
     */
    @RequiresPermission(allOf =
    [android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE])
    fun initialize() {
        if (!this.initialized.getAndSet(true)) {
            val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            this.context.getSystemService(ConnectivityManager::class.java)
                    ?.registerNetworkCallback(request, NetworkStatusCallback)
        }
    }

    /**
     * Stop network management
     */
    fun destroy() {
        if (this.initialized.getAndSet(false)) {
            this.context.getSystemService(ConnectivityManager::class.java)
                    ?.unregisterNetworkCallback(NetworkStatusCallback)
            NetworkStatusQueue.stop()
        }
    }
}