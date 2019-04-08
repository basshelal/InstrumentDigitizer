/**
 * Extensions and TypeAliases on base types, adds no new special functionality
 */
@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import Duration
import now
import org.apache.commons.math3.complex.Complex
import till
import kotlin.math.roundToInt

typealias ComplexArray = Array<Complex>
typealias ComplexMap = Map<Index, Complex>
typealias Frequency = Double
typealias Amplitude = Double
typealias Phase = Double
typealias Index = Int
typealias Seconds = Double

inline val Number.d: Double
    get() = this.toDouble()

inline val Number.i: Int
    get() = this.toInt()

inline val Number.l: Long
    get() = this.toLong()

inline val Number.f: Float
    get() = this.toFloat()

inline val Number.b: Byte
    get() = this.toByte()

inline val Number.inverse: Double
    get() = 1 / this.d

inline fun DoubleArray.toByteArray() = ByteArray(this.size) { this[it].toByte() }

inline fun DoubleArray.toIntArray() = IntArray(this.size) { this[it].roundToInt() }

inline fun <T> Iterable<T>.printEach() = this.forEach { println(it) }

inline fun measureTime(operationName: String = "", func: () -> Any): Duration {
    val start = now
    println("Starting operation $operationName at $start")
    func()
    val end = now
    println("Finishing operation $operationName at $end")
    val duration = start till end
    println("Operation $operationName took $duration")
    return duration
}