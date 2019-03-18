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

    buffer = generateSineWave(220, 10, 44100)

    val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

    newStream.printStreamInfo()

    AudioSystem.write(
            newStream,
            AudioFileFormat.Type.WAVE,
            File(OUTPUT_PATH)
    )
}

fun generateSineWave(frequency: Int, seconds: Int, sampleRate: Int): ByteArray {
    val totalSamples = seconds * sampleRate // this disregards channels, so multiply by 2 for stereo
    return ByteArray(totalSamples) {
        val angle = (2.0 * PI * it) / (sampleRate.toDouble() / frequency)
        return@ByteArray (sin(angle) * 128).toByte()
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