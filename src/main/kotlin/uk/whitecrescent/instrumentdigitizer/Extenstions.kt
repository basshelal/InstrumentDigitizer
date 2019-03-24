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


inline fun ComplexArray.real() = DoubleArray(this.size) { this[it].real }

inline fun ComplexArray.imaginary() = DoubleArray(this.size) { this[it].imaginary }

inline fun ComplexArray.toMap() = map { it.real to it.imaginary }.toMap()

inline fun ComplexArray.toIntMap() = map { it.real.roundToInt() to it.imaginary.roundToInt() }.toMap()

inline fun ComplexArray.rounded() = map { Complex(it.real.roundToInt().toDouble(), it.imaginary.roundToInt().toDouble()) }.toTypedArray()

inline fun ComplexArray.reduced() = rounded()
        .mapIndexed { index, complex -> index to complex }.toMap()
        .filterValues { it.real != 0.0 || it.imaginary != 0.0 }

inline fun ComplexMap.sortedByIndex() = toList().sortedBy { it.first }.toMap()

inline fun ComplexMap.sortedByMaxReal() = toList().sortedByDescending { it.second.real }.toMap()

inline fun ComplexMap.sortedByMaxImaginary() = toList().sortedByDescending { it.second.imaginary }.toMap()

inline fun ComplexMap.maxReal() = maxBy { it.value.real }

inline fun ComplexMap.minReal() = minBy { it.value.real }

inline fun ComplexMap.maxImaginary() = maxBy { it.value.imaginary }

inline fun ComplexMap.minImaginary() = minBy { it.value.imaginary }

inline fun ComplexMap.getPartials(amount: Int = this.size) = sortedByMaxReal().toList().take(amount).toMap()

inline fun ComplexMap.reduceInsignificantPartials() = getPartials()
        .filterValues { (it.real / maxReal()!!.value.real) > 0.1 }

inline fun ComplexMap.splitInHalf() = toList().take(size / 2).toMap()