package uk.whitecrescent.instrumentdigitizer

import io.reactivex.Observable
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioSystem

class Reader(val filePath: String = A3_VIOLIN_FILE_PATH) {

    fun read() {
        val file = File(filePath)
        val size = file.readBytes().size
        val buffer = ByteArray(size)
        val stream = AudioSystem.getAudioInputStream(file)
        stream.read(buffer)
        stream.format.encoding.print

        Observable.fromIterable(buffer.asList()).take(size.toLong()).subscribe {
            //print(it)
        }
        stream.close()
    }

    fun write() {
        val file = File(filePath)
        val stream = AudioSystem.getAudioInputStream(file)
        AudioSystem.write(
                stream, AudioFileFormat.Type.WAVE, File("test.wav")
        )
        stream.close()
    }

}

const val A3_VIOLIN_FILE_PATH = "src/main/resources/violin_a3.wav"
