package uk.whitecrescent.instrumentdigitizer

import io.reactivex.Observable
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class ReaderWriter(val filePath: String = A3_VIOLIN_FILE_PATH) {

    lateinit var stream: AudioInputStream
    lateinit var buffer: ByteArray

    fun read() {
        val file = File(filePath)
        val size = file.readBytes().size
        buffer = ByteArray(size)
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        stream = audioInputStream
        stream.read(buffer)

        Observable.fromIterable(buffer.asList()).take(size.toLong()).subscribe {

        }
    }

    fun write(path: String = OUTPUT_PATH) {
        val audioInputStream = AudioSystem.getAudioInputStream(File(A3_VIOLIN_FILE_PATH))
        AudioSystem.write(
                audioInputStream,
                AudioFileFormat.Type.WAVE,
                File(path)
        )
    }

    fun writeText(path: String = OUTPUT_PATH_TEXT) {
        val text = StringBuilder()
        buffer.forEach { text.append(it) }
        File(path).writeText(text.toString())
        write()
    }

    fun close() = stream.close()

}
