package uk.whitecrescent.instrumentdigitizer

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.absoluteValue
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
            File(OUTPUT_PATH_WAV)
    )
}

fun writeSineWaveAudio(filePath: String = A3_VIOLIN_FILE_PATH) {
    val file = File(filePath)
    val size = file.readBytes().size
    var buffer = ByteArray(size)
    val stream = AudioSystem.getAudioInputStream(file)
    stream.read(buffer)

    buffer = generateSineWave(220, 10, 0.0, 44100, 2)

    val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

    newStream.printStreamInfo()

    AudioSystem.write(
            newStream,
            AudioFileFormat.Type.WAVE,
            File(OUTPUT_PATH_WAV)
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
fun generateSineWave(frequency: Int, seconds: Int, phase: Double = 0.0,
                     sampleRate: Int = SAMPLE_RATE, channels: Int = 2): ByteArray {
    val totalSamples = seconds * sampleRate * channels
    val period = sampleRate.toDouble() / frequency
    return ByteArray(totalSamples) {
        val angle = (2.0 * PI * it) / period
        /* 127 for normalizing to the range of Byte: -127 to 127 since sin(angle) will return value in range: -1 to 1 */
        /*Amp * sin(2 * PI * f * t + phase)*/
        return@ByteArray (127F * sin(angle + phase)).toByte()
    }
}

/*
 * Used for testing,
 * Compares between 2 sets of data, the original and the converted and returns the differences,
 * the differences should ideally contain nothing useful, so either 0s or very low values that are
 * not useful in finding what the original was
 */
fun compare(original: ByteArray, converted: ByteArray) = compare(original.toDoubleArray(), converted.toDoubleArray())

fun compare(original: DoubleArray, converted: DoubleArray): DoubleArray {
    require(converted.size == original.size) {
        "Original and Converted must be equal in size!" +
                " Original Size: ${original.size}, Converted Size: ${converted.size}"
    }
    return DoubleArray(converted.size) { (original[it] - converted[it]).absoluteValue }
}

fun compare(original: ComplexArray, converted: ComplexArray): Pair<DoubleArray, DoubleArray> {
    require(converted.size == original.size) {
        "Original and Converted must be equal in size!" +
                " Original Size: ${original.size}, Converted Size: ${converted.size}"
    }
    return compare(original.real(), converted.real()) to compare(original.imaginary(), converted.imaginary())
}

fun writeTextToFile(data: ByteArray, delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        data.forEachIndexed { index, byte ->
            appendText("$index$delimiter$byte$lineEnd")
        }
    }
}

fun writeTextToFile(data: ComplexArray, delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        data.forEach {
            appendText("${it.real}$delimiter${it.imaginary}$lineEnd")
        }
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