package fish.stdl

import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kong.unirest.Unirest
import org.jsoup.Jsoup

open class SoundtrackDownloader {
    fun download(albumSite: String, outDir: File) {
        println("downloading from: $albumSite")
        val page = fetchPage(albumSite)!!
        val doc = Jsoup.parse(page)
        val songLinks = doc.select("td[class=playlistDownloadSong] a").map{ it.attr("href")}

        // for each link, grab its page, and then get the mp3 download link in each, save it to outDir
        songLinks.forEach { url ->
            val songPage = fetchPage("https://downloads.khinsider.com$url")!!
            val raw = Jsoup.parse(songPage)
            val mp3Url = raw.selectFirst("div[id=pageContent] p a:has(span)")!!.attr("href")
            if (mp3Url.endsWith("mp3")) {
                val name = decode(mp3Url.substringAfterLast('/'))
                val outPath = outDir.toPath().resolve(name)
                if (name.startsWith("02")) {
                    val saved = Unirest.get(mp3Url).asFile(outPath.toString()).body
                    println("Saved to $saved")
                }
            } else {
                println("Could not find mp3 file in url $mp3Url")
            }
        }
    }

    fun decode(url: String): String {
        return URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    }

    private val defaultHeaders = mapOf(
        "origin" to "https://downloads.khinsider.com/",
        "accept-encoding" to "gzip, deflate, br",
        "accept-language" to "en-GB,en;q=0.9,en-US;q=0.8,fr;q=0.7",
//        "x-requested-with" to "XMLHttpRequest",
        "pragma" to "no-cache",
//        "content-type" to "application/x-www-form-urlencoded; charset=UTF-8",
        "accept" to "application/json, text/javascript, */*; q=0.01",
        "cache-control" to "no-cache",
    )

    private fun fetchPage(site: String): String? {
        return Unirest
            .get(site)
            .headers(defaultHeaders)
            .asString()
            .body
    }
}
