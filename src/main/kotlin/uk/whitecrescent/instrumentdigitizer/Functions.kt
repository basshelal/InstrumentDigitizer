@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import org.apache.commons.math3.util.ArithmeticUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileReader
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

inline fun fourierTransform(data: ByteArray) = fourierTransform(data.toComplexArray())

inline fun fourierTransform(data: IntArray) = fourierTransform(data.toComplexArray())

/*
 * Just the basic Fourier Transform to transform from Time Domain to Frequency domain
 * should probably return a list of SineWaves
 */
inline fun fourierTransform(data: ComplexArray): ComplexArray {
    return FastFourierTransformer(DftNormalization.STANDARD).transform(data, TransformType.FORWARD)
}

inline fun inverseFourierTransform(data: ComplexArray): ComplexArray {
    return FastFourierTransformer(DftNormalization.STANDARD).transform(data, TransformType.INVERSE)
}

/*
 * This will be a transform on the Fourier transformed data,
 * We use this to find any modulation in pitch over time, like LFOs
 */
inline fun modulationTransform(data: ByteArray): ByteArray {
    return data
}

/*
 * Cuts off any silence from the beginning and end of the data,
 * this is anything that is 0 after noise reduction
 */
inline fun trim(data: ByteArray): ByteArray {
    return data
}

/*
 * Pads the passed in ByteArray with zeros so that it can be used in Fast Fourier Transform functions
 * that require the transform be on collections of a size that is a power of 2
 */
inline fun pad(data: ByteArray, padWith: Byte = 0): ByteArray {
    val list = ArrayList(data.asList())
    val nextPowerOfTwo = nextPowerOfTwo(list.size)

    if (list.size != nextPowerOfTwo) {
        list.ensureCapacity(nextPowerOfTwo)
        list.addAll(ByteArray(nextPowerOfTwo - list.size) { padWith }.asList())
    }

    require(list.size == nextPowerOfTwo) { "Required size $nextPowerOfTwo, actual size ${list.size}" }

    return list.toByteArray()
}

/*
 * Truncates the passed in ByteArray so that it can be used in Fast Fourier Transform functions
 * that require the transform be on collections of a size that is a power of 2
 */
inline fun truncate(data: ByteArray) =
        ByteArray(previousPowerOfTwo(data.size)) { data[it] }

inline fun truncate(data: IntArray) =
        IntArray(previousPowerOfTwo(data.size)) { data[it] }

/*
 * Truncated, Transformed, Rounded, Reduced
 */
inline fun ttrr(data: ByteArray): ComplexMap {
    return data.truncated().fourierTransformed().rounded().removeZeros()
}

// The full execution that will return the minimum required data to grab the frequency of a sine Wave
inline fun fullExecution(data: ByteArray): ComplexMap {
    return data
            .truncated()            // Truncate to allow FFT
            .fourierTransformed()   // FFT, makes values Complex with 0.0 for imaginary parts
            .rounded()              // Round everything to Int to avoid tiny numbers close to 0
            .removeZeros()          // Remove entries equal to (0.0, 0.0)
            .splitInHalf()          // Get first half since data is identical in both
            .reducePartials()       // Remove unnecessary partials
}

inline fun execute(data: ByteArray, sampleRate: Int): FourierOutput {
    val size = previousPowerOfTwo(data.size)
    return data.truncated()
            .fourierTransformed()
            .rounded()
            .removeZeros()
            .splitInHalf().map {

                val index = it.key.d
                val real = it.value.real
                val imaginary = it.value.imaginary
                val frequencyCalc = (index.d / size.d) * sampleRate.d
                val amplitudeCalc = abs(hypot(imaginary, real))
                val phaseCalc = (atan2(imaginary, real)) / PI

                return@map FourierEntry(
                        index = index.i,
                        real = real,
                        imaginary = imaginary,
                        frequency = frequencyCalc,
                        amplitude = amplitudeCalc,
                        phase = phaseCalc
                )
            }.toFourierOutput()
}

inline fun nextPowerOfTwo(number: Int): Int {
    val lg = log2(number.d)

    val ceiled = ceil(lg).i

    val result = 2.0.pow(ceiled)

    assert(ArithmeticUtils.isPowerOfTwo(result.l))
    return result.i
}

inline fun previousPowerOfTwo(number: Int): Int {
    val lg = log2(number.d)

    val floored = floor(lg).i

    val result = 2.0.pow(floored)

    assert(ArithmeticUtils.isPowerOfTwo(result.l))
    return result.i
}

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

inline fun generateIntSineWave(frequency: Frequency, amplitude: Amplitude = 1.0, phase: Phase = 0.0,
                               seconds: Seconds, sampleRate: Int = SAMPLE_RATE, channels: Int = 1): IntArray {

    val maxAmplitude = amplitude * maxInt
    val totalSamples = (seconds * sampleRate.d * channels.d).i
    val period = sampleRate.d / frequency
    val phaseShift = phase * PI

    return IntArray(totalSamples) {
        val angle = (2.0 * PI * it) / period
        /*Amp * sin(2 * PI * f * t + phase)*/
        return@IntArray (maxAmplitude * cos(angle + phaseShift)).i
    }
}

inline fun generateIntSineWave(sineWave: SineWave, seconds: Seconds,
                               sampleRate: Int = SAMPLE_RATE, channels: Int = 1) =
        generateIntSineWave(sineWave.frequency, sineWave.amplitude, sineWave.phase, seconds, sampleRate, channels)

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
inline fun generateSineWave(frequency: Frequency, amplitude: Amplitude = 1.0, phase: Phase = 0.0,
                            seconds: Seconds, sampleRate: Int = SAMPLE_RATE, channels: Int = 1): ByteArray {

    val maxAmplitude = amplitude * MAX_AMPLITUDE
    val totalSamples = (seconds * sampleRate.d * channels.d).i
    val period = sampleRate.d / frequency
    val phaseShift = phase * PI

    return ByteArray(totalSamples) {
        val angle = (2.0 * PI * it) / period
        /*Amp * cos(2 * PI * f * t + phase)*/
        return@ByteArray (maxAmplitude * cos(angle + phaseShift)).b
    }
}

inline fun generateSineWave(sineWave: SineWave, seconds: Seconds,
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
    stream.close()
}

inline fun readFromWaveFile(fileName: String): ByteArray {
    val file = newFile("$fileName.wav")
    val stream = AudioSystem.getAudioInputStream(EASY_FORMAT, AudioSystem.getAudioInputStream(file))
    val buffer = ByteArray(stream.frameLength.i)

    stream.read(buffer)

    stream.close()
    return buffer
}

inline fun newFile(name: String) = File(RESOURCES_DIR + name).apply { createNewFile() }

inline fun easyFormatAudioInputStream(buffer: ByteArray) =
        AudioInputStream(ByteArrayInputStream(buffer), EASY_FORMAT, buffer.size.l)

inline fun addSineWaves(sineWaves: List<SineWave>, seconds: Seconds, sampleRate: Int = SAMPLE_RATE): ByteArray {
    val arrays = sineWaves.map { generateSineWave(it, seconds, sampleRate) }
    return ByteArray(arrays.first().size) { i ->
        arrays.map { it[i] }.sum().toByte()
    }
}

inline fun addSineWavesEvenly(sineWaves: List<SineWave>, seconds: Seconds, sampleRate: Int = SAMPLE_RATE): ByteArray {
    val amplitude = 1.0 / sineWaves.size.d
    val arrays = sineWaves.map {
        it.amplitude = amplitude
        generateSineWave(it, seconds, sampleRate)
    }
    return ByteArray(arrays.first().size) { i ->
        arrays.map { it[i] }.sum().toByte()
    }
}

/**
 * Convert amplitude to decibels. 1.0 is zero dB. 0.5 is -6.02 dB.
 */
inline fun amplitudeToDecibels(amplitude: Amplitude): Double {
    return (ln(amplitude) * 20) / ln(10.0)
}

/**
 * Convert decibels to amplitude. Zero dB is 1.0 and -6.02 dB is 0.5.
 */
inline fun decibelsToAmplitude(decibels: Double): Double {
    return 10.0.pow(decibels / 20.0)
}

inline fun readAllInstruments(): List<Instrument> {
    return gson.fromJson<List<Instrument>>(FileReader(File(INSTRUMENTS_FILE)), List::class.java)
            ?: emptyList()
}

inline fun saveInstrument(instrument: Instrument) {
    writeInstruments(
            readAllInstruments().toMutableList().apply { add(instrument) }.distinctBy { it.name }
    )
}

inline fun deleteInstrument(instrument: Instrument) {
    writeInstruments(
            readAllInstruments().toMutableList().apply { remove(instrument) }.distinctBy { it.name }
    )
}

inline fun writeInstruments(instruments: List<Instrument>) {
    File(INSTRUMENTS_FILE).apply {
        writeText(gson.toJson(instruments))
    }
}

class FourierOutput(val fourierEntries: List<FourierEntry>) {

    val reals = mutableMapOf<Index, Double>()

    val imaginaries = mutableMapOf<Index, Double>()

    val frequencies = mutableMapOf<Index, Frequency>()

    val amps = mutableMapOf<Index, Amplitude>()

    val phases = mutableMapOf<Index, Phase>()

    init {
        fourierEntries.forEach {
            reals[it.index] = it.real
            imaginaries[it.index] = it.imaginary
            frequencies[it.index] = it.frequency
            amps[it.index] = it.amplitude
            phases[it.index] = it.phase
        }
    }
}

inline fun List<FourierEntry>.toFourierOutput() = FourierOutput(this)

data class FourierEntry(val index: Index,
                        val real: Double,
                        val imaginary: Double,
                        val frequency: Frequency,
                        val amplitude: Amplitude,
                        val phase: Phase)