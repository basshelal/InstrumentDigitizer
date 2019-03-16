package uk.whitecrescent.instrumentdigitizer

import io.reactivex.Observable
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin
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

    fun writeRandomAudio() {
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

        val frames = newStream.frameLength
        val frameSize = newStream.format.frameSize
        val frameRate = newStream.format.frameRate
        val sampleRate = newStream.format.sampleRate
        val sampleSize = newStream.format.sampleSizeInBits
        println("""
            Frames: $frames
            FrameSize: $frameSize
            FrameRate: $frameRate
            SampleRate: $sampleRate
            SampleSize: $sampleSize
        """.trimIndent())

        val duration = frames / frameRate
        println("Duration: $duration")

        AudioSystem.write(
                newStream,
                AudioFileFormat.Type.WAVE,
                File(OUTPUT_PATH)
        )
    }

    fun writeSineWaveAudio() {
        val file = File(filePath)
        val size = file.readBytes().size
        buffer = ByteArray(size)
        stream = AudioSystem.getAudioInputStream(file)
        stream.read(buffer)

        buffer = generateSineWave(220, 20, 44100)

        val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

        val frames = newStream.frameLength
        val frameSize = newStream.format.frameSize
        val frameRate = newStream.format.frameRate
        val sampleRate = newStream.format.sampleRate
        val sampleSize = newStream.format.sampleSizeInBits
        println("""
            Frames: $frames
            FrameSize: $frameSize
            FrameRate: $frameRate
            SampleRate: $sampleRate
            SampleSize: $sampleSize
        """.trimIndent())

        val duration = frames / frameRate
        println("Duration: $duration")

        AudioSystem.write(
                newStream,
                AudioFileFormat.Type.WAVE,
                File(OUTPUT_PATH)
        )
    }

    fun generateSineWave(frequency: Int, seconds: Int, sampleRate: Int): ByteArray {
        val totalSamples = seconds * sampleRate
        return ByteArray(totalSamples) {
            val angle = (2.0 * PI * it) / (sampleRate.toDouble() / frequency)
            return@ByteArray (sin(angle) * 128).toByte()
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

const val A3_VIOLIN_FILE_PATH = "src/main/resources/violin_a3.wav"
const val OUTPUT_PATH = "src/main/resources/out.wav"
const val OUTPUT_PATH_TEXT = "src/main/resources/out.txt"
