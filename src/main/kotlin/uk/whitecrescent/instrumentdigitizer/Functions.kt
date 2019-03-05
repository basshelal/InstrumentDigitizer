package uk.whitecrescent.instrumentdigitizer

import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.sin

// TODO: 22-Jan-19 This whole thing lol
object Functions {

    // TODO: 22-Jan-19 Still unsure about the data type of ByteArray

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

    /*
     * Just the basic Fourier Transform to transform from Time Domain to Frequency domain
     * should probably return a list of SineWaves
     */
    fun fourierTransform(data: ByteArray): ComplexArray {
        val complexArray = ComplexArray(data.size) { Complex(data[it].toDouble(), 0.0) }
        return FastFourierTransformer(DftNormalization.STANDARD).transform(complexArray, TransformType.FORWARD)
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
     * Used for testing,
     * Compares between 2 sets of data, the original and the converted and returns the differences,
     * the differences should ideally contain nothing useful, so either 0s or very low values that are
     * not useful in finding what the original was
     */
    fun compare(original: ByteArray, converted: ByteArray): ByteArray {
        return original
    }

    /*
     * Slices the given data into the detected different frequencies found in it,
     * the returned list is a list of Pairs which contains the frequency mapped to
     * the index of the original array where it first exists
     */
    fun sliceByFrequencies(data: ByteArray): List<Pair<Long, Int>> {
        return emptyList()
    }

    fun synthesizeSingleSineWave(size: Int): ByteArray {
        val result = ByteArray(size) {
            sin(5.0).toByte()
        }

        //sin(2*PI*f*t+ p)

        /*val fs = 512; // Sampling frequency (samples per second)
        val dt = 1 / fs; // seconds per sample
        val StopTime = 0.25; // seconds
        val t = (0:dt:StopTime); // seconds
        val F = 60; // Sine wave frequency (hertz)
        val data = sin(2 * PI * F * t);
        //For one cycle get time period
        val T = 1 / F;
        // time step for one time period
        val tt = 0:dt:T+dt ;
        val d = sin(2 * PI * F * tt);*/
        return result
    }

}

typealias ComplexArray = Array<Complex>

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class AudioData(val data: ByteArray)