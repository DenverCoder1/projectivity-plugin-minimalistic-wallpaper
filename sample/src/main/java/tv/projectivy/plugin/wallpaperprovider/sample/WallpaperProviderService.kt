package tv.projectivy.plugin.wallpaperprovider.sample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.UUID
import tv.projectivy.plugin.wallpaperprovider.api.Event
import tv.projectivy.plugin.wallpaperprovider.api.IWallpaperProviderService
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper
import tv.projectivy.plugin.wallpaperprovider.api.WallpaperType

class WallpaperProviderService : Service() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
    }

    override fun onBind(intent: Intent): IBinder = binder

    private val binder = object : IWallpaperProviderService.Stub() {
        override fun getWallpapers(event: Event?): List<Wallpaper> {
            if (event !is Event.TimeElapsed) return emptyList()

            return listOf(
                Wallpaper(
                    uri = "https://minimalistic-wallpaper.demolab.com/?random=${UUID.randomUUID()}",
                    type = WallpaperType.IMAGE,
                    source = "https://minimalistic-wallpaper.demolab.com/"
                )
            )
        }

        override fun getPreferences(): String = PreferencesManager.export()

        override fun setPreferences(params: String) {
            PreferencesManager.import(params)
        }
    }
}
