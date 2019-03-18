package uk.whitecrescent.instrumentdigitizer

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

fun writeRandomAudio(filePath: String = A3_VIOLIN_FILE_PATH) {
    val file = File(filePath)
    val size = file.readBytes().size
    val buffer = ByteArray(size)
    val stream = AudioSystem.getAudioInputStream(file)
    stream.read(buffer)

    val random = Random.nextBytes(buffer.size)

    buffer.forEachIndexed { index, byte ->
        buffer[index] = random[index]
    }

    val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

    newStream.printStreamInfo()

    AudioSystem.write(
            newStream,
            AudioFileFormat.Type.WAVE,
            File(OUTPUT_PATH)
    )
}

fun writeSineWaveAudio(filePath: String = A3_VIOLIN_FILE_PATH) {
    val file = File(filePath)
    val size = file.readBytes().size
    var buffer = ByteArray(size)
    val stream = AudioSystem.getAudioInputStream(file)
    stream.read(buffer)

    buffer = generateSineWave(220, 10, 44100, 2)

    val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

    newStream.printStreamInfo()

    AudioSystem.write(
            newStream,
            AudioFileFormat.Type.WAVE,
            File(OUTPUT_PATH)
    )
}

/**
 * Generates a Sine wave, the general algorithm is taken from [Rosetta Stone](https://rosettacode.org/wiki/Sine_wave#Kotlin)
 * @param frequency frequency in Hz
 * @param seconds seconds of Sine Wave
 * @param sampleRate sample rate in Hz (cycles per second)
 * @param channels channels of the sine wave
 * @return A [ByteArray] representing a Sine Wave
 */
fun generateSineWave(frequency: Int, seconds: Int, sampleRate: Int = SAMPLE_RATE, channels: Int = 2): ByteArray {
    val totalSamples = seconds * sampleRate * channels
    val period = sampleRate.toDouble() / frequency
    return ByteArray(totalSamples) {
        val angle = (2.0 * PI * it) / period
        /* 127 for normalizing to the range of Byte: -127 to 127 */
        return@ByteArray (sin(angle) * 127F).toByte()
    }
}

fun AudioInputStream.printStreamInfo() {
    val frames = this.frameLength
    val frameSize = this.format.frameSize
    val frameRate = this.format.frameRate
    val sampleRate = this.format.sampleRate
    val sampleSize = this.format.sampleSizeInBits
    println("""
            Frames: $frames
            FrameSize: $frameSize
            FrameRate: $frameRate
            SampleRate: $sampleRate
            SampleSize: $sampleSize
        """.trimIndent())

    val duration = frames / frameRate
    println("Duration: $duration")
}