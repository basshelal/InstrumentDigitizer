package uk.whitecrescent.instrumentdigitizer

// TODO: 22-Jan-19 This whole thing lol
object Algorithm {

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
     */
    fun fourierTransform(data: ByteArray): ByteArray {
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
     * Used for testing,
     * Compares between 2 sets of data, the original and the converted and returns the differences,
     * the differences should ideally contain nothing useful, so either 0s or very low values that are
     * not useful in finding what the original was
     */
    fun compare(original: ByteArray, converted: ByteArray): ByteArray {
        return original + converted
    }

    /*
     * Slices the given data into the detected different frequencies found in it,
     * the returned list is a list of Pairs which contains the frequency mapped to
     * the index of the original array where it first exists
     */
    fun sliceByFrequencies(data: ByteArray): List<Pair<Long, Int>> {
        return emptyList()
    }

}

inline class Data(val data: ByteArray)