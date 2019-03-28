@file:Suppress("NOTHING_TO_INLINE")

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

inline fun writeRandomAudio(filePath: String = A3_VIOLIN_FILE_PATH) {
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

inline fun writeSineWaveAudio(filePath: String = A3_VIOLIN_FILE_PATH) {
    val file = File(filePath)
    val size = file.readBytes().size
    var buffer = ByteArray(size)
    val stream = AudioSystem.getAudioInputStream(file)
    stream.read(buffer)

    buffer = generateSineWave(SineWave(220.0, 1.0, 0.5), 10.0, 44100, 2)

    val newStream = AudioInputStream(ByteArrayInputStream(buffer), stream.format, stream.frameLength)

    stream.printStreamInfo()

    AudioSystem.write(
            newStream,
            AudioFileFormat.Type.WAVE,
            File(OUTPUT_PATH_WAV)
    )
}

/**
 * Generates a Sine wave, the general algorithm is taken from [Rosetta Stone](https://rosettacode.org/wiki/Sine_wave#Kotlin)
 *
 * @param frequency frequency in Hz
 * @param amplitude a Double between 0.0 and 1.0 to multiply by the [MAX_AMPLITUDE]
 * @param seconds seconds of Sine Wave
 * @param phase a Double between -1.0 and 1.0 to add phase to this Sine Wave, in Radians
 * @param sampleRate sample rate in Hz (cycles per second)
 * @param channels channels of the sine wave
 * @return A [ByteArray] representing a Sine Wave
 */
inline fun generateSineWave(frequency: Double, amplitude: Double = 1.0, phase: Double = 0.0,
                            seconds: Double, sampleRate: Int = SAMPLE_RATE, channels: Int = 1): ByteArray {

    val maxAmplitude = amplitude * MAX_AMPLITUDE
    val totalSamples = (seconds * sampleRate.d * channels.d).i
    val period = sampleRate.d / frequency
    val phaseShift = phase * PI

    return ByteArray(totalSamples) {
        val angle = (2.0 * PI * it) / period
        /*Amp * sin(2 * PI * f * t + phase)*/
        return@ByteArray (maxAmplitude * sin(angle + phaseShift)).toByte()
    }
}

inline fun generateSineWave(sineWave: SineWave, seconds: Double,
                            sampleRate: Int = SAMPLE_RATE, channels: Int = 1) =
        generateSineWave(sineWave.frequency, sineWave.amplitude, sineWave.phase, seconds, sampleRate, channels)

inline fun generateTwoSineWaves(frequency1: Int, frequency2: Int,
                                phase1: Double = 0.0, phase2: Double = 0.0,
                                seconds: Double, sampleRate: Int = SAMPLE_RATE, channels: Int = 2): ByteArray {

    // TODO: 24-Mar-19 This isn't very right but it's ok, missing amplitude, how much Amp in each wave

    val maxAmplitude = 127.0 / 2
    val totalSamples = (seconds * sampleRate.d * channels.d).i

    val period1 = sampleRate.d / frequency1
    val period2 = sampleRate.d / frequency2

    val phaseShift1 = phase1 * PI
    val phaseShift2 = phase2 * PI

    return ByteArray(totalSamples) {
        val angle1 = (2.0 * PI * it) / period1
        val angle2 = (2.0 * PI * it) / period2
        /*Amp * sin(2 * PI * f * t + phase)*/
        val addedWaves = sin(angle1 + phaseShift1) + sin(angle2 + phaseShift2)
        return@ByteArray (maxAmplitude * addedWaves).toByte()
    }
}

/*
 * Used for testing,
 * Compares between 2 sets of data, the original and the converted and returns the differences,
 * the differences should ideally contain nothing useful, so either 0s or very low values that are
 * not useful in finding what the original was
 */
inline fun compare(original: ByteArray, converted: ByteArray) = compare(original.toDoubleArray(), converted.toDoubleArray())

inline fun compare(original: DoubleArray, converted: DoubleArray): DoubleArray {
    require(converted.size == original.size) {
        "Original and Converted must be equal in size!" +
                " Original Size: ${original.size}, Converted Size: ${converted.size}"
    }
    return DoubleArray(converted.size) { (original[it] - converted[it]).absoluteValue }
}

inline fun compare(original: ComplexArray, converted: ComplexArray): Pair<DoubleArray, DoubleArray> {
    require(converted.size == original.size) {
        "Original and Converted must be equal in size!" +
                " Original Size: ${original.size}, Converted Size: ${converted.size}"
    }
    return compare(original.real(), converted.real()) to compare(original.imaginary(), converted.imaginary())
}

inline fun writeToWaveFile(data: ByteArray, fileName: String) {
    val file = newFile("$fileName.wav")

    val stream = easyFormatAudioInputStream(data)

    AudioSystem.write(
            stream,
            AudioFileFormat.Type.WAVE,
            file
    )
}

inline fun readFromWaveFile(fileName: String): ByteArray {
    val file = newFile("$fileName.wav")
    val stream = AudioSystem.getAudioInputStream(EASY_FORMAT, AudioSystem.getAudioInputStream(file))
    val buffer = ByteArray(stream.frameLength.i)

    stream.read(buffer)

    return buffer
}

inline fun writeTextToFile(data: ByteArray, delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        data.forEachIndexed { index, byte ->
            appendText("$index$delimiter$byte$lineEnd")
        }
    }
}

inline fun writeTextToFile(data: ComplexArray, delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        data.forEach {
            appendText("${it.real}$delimiter${it.imaginary}$lineEnd")
        }
    }
}

inline fun newFile(name: String) = File(RESOURCES_DIR + name).apply { createNewFile() }

inline fun easyFormatAudioInputStream(buffer: ByteArray) =
        AudioInputStream(ByteArrayInputStream(buffer), EASY_FORMAT, buffer.size.toLong())

inline fun addSineWaves(sineWaves: List<SineWave>, seconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
    val arrays = sineWaves.map { generateSineWave(it, seconds, sampleRate) }
    return ByteArray(arrays.first().size) { i ->
        arrays.map { it[i] }.sum().toByte()
    }
}

inline fun addSineWavesEvenly(sineWaves: List<SineWave>, seconds: Double, sampleRate: Int = SAMPLE_RATE): ByteArray {
    val amplitude = 1.0 / sineWaves.size.d
    val arrays = sineWaves.map {
        it.amplitude = amplitude
        generateSineWave(it, seconds, sampleRate)
    }
    return ByteArray(arrays.first().size) { i ->
        arrays.map { it[i] }.sum().toByte()
    }
}

inline fun AudioInputStream.printStreamInfo() {
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
            Format: $format
        """.trimIndent())

    val duration = frames / frameRate
    println("Duration: $duration")
}