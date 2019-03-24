package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import org.apache.commons.math3.util.ArithmeticUtils

object Functions {

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
    fun truncate(data: ByteArray): ByteArray {
        val list = ArrayList<Byte>()
        val previousPowerOfTwo = previousPowerOfTwo(data.size)

        list.ensureCapacity(previousPowerOfTwo)
        (0 until previousPowerOfTwo).forEach {
            list.add(data[it])
        }

        require(list.size == previousPowerOfTwo) { "Required size $previousPowerOfTwo, actual size ${list.size}" }

        return list.toByteArray()
    }

    fun unicorn(data: ByteArray): Map<Int, Complex> {
        return data.truncated().fourierTransformed().rounded().reduced()
    }

    private fun nextPowerOfTwo(number: Int): Int {
        var result = number
        while (!ArithmeticUtils.isPowerOfTwo(result.toLong())) result++
        return result
    }

    private fun previousPowerOfTwo(number: Int): Int {
        var result = number
        while (!ArithmeticUtils.isPowerOfTwo(result.toLong())) result--
        return result
    }

}