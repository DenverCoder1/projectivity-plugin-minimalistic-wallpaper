package tv.projectivy.plugin.wallpaperprovider.minimalistic

import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URL
import java.util.Collections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WallpaperProviderServiceTest {
    private fun withResponse(status: Int, location: String?, test: (URL) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            assertEquals("HEAD", exchange.requestMethod)
            if (location != null) exchange.responseHeaders.add("Location", location)
            exchange.sendResponseHeaders(status, -1)
            exchange.close()
        }
        server.start()
        try {
            test(URL("http://127.0.0.1:${server.address.port}/?random=1"))
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun providesSeveralDistinctRandomWallpapers() {
        val requestUrls = Collections.synchronizedList(mutableListOf<String>())

        val wallpapers = loadRandomWallpapers(6, true) { url ->
            requestUrls.add(url.toString())
            url.toString() to "wallpaper.jpg"
        }

        assertEquals(6, wallpapers.size)
        assertEquals(6, requestUrls.toSet().size)
        assertEquals(6, wallpapers.map { it.uri }.toSet().size)
    }

    @Test
    fun actionUriFollowsShowActionButtonSetting() {
        for (showActionButton in listOf(true, false)) {
            val wallpaper = loadRandomWallpapers(1, showActionButton) {
                "https://example.com/image.jpg" to "image.jpg"
            }.single()

            assertEquals(
                if (showActionButton) wallpaper.uri else null,
                wallpaper.actionUri
            )
        }
    }

    @Test
    fun usesRedirectUrlAndFilename() {
        withResponse(302, "https://example.com/images/yefedlger-eolyn.jpg") { url ->
            assertEquals(
                "https://example.com/images/yefedlger-eolyn.jpg" to "yefedlger-eolyn.jpg",
                resolveWallpaper(url)
            )
        }
    }

    @Test
    fun resolvesRelativeRedirectAndDecodesFilename() {
        withResponse(302, "/images/hello%20world.jpg?size=large") { url ->
            assertEquals(
                "http://127.0.0.1:${url.port}/images/hello%20world.jpg?size=large" to "hello world.jpg",
                resolveWallpaper(url)
            )
        }
    }

    @Test
    fun rejectsInvalidResponses() {
        for ((status, location) in listOf(
            200 to "https://example.com/image.jpg",
            302 to null,
            302 to "https://example.com/images/",
            302 to "file:///image.jpg",
            302 to "https://example.com/bad url.jpg"
        )) {
            withResponse(status, location) { url ->
                assertThrows(IOException::class.java) { resolveWallpaper(url) }
            }
        }
    }
}
