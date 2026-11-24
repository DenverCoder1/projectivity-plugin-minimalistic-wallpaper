package tv.projectivy.plugin.wallpaperprovider.minimalistic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import tv.projectivy.plugin.wallpaperprovider.api.Event
import tv.projectivy.plugin.wallpaperprovider.api.IWallpaperProviderService
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper
import tv.projectivy.plugin.wallpaperprovider.api.WallpaperType

private const val WALLPAPERS_PER_REQUEST = 6

class WallpaperProviderService : Service() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
    }

    override fun onBind(intent: Intent): IBinder = binder

    private val binder = object : IWallpaperProviderService.Stub() {
        override fun getWallpapers(event: Event?): List<Wallpaper> {
            if (event !is Event.TimeElapsed) return emptyList()

            val showActionButton = PreferencesManager[
                PreferencesManager.SHOW_ACTION_BUTTON,
                PreferencesManager.DEFAULT_SHOW_ACTION_BUTTON
            ]
            return loadRandomWallpapers(WALLPAPERS_PER_REQUEST, showActionButton, ::resolveWallpaper)
        }

        override fun getPreferences(): String = PreferencesManager.export()

        override fun setPreferences(params: String) {
            PreferencesManager.import(params)
        }
    }
}

internal fun loadRandomWallpapers(
    count: Int,
    showActionButton: Boolean,
    resolver: (URL) -> Pair<String, String>
): List<Wallpaper> {
    require(count > 0)
    val executor = Executors.newFixedThreadPool(count)
    try {
        val futures = List(count) {
            executor.submit(Callable<Wallpaper?> {
                try {
                    val url = URL(
                        "https://minimalistic-wallpaper.demolab.com/?random=${UUID.randomUUID()}&redirect=1"
                    )
                    val (uri, title) = resolver(url)
                    Wallpaper(
                        uri = uri,
                        type = WallpaperType.IMAGE,
                        source = uri,
                        title = title,
                        author = "minimalistic-wallpaper.demolab.com",
                        actionUri = if (showActionButton) uri else null
                    )
                } catch (e: IOException) {
                    Log.e("WallpaperProvider", "Failed to resolve random wallpaper", e)
                    null
                }
            })
        }
        return futures.mapNotNull { it.get() }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw IOException("Interrupted while resolving random wallpapers", e)
    } finally {
        executor.shutdownNow()
    }
}

internal fun resolveWallpaper(requestUrl: URL): Pair<String, String> {
    val connection = requestUrl.openConnection() as HttpURLConnection
    try {
        connection.requestMethod = "HEAD"
        connection.instanceFollowRedirects = false
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        val status = connection.responseCode
        if (status !in listOf(301, 302, 303, 307, 308)) {
            throw IOException("Unexpected wallpaper response: HTTP $status")
        }
        val location = connection.getHeaderField("Location")
            ?: throw IOException("Wallpaper redirect has no Location header")
        val uri = try {
            requestUrl.toURI().resolve(URI(location))
        } catch (e: URISyntaxException) {
            throw IOException("Invalid wallpaper redirect URL", e)
        } catch (e: IllegalArgumentException) {
            throw IOException("Invalid wallpaper redirect URL", e)
        }
        if (uri.scheme !in listOf("http", "https") || uri.host.isNullOrEmpty()) {
            throw IOException("Invalid wallpaper redirect URL: $uri")
        }
        val path = uri.path
        if (path.isNullOrEmpty() || path.endsWith('/')) {
            throw IOException("Wallpaper redirect URL has no filename: $uri")
        }
        return uri.toASCIIString() to path.substringAfterLast('/')
    } finally {
        connection.disconnect()
    }
}
