package uk.whitecrescent.instrumentdigitizer

import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class ReaderWriter(val filePath: String = A3_VIOLIN_FILE_PATH) {

    lateinit var stream: AudioInputStream
    lateinit var buffer: ByteArray

    fun read(): ByteArray {
        val file = File(filePath)
        val size = file.readBytes().size
        buffer = ByteArray(size)
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        stream = audioInputStream
        stream.read(buffer)

        return buffer
    }

    fun write(outPath: String = OUTPUT_PATH_WAV) {
        val audioInputStream = AudioSystem.getAudioInputStream(File(filePath))
        AudioSystem.write(
                audioInputStream,
                AudioFileFormat.Type.WAVE,
                File(outPath)
        )
    }

    fun writeText(outPath: String = OUTPUT_PATH_TEXT) {
        val text = StringBuilder()
        buffer.forEach { text.append(it) }
        File(outPath).writeText(text.toString())
        write()
    }

    fun close() = stream.close()

}
