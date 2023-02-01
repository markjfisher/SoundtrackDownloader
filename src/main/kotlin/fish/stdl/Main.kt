package fish.stdl

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.mainBody
import java.io.File

class STDLArgs(parser: ArgParser) {
    val albumSite by parser.positional(name = "ALBUM_SITE", help = "Album site to scrape songs from (off https://downloads.khinsider.com/game-soundtracks/album)")
    val outDir by parser.positional(name = "OUTPUT_DIR", help = "The output directory to save downloaded files to")
}

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::STDLArgs).run {
        if (albumSite.isEmpty()) {
            throw SystemExitException("You must specify the albumSite to parse.", -1)
        }
        val outDirFile = File(outDir)
        if (!outDirFile.isDirectory) {
            throw SystemExitException("You must specify a valid output directory", -1)
        }
        SoundtrackDownloader().download(albumSite, outDirFile)
    }
}