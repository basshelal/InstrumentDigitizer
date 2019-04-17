/**
 * Functions that perform operations as inline extensions since I prefer to use postfix notation
 * over prefix with brackets
 */
@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

inline fun ByteArray.fullExecution() = fullExecution(this)

inline fun ByteArray.getFrequencies() = fullExecution()
        .map { (it.key.d / previousPowerOfTwo(size).d) * SAMPLE_RATE.d }
        .sorted()

inline fun ByteArray.getFrequenciesDistinct() = getFrequencies().map { it.i }.distinct()

inline fun ByteArray.padded() = pad(this)

inline fun ByteArray.truncated() = truncate(this)

inline fun IntArray.truncated() = truncate(this)

inline fun ByteArray.toComplexArray() = ComplexArray(this.size) { Complex(this[it].d, 0.0) }

inline fun IntArray.toComplexArray() = ComplexArray(this.size) { Complex(this[it].d, 0.0) }

inline fun ByteArray.toDoubleArray() = DoubleArray(this.size) { this[it].d }

inline fun ByteArray.fourierTransformed() = fourierTransform(this)

inline fun IntArray.fourierTransformed() = fourierTransform(this)

inline fun ByteArray.ttrr() = ttrr(this)

inline infix fun ByteArray.add(other: ByteArray): ByteArray {
    val largerSize = max(this.size, other.size)
    val smallerSize = min(this.size, other.size)
    val biggerArray = if (this.size == largerSize) this else other

    return ByteArray(largerSize) {
        if (it < smallerSize) (this[it] + other[it]).toByte()
        else (0 + biggerArray[it]).toByte()
    }
}


/**
 * Returns the real parts of all the Complex numbers in this [ComplexArray]
 */
inline fun ComplexArray.real() = DoubleArray(this.size) { this[it].real }

/**
 * Returns the imaginary parts of all the Complex numbers in this [ComplexArray]
 */
inline fun ComplexArray.imaginary() = DoubleArray(this.size) { this[it].imaginary }

/**
 * Returns this [ComplexArray] as a [Map] mapping the real to the imaginary parts of each
 * Complex number in this [ComplexArray]
 */
inline fun ComplexArray.toMap() = map { it.real to it.imaginary }.toMap()

/**
 * Returns this [ComplexArray] as a [Map] mapping the real to the imaginary parts of each
 * Complex number in this [ComplexArray] but both being rounded to Ints
 */
inline fun ComplexArray.toIntMap() = map { it.real.roundToInt() to it.imaginary.roundToInt() }.toMap()

/**
 * Returns this [ComplexArray] with the parts of each Complex value in it rounded to the nearest Int
 */
inline fun ComplexArray.rounded(): ComplexArray {
    forEachIndexed { i, it ->
        this[i] = Complex(it.real.roundToInt().d, it.imaginary.roundToInt().d)
    }
    return this
}

/**
 * Maps each index in this [ComplexArray] to its corresponding Complex number
 */
inline fun ComplexArray.mapIndexed(): ComplexMap = mapIndexed { index, complex -> index to complex }.toMap()

/**
 * Reduces the elements in this [ComplexMap] such that any Complex number with both real and imaginary parts
 * equalling to 0.0 after being [rounded], is removed
 */
inline fun ComplexArray.removeZeros(): ComplexMap {
    val result = HashMap<Int, Complex>()
    forEachIndexed { index, it ->
        if (it.real != 0.0 || it.imaginary != 0.0) result.put(index, it)
    }
    return result
}


/**
 * Sorts this [ComplexMap] by Index, ie the keys in this Map
 */
inline fun ComplexMap.sortedByIndex() = toList().sortedBy { it.first }.toMap()

/**
 * Sorts this [ComplexMap] by the real parts of the values in this Map, in a descending order
 * meaning the entry with the largest real part will be first
 */
inline fun ComplexMap.sortedByRealDescending() = toList().sortedByDescending { it.second.real }.toMap()

/**
 * Sorts this [ComplexMap] by the imaginary parts of the values in this Map, in a descending order
 * meaning the entry with the largest imaginary part will be first
 */
inline fun ComplexMap.sortedByImaginaryDescending() = toList().sortedByDescending { it.second.imaginary }.toMap()

/**
 * Returns the first entry in this [ComplexMap] with the maximum real part
 */
inline val ComplexMap.maxReal get() = maxBy { it.value.real }

/**
 * Returns the first entry in this [ComplexMap] with the minimum real part
 */
inline val ComplexMap.minReal get() = minBy { it.value.real }

/**
 * Returns the first entry in this [ComplexMap] with the maximum imaginary part
 */
inline val ComplexMap.maxImaginary get() = maxBy { it.value.imaginary }

/**
 * Returns the first entry in this [ComplexMap] with the minimum imaginary part
 */
inline val ComplexMap.minImaginary get() = minBy { it.value.imaginary }

/**
 * Gets the first [amount] of entries in this [ComplexMap] after being [sortedByRealDescending]
 */
inline fun ComplexMap.getPartials(amount: Int = this.size) = sortedByRealDescending().toList().take(amount).toMap()

/**
 * Removes any partials that have a ratio less than the [threshold] when divided by the max real value, usually these
 * are partials that represent noise or insignificant sounds
 */
inline fun ComplexMap.reducePartials(threshold: Double = 0.1) = getPartials()
        .filterValues { (it.real / maxReal!!.value.real) > threshold }

/**
 * Splits this [ComplexMap] in half returning only the first half
 */
inline fun ComplexMap.splitInHalf() = toList().take(size / 2).toMap()


inline fun SineWave.play(seconds: Seconds = 2.0) {
    generateSineWave(this, seconds).play()
}

inline fun ByteArray.play() {
    val format = EASY_FORMAT
    AudioSystem.getSourceDataLine(format)
            .apply {
                open(format)
                start()
                write(this@play, 0, this@play.size)
                //drain()
                //flush()
            }.close()
}

inline fun IntArray.toByteArrayScaled(): ByteArray {
    return ByteArray(this.size) { ((this[it].d / maxInt.d) * maxByte.d).b }
}

inline fun IntArray.play() {
    val format = EASY_FORMAT
    AudioSystem.getSourceDataLine(format)
            .apply {
                open(format)
                start()
                write(this@play.toByteArrayScaled(), 0, this@play.size)
                //drain()
                //flush()
            }.close()
}

inline fun Instrument.getByteArray(frequency: Frequency = 440.0, amplitude: Amplitude = 1.0, seconds: Seconds = 2.0) =
        addSineWaves(overtoneRatios.toSineWaves(frequency, amplitude), seconds)

inline fun Instrument.play(frequency: Frequency = 440.0, amplitude: Amplitude = 1.0, seconds: Seconds = 2.0) {
    addSineWaves(overtoneRatios.toSineWaves(frequency, amplitude), seconds).play()
}

inline fun Instrument.play(key: Key, amplitude: Amplitude = 1.0, seconds: Seconds = 1.0) {
    play(key.frequency, amplitude, seconds)
}

inline fun Instrument.play(keys: Map<Key, Seconds>) {
    val format = EASY_FORMAT
    val list = ArrayList<Byte>()
    keys.forEach {
        list.addAll(addSineWaves(overtoneRatios.toSineWaves(it.key.frequency), it.value).asList())
    }
    val buffer = list.toByteArray()
    println(buffer.size / SAMPLE_RATE)
    AudioSystem.getSourceDataLine(format)
            .apply {
                open(format)
                start()
                write(buffer, 0, buffer.size)
            }.close()
}

inline fun List<OvertoneRatio>.toSineWaves(fundamentalFrequency: Frequency, fundamentalAmplitude: Amplitude = 1.0):
        List<SineWave> {
    require(this.isNotEmpty())
    return map { SineWave(fundamentalFrequency * it.frequencyRatio, fundamentalAmplitude * it.amplitude, it.phase) }
}

inline fun List<SineWave>.sortedByFrequency() = sortedBy { it.frequency }

inline fun List<SineWave>.addAllSineWaves(seconds: Seconds = 1.0, sampleRate: Int = SAMPLE_RATE) =
        addSineWaves(this, seconds, sampleRate)

inline fun List<SineWave>.addAllSineWavesEvenly(seconds: Seconds = 1.0, sampleRate: Int = SAMPLE_RATE) =
        addSineWavesEvenly(this, seconds, sampleRate)

inline fun List<SineWave>.getFrequencyRatiosToFundamental(): List<Frequency> {
    require(this.isNotEmpty()) { "List cannot be empty" }
    return sortedByFrequency().map { it.frequency / this[0].frequency }
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

inline fun <T> Iterable<T>.writeTextToFile(lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        forEach {
            appendText("$it$lineEnd")
        }
    }
}

inline fun <T> Iterable<T>.writeTextToFileIndexed(delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        forEachIndexed { index, it ->
            appendText("$index$delimiter$it$lineEnd")
        }
    }
}

inline fun ComplexArray.writeTextToFile(delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        forEach {
            appendText("${it.real}$delimiter${it.imaginary}$lineEnd")
        }
    }
}

inline fun <K, V> Map<K, V>.writeTextToFile(delimiter: String = ",", lineEnd: String = "\n", outPath: String = OUTPUT_PATH_OUT) {
    File(outPath).apply {
        writeText("")
        forEach {
            appendText("${it.key}$delimiter${it.value}$lineEnd")
        }
    }
}