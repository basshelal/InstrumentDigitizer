import com.sun.media.sound.WaveFileReader
import io.reactivex.Observable
import java.io.File

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val file = File(A3_VIOLIN_FILE_PATH)
            val size = file.readBytes().size
            val buffer = ByteArray(size)
            WaveFileReader().getAudioInputStream(file).read(buffer)

            Observable.fromIterable(buffer.asList()).take(5000).subscribe {
                //it.print
            }

            File("src/main/test.wav").writeBytes(buffer)

        }
    }

}

const val A3_VIOLIN_FILE_PATH = "src/main/resources/violin_a3.wav"

inline val Any?.print: Unit
    get() = println(this.toString())