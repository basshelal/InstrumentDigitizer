package uk.whitecrescent.instrumentdigitizer

import io.reactivex.Observable
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.random.Random

class Reader(val filePath: String = A3_VIOLIN_FILE_PATH) {

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

    fun writeText(path: String = OUTPUT_PATH_TEXT) {
        val text = StringBuilder()
        buffer.forEach { text.append(it) }
        File(path).writeText(text.toString())

        write()
    }

    fun writeFromBuffer() {
        val file = File(filePath)
        val size = file.readBytes().size
        buffer = ByteArray(size)
        stream = AudioSystem.getAudioInputStream(file)
        stream.read(buffer)

        val random = Random.nextBytes(buffer.size)
        buffer.forEachIndexed { index, byte ->
            buffer[index] = random[index]
        }

        val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

        AudioSystem.write(
                newStream,
                AudioFileFormat.Type.WAVE,
                File(OUTPUT_PATH)
        )
    }

    fun write(path: String = OUTPUT_PATH) {
        val audioInputStream = AudioSystem.getAudioInputStream(File(A3_VIOLIN_FILE_PATH))
        AudioSystem.write(
                audioInputStream,
                AudioFileFormat.Type.WAVE,
                File(path)
        )
    }

    fun close() = stream.close()

}

const val A3_VIOLIN_FILE_PATH = "src/main/resources/violin_a3.wav"
const val OUTPUT_PATH = "src/main/resources/out.wav"
const val OUTPUT_PATH_TEXT = "src/main/resources/out.txt"
