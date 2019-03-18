@file:Suppress("UNUSED_PARAMETER", "UNUSED")

// TODO: 18-Mar-19 Remove above supress statement ^

package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import org.apache.commons.math3.util.ArithmeticUtils
import kotlin.math.roundToInt

object Functions {

    /*
     * We should probably make sure that these operations are done in-place, meaning
     * we manipulate/modify the original data and not return new data.
     * We could also have an encapsulation of the data type in a new type like Data below
     */

    /*
     * Probably the order of how the final algorithm will look like, very unsure
     */
    fun execute(data: ByteArray): ByteArray {
        return data.apply {
            noiseReduction(this)
            trim(this)
            fourierTransform(this)
            modulationTransform(this)
        }
    }

    fun fourierTransform(data: ByteArray) = fourierTransform(data.toComplex())

    /*
     * Just the basic Fourier Transform to transform from Time Domain to Frequency domain
     * should probably return a list of SineWaves
     */
    fun fourierTransform(data: ComplexArray): ComplexArray {
        return FastFourierTransformer(DftNormalization.STANDARD).transform(data, TransformType.FORWARD)
    }

    fun inverseFourierTransform(data: ComplexArray): ComplexArray {
        return FastFourierTransformer(DftNormalization.STANDARD).transform(data, TransformType.INVERSE)
    }

    fun realFourierTransform(data: ByteArray): ByteArray {

        return data
    }

    /*
     * This will be a transform on the Fourier transformed data,
     * We use this to find any modulation in pitch over time, like LFOs
     */
    fun modulationTransform(data: ByteArray): ByteArray {
        return data
    }

    /*
     * Remove any values lower than a certain threshold, less than which is considered noise
     */
    fun noiseReduction(data: ByteArray): ByteArray {
        return data
    }

    /*
     * Cuts off any silence from the beginning and end of the data,
     * this is anything that is 0 after noise reduction
     */
    fun trim(data: ByteArray): ByteArray {
        return data
    }

    /*
     * Detects the single frequency of the given data
     */
    fun frequencyDetection(data: ByteArray): Long {
        return 0L
    }

    /*
     * Slices the given data into the detected different frequencies found in it,
     * the returned list is a list of Pairs which contains the frequency mapped to
     * the index of the original array where it first exists
     */
    fun sliceByFrequencies(data: ByteArray): List<Pair<Long, Int>> {
        return emptyList()
    }

    /*
     * Pads the passed in ByteArray with zeros so that it can be used in Fast Fourier Transform functions
     * that require the transform be on collections of a size that is a power of 2
     */
    fun pad(data: ByteArray, padWith: Byte = 0): ByteArray {
        val list = ArrayList(data.asList())

        // TODO: 18-Mar-19 Optimize below because with many iterations this becomes slow and CPU heavy
        // find a way to figure out the next power of two and just add that many 0s at the end or something like that
        while (!ArithmeticUtils.isPowerOfTwo(list.size.toLong())) list.add(padWith)
        return ByteArray(list.size) { list[it] }
    }

}

// Extensions and Utils

typealias ComplexArray = Array<Complex>

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class AudioData(val data: ByteArray)

fun ByteArray.padded() = Functions.pad(this)

fun ByteArray.toComplex() = ComplexArray(this.size) { Complex(this[it].toDouble(), 0.0) }

fun ByteArray.toDoubleArray() = DoubleArray(this.size) { this[it].toDouble() }

fun DoubleArray.toByteArray() = ByteArray(this.size) { this[it].toByte() }

fun DoubleArray.toIntArray() = IntArray(this.size) { this[it].roundToInt() }

fun ComplexArray.real() = DoubleArray(this.size) { this[it].real }

fun ComplexArray.imaginary() = DoubleArray(this.size) { this[it].imaginary }
