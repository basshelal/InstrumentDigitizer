@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import javax.sound.sampled.AudioSystem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

typealias ComplexArray = Array<Complex>

typealias ComplexMap = Map<Int, Complex>


inline fun ByteArray.fullExecution() = Functions.fullExecution(this)

inline fun ByteArray.padded() = Functions.pad(this)

inline fun ByteArray.truncated() = Functions.truncate(this)

inline fun ByteArray.toComplexArray() = ComplexArray(this.size) { Complex(this[it].toDouble(), 0.0) }

inline fun ByteArray.toDoubleArray() = DoubleArray(this.size) { this[it].toDouble() }

inline fun ByteArray.fourierTransformed() = Functions.fourierTransform(this)

inline fun ByteArray.ttrr() = Functions.ttrr(this)

inline infix fun ByteArray.add(other: ByteArray): ByteArray {
    val largerSize = max(this.size, other.size)
    val smallerSize = min(this.size, other.size)
    val biggerArray = if (this.size == largerSize) this else other

    return ByteArray(largerSize) {
        if (it < smallerSize) (this[it] + other[it]).toByte()
        else (0 + biggerArray[it]).toByte()
    }
}

inline fun DoubleArray.toByteArray() = ByteArray(this.size) { this[it].toByte() }

inline fun DoubleArray.toIntArray() = IntArray(this.size) { this[it].roundToInt() }


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
inline fun ComplexArray.rounded() = map { Complex(it.real.roundToInt().toDouble(), it.imaginary.roundToInt().toDouble()) }.toTypedArray()

/**
 * Maps each index in this [ComplexArray] to its corresponding Complex number
 */
inline fun ComplexArray.mapIndexed() = mapIndexed { index, complex -> index to complex }.toMap()

/**
 * Reduces the elements in this [ComplexMap] such that any Complex number with both real and imaginary parts
 * equalling to 0.0 after being [rounded], is removed
 */
inline fun ComplexArray.reduced() = rounded()
        .mapIndexed().filterValues { it.real != 0.0 || it.imaginary != 0.0 }


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


inline fun SineWave.play(seconds: Int = 2) {
    val buffer = generateSineWave(this, seconds)
    val format = EASY_FORMAT
    val line = AudioSystem.getSourceDataLine(format)
    line.apply {
        open(format)
        start()
        write(buffer, 0, buffer.size)
        drain()
        close()
    }
}

inline fun ByteArray.play() {
    val buffer = this
    val format = EASY_FORMAT
    val line = AudioSystem.getSourceDataLine(format)
    line.apply {
        open(format)
        start()
        write(buffer, 0, buffer.size)
        drain()
        close()
    }
}

inline fun Instrument.play(frequency: Double = 440.0, amplitude: Double = 1.0, seconds: Int = 2) {
    addSineWaves(overtoneRatios.toSineWaves(frequency, amplitude), seconds).play()
}

inline fun Instrument.play(key: Key, amplitude: Double = 1.0, seconds: Int = 1) {
    play(key.frequency, amplitude, seconds)
}

inline fun List<OvertoneRatio>.toSineWaves(fundamentalFrequency: Double, fundamentalAmplitude: Double = 1.0): List<SineWave> {
    require(this.isNotEmpty())
    return map { SineWave(fundamentalFrequency * it.frequencyRatio, fundamentalAmplitude * it.amplitudeRatio, it.phase) }
}

inline fun List<SineWave>.sortedByFrequency() = sortedBy { it.frequency }

inline fun List<SineWave>.getFrequencyRatiosToFundamental(): List<Double> {
    require(this.isNotEmpty()) { "List cannot be empty" }
    return sortedByFrequency().map { it.frequency / this[0].frequency }
}