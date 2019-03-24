@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import kotlin.math.roundToInt

typealias ComplexArray = Array<Complex>

typealias ComplexMap = Map<Int, Complex>

inline fun ByteArray.padded() = Functions.pad(this)

inline fun ByteArray.truncated() = Functions.truncate(this)

inline fun ByteArray.toComplex() = ComplexArray(this.size) { Complex(this[it].toDouble(), 0.0) }

inline fun ByteArray.paddedComplex() = Functions.pad(this).toComplex()

inline fun ByteArray.toDoubleArray() = DoubleArray(this.size) { this[it].toDouble() }

inline fun ByteArray.fourierTransformed() = Functions.fourierTransform(this)

inline fun ByteArray.unicorn() = Functions.unicorn(this)


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

inline fun ComplexMap.getPartials(amount: Int = this.size) = sortedByRealDescending().toList().take(amount).toMap()

inline fun ComplexMap.reduceInsignificantPartials() = getPartials()
        .filterValues { (it.real / maxReal!!.value.real) > 0.1 }

inline fun ComplexMap.splitInHalf() = toList().take(size / 2).toMap()