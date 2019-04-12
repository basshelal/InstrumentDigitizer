package tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.instrumentdigitizer.BASIC_INSTRUMENT
import uk.whitecrescent.instrumentdigitizer.DESIRED_DIFFERENCE
import uk.whitecrescent.instrumentdigitizer.Frequency
import uk.whitecrescent.instrumentdigitizer.HALF_PI
import uk.whitecrescent.instrumentdigitizer.Index
import uk.whitecrescent.instrumentdigitizer.Key
import uk.whitecrescent.instrumentdigitizer.MAX_AMPLITUDE
import uk.whitecrescent.instrumentdigitizer.Note
import uk.whitecrescent.instrumentdigitizer.Octave
import uk.whitecrescent.instrumentdigitizer.Phase
import uk.whitecrescent.instrumentdigitizer.SAMPLE_RATE
import uk.whitecrescent.instrumentdigitizer.SAMPLE_RATE_POWER_OF_TWO
import uk.whitecrescent.instrumentdigitizer.addAllSineWavesEvenly
import uk.whitecrescent.instrumentdigitizer.addSineWaves
import uk.whitecrescent.instrumentdigitizer.addSineWavesEvenly
import uk.whitecrescent.instrumentdigitizer.d
import uk.whitecrescent.instrumentdigitizer.fourierTransform
import uk.whitecrescent.instrumentdigitizer.fourierTransformed
import uk.whitecrescent.instrumentdigitizer.fullExecution
import uk.whitecrescent.instrumentdigitizer.generateIntSineWave
import uk.whitecrescent.instrumentdigitizer.generateSineWave
import uk.whitecrescent.instrumentdigitizer.generateTwoSineWaves
import uk.whitecrescent.instrumentdigitizer.getFrequenciesDistinct
import uk.whitecrescent.instrumentdigitizer.i
import uk.whitecrescent.instrumentdigitizer.inverseFourierTransform
import uk.whitecrescent.instrumentdigitizer.label
import uk.whitecrescent.instrumentdigitizer.mapIndexed
import uk.whitecrescent.instrumentdigitizer.maxDouble
import uk.whitecrescent.instrumentdigitizer.maxImaginary
import uk.whitecrescent.instrumentdigitizer.maxReal
import uk.whitecrescent.instrumentdigitizer.minComplex
import uk.whitecrescent.instrumentdigitizer.minDouble
import uk.whitecrescent.instrumentdigitizer.minImaginary
import uk.whitecrescent.instrumentdigitizer.minReal
import uk.whitecrescent.instrumentdigitizer.padded
import uk.whitecrescent.instrumentdigitizer.play
import uk.whitecrescent.instrumentdigitizer.previousPowerOfTwo
import uk.whitecrescent.instrumentdigitizer.printEach
import uk.whitecrescent.instrumentdigitizer.printLine
import uk.whitecrescent.instrumentdigitizer.readFromWaveFile
import uk.whitecrescent.instrumentdigitizer.reducePartials
import uk.whitecrescent.instrumentdigitizer.removeZeros
import uk.whitecrescent.instrumentdigitizer.rounded
import uk.whitecrescent.instrumentdigitizer.sineWave
import uk.whitecrescent.instrumentdigitizer.splitInHalf
import uk.whitecrescent.instrumentdigitizer.toComplexArray
import uk.whitecrescent.instrumentdigitizer.toIntMap
import uk.whitecrescent.instrumentdigitizer.truncated
import uk.whitecrescent.instrumentdigitizer.ttrr
import uk.whitecrescent.instrumentdigitizer.writeSineWaveAudio
import uk.whitecrescent.instrumentdigitizer.writeToWaveFile
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.hypot

@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Test Write Data")
    @Test
    fun testWriteData() {
        writeSineWaveAudio()
    }

    @DisplayName("Test Generate Sine Wave")
    @Test
    fun testGenerateSineWave() {
        val wave = generateSineWave(220.0, 1.0, 0.0, 1.0, 1000, 1)
        wave.forEach { println(it) }

        assertEquals(1000, wave.size)
    }

    @DisplayName("Test Play Sine Wave")
    @Test
    fun testPlaySineWave() {
        val buffer = generateSineWave(440.0, 1.0, 0.5, 2.0, SAMPLE_RATE, 1)
        val format = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)
        val line = AudioSystem.getSourceDataLine(format)
        line.apply {
            open(format)
            start()
            write(buffer, 0, buffer.size)
            drain()
            close()
        }
    }

    @DisplayName("Test Play 2 Sine Waves")
    @Test
    fun testPlay2SineWaves() {
        val buffer = generateTwoSineWaves(440, 220, 0.0, 0.5, 2.0, SAMPLE_RATE, 1)
        val format = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)
        val line = AudioSystem.getSourceDataLine(format)
        line.apply {
            open(format)
            start()
            write(buffer, 0, buffer.size)
            drain()
            close()
        }
    }

    @DisplayName("Test Fourier Forward and Inverse")
    @Test
    fun testFourierForwardAndInverse() {
        val buffer = generateSineWave(220.0, 1.0, 0.0, 10.0, SAMPLE_RATE, 2)

        val original = buffer.padded()
        val originalComplex = original.toComplexArray()
        val transformed = fourierTransform(original)
        val inversed = inverseFourierTransform(transformed)

        assertTrue(originalComplex.size == transformed.size)
        assertTrue(transformed.size == inversed.size)

        (0 until originalComplex.size).forEach {
            assertTrue((originalComplex[it].real - inversed[it].real).absoluteValue <= DESIRED_DIFFERENCE)
            assertTrue((originalComplex[it].imaginary - inversed[it].imaginary).absoluteValue <= DESIRED_DIFFERENCE)
        }

        //transformed.take(256).toTypedArray().toIntMap().toList().forEach { println(it) }

        val hashSetK = hashSetOf(*transformed.asList().toTypedArray().toIntMap().map { it.key }.toTypedArray())
        val hashSetV = hashSetOf(*transformed.asList().toTypedArray().toIntMap().map { it.value }.toTypedArray())

        println(hashSetK.size)
        println(hashSetV.size)
        println(transformed.size)

        //result.map { it.real * (1000.0 / result.size) }.forEach { println(it) }
    }

    @DisplayName("Test Write Sine to CSV")
    @Test
    fun testWriteSineToCSV() {
        val buffer = generateSineWave(440.0, 1.0, 0.0, 1.0, 1024, 1)

        val original = buffer.padded()
        val originalComplex = original.toComplexArray()
        val transformed = fourierTransform(original)
        val inversed = inverseFourierTransform(transformed)

        /*writeTextToFile(buffer, outPath = RESOURCES_DIR + "SineWave.out")
        writeTextToFile(transformed, outPath = RESOURCES_DIR + "Transformed.out")*/

        println(transformed.map { it.real }.max())
        println(transformed.map { it.imaginary }.max())

    }

    @DisplayName("Test Write To Wave File")
    @Test
    fun testWriteToWaveFile() {
        val sineWave = sineWave(440, 1.0, 0.5)
        val buffer = generateSineWave(sineWave, 10.0, SAMPLE_RATE, 1)
        writeToWaveFile(buffer, "MyFile")
        val readBuffer = readFromWaveFile("MyFile")

        assertEquals(buffer.size, readBuffer.size)
        (0 until buffer.size).forEachIndexed { index, it ->
            assertEquals(buffer[it], readBuffer[it])
        }
    }

    @DisplayName("Test Full Execution")
    @Test
    fun testFullExecution() {
        val sineWave = generateSineWave(440.0, 1.0, 0.5, 1.0, 1024, 1)

        val ttrr = sineWave.ttrr()

        ttrr.forEach {
            println(it)
        }

        println()

        val maxReal = ttrr.maxReal
        val minReal = ttrr.minReal
        val maxImaginary = ttrr.maxImaginary
        val minImaginary = ttrr.minImaginary

        println("Max Real is ${maxReal?.value} at index ${maxReal?.key}")
        println("Min Real is ${minReal?.value} at index ${minReal?.key}")
        println("Max Imag is ${maxImaginary?.value} at index ${maxImaginary?.key}")
        println("Min Imag is ${minImaginary?.value} at index ${minImaginary?.key}")

        println()

        ttrr.forEach { index, complex ->
            val left = ttrr[index]
            val right = ttrr[1024 - index]

            assertEquals(left, right)

            println("Left @$index: $left == Right @${1024 - index}: $right")
        }

        println()

        ttrr.splitInHalf().reducePartials().forEach {
            println(it)
        }

        println()

        generateTwoSineWaves(440, 220, 0.5, 0.5, 1.0, 1024, 1)
                .fullExecution()
                .forEach {
                    println(it)
                }

        // TODO: 24-Mar-19 Why are there 2 maxReals? 440 and 584 (584 == 1024 - 440)
        // in fact why does each real number have 2 instances of itself?

        // we can ignore any values after the second half of the indexes, since there will always be an identical on
        // the first half


        // TODO: 24-Mar-19 Idea! Make an algorithm that shifts a sine wave to make it have perfect results like above
        // any old sine wave will return weird results because of the phase shift, but if we make an algorithm that
        // can determine how much to shift the phase to get the perfect results we would make our lives a lot easier.
        // The shifting would find the position to start from, if we reach the end we use the values that were before
        // our new start point
        // D, E, F, X, I, J, T, R (X is our desired start point)
        // X, I, J, T, R, D, E, F (after phase shift transform)
    }

    @DisplayName("Test Large Full Execution")
    @Test
    fun testLargeFullExecution() {
        val sineWave = sineWave(440, 1.0, 0.5)
        val buffer = generateSineWave(sineWave, 1.0, 65536)

        buffer.fullExecution().forEach { println(it) }

        sineWave.play()
    }

    @DisplayName("Test Add Sine Waves")
    @Test
    fun testAddSineWaves() {
        val list = listOf(
                sineWave(220, 0.1, 0.5),
                sineWave(440, 0.1, 0.5),
                sineWave(880, 0.1, 0.5),
                sineWave(1320, 0.1, 0.5),
                sineWave(660, 0.1, 0.5),
                sineWave(110, 0.1, 0.5)
        )
        val buffer = addSineWaves(list, 5.0)

        buffer.play()

        buffer.fullExecution().forEach {
            println(it)
        }

    }

    @DisplayName("Test Basic Sine Wave Full Execution")
    @Test
    fun testBasicSineWaveFullExecution() {
        val freq = 440

        val sineWave = sineWave(freq, 0.5, 1.0)

        val data = generateSineWave(sineWave, 1.0, SAMPLE_RATE, 1)

        val ratio = SAMPLE_RATE.d / freq.d

        val inverseRatio = freq.d / SAMPLE_RATE.d

        val size = previousPowerOfTwo(data.size)

        data.truncated()                // Truncate to allow FFT
                .fourierTransformed()   // FFT, makes values Complex with 0.0 for imaginary parts
                .rounded()              // Round everything to Int to avoid tiny numbers close to 0
                .removeZeros()              // Remove entries equal to (0.0, 0.0)
                .splitInHalf()          // Get first half since data is identical in both
                .reducePartials()       // Remove unnecessary partials
                .forEach {
                    val calculatedRatio = size.d / it.key.d
                    val calculatedFreq = (it.key.d / size.d) * SAMPLE_RATE.d

                    println(it)

                    println()

                    println("Calculated ratio:\t $calculatedRatio")
                    println("Actual ratio:\t\t $ratio")

                    println("Ratio Error:\t\t ${abs(ratio - calculatedRatio)}")

                    println()

                    println("Calculated frequency :\t $calculatedFreq")
                    println("Actual frequency :\t\t ${inverseRatio * SAMPLE_RATE.d} ")

                    println("Frequency Error:\t\t ${abs((inverseRatio * SAMPLE_RATE.d) - calculatedFreq)}")

                    println()

                    val atan = atan2(it.value.imaginary, it.value.real)
                    val phase = (atan + (PI / 2.0)) / PI
                    println("Atan: $atan")
                    println("Phase: $phase")

                }

        sineWave.play()
    }

    @DisplayName("Find Perfect Phase Shift")
    @Test
    fun testFindPerfectPhaseShift() {
        val freq = 440

        val sineWave = sineWave(freq, 0.5, 0.0)

        (0 until 100).forEach {
            val phase = it.d / 1000.0
            sineWave.phase = phase

            generateSineWave(sineWave, 1.0)
                    .fullExecution().forEach {
                        if (it.value.imaginary == 0.0) {
                            println("Success at phase $phase")
                            println(it)
                            println()
                        }
                    }
        }
    }

    @DisplayName("Test Multiple Sine Waves Full Execution")
    @Test
    fun testMultipleSineWavesFullExecution() {

        val sineWave1 = sineWave(220, 0.4, 0.5)
        val sineWave2 = sineWave(440, 0.3, 0.5)
        val sineWave3 = sineWave(660, 0.1, 0.5)
        val sineWave4 = sineWave(880, 0.1, 0.5)
        val sineWave5 = sineWave(1220, 0.05, 0.5)
        val sineWave6 = sineWave(1360, 0.05, 0.5)

        val sineWaves = listOf(sineWave1, sineWave2, sineWave3, sineWave4, sineWave5, sineWave6)

        val data = addSineWavesEvenly(sineWaves, 2.0)

        data.getFrequenciesDistinct().printEach()

        data.play()
    }

    @DisplayName("Test Sample Instrument")
    @Test
    fun testSampleInstrument() {
        BASIC_INSTRUMENT.apply {
            play(mapOf(
                    Key(Note.A, Octave.FOUR) to 0.75,
                    Key(Note.As, Octave.FOUR) to 0.75,
                    Key(Note.C, Octave.FIVE) to 0.75,
                    Key(Note.D, Octave.FIVE) to 0.75,
                    Key(Note.C, Octave.FIVE) to 0.75,
                    Key(Note.As, Octave.FOUR) to 0.75,
                    Key(Note.A, Octave.FOUR) to 0.75
            ))
        }
    }

    @DisplayName("Test Violin A3")
    @Test
    fun testViolinA3() {
        // Takes 1 minute
        readFromWaveFile("violin_a3").getFrequenciesDistinct().printEach()
    }

    @DisplayName("Test Loudness")
    @Test
    fun testLoudness() {
        // Remember we use decibels not amplitude to change and understand loudness
        generateSineWave(220.0, 0.5, 0.0, 2.0).play()
        generateSineWave(220.0, 0.1, 0.0, 2.0).play()
    }

    @DisplayName("Test Phase Calculation")
    @Test
    fun testPhaseCalculation() {
        val sampleRate = SAMPLE_RATE_POWER_OF_TWO

        val freq = 440

        val phase = 0.5

        val sineWave = sineWave(freq, 0.5, phase)

        val data = generateSineWave(sineWave, 1.0, sampleRate, 1)

        val sampleRateToFrequencyKNOWN = sampleRate.d / freq.d

        val frequencyToSampleRateKNOWN = freq.d / sampleRate

        val originalSize = data.size

        val newSize = previousPowerOfTwo(data.size)

        val result = mutableMapOf<Double, Double>() //freq to phase

        data.truncated()                // Truncate to allow FFT
                .fourierTransformed()   // FFT, makes values Complex with 0.0 for imaginary parts
                .rounded()              // Round everything to Int to avoid tiny numbers close to 0
                .removeZeros()              // Remove entries equal to (0.0, 0.0)
                .splitInHalf()          // Get first half since data is identical in both
                .reducePartials()       // Remove unnecessary partials
                .forEach {
                    val index = it.key.d
                    val real = it.value.real
                    val imaginary = it.value.imaginary

                    val indexToSize = index.d / newSize.d

                    val sizeToSampleRate = newSize.d / sampleRate.d

                    val indexToSampleRate = index.d / sampleRate.d

                    val sizeToIndex = newSize.d / index.d

                    val indexToSizeTimesSampleRate = indexToSize * sampleRate.d


                    println(it)

                    println()

                    println("Index / Size :\t $indexToSize")
                    println("Size / SampleRate :\t $sizeToSampleRate")
                    println("Index / SampleRate :\t $indexToSampleRate")
                    println("Size / Index :\t $sizeToIndex")
                    println("SampleRate / Frequency :\t $sampleRateToFrequencyKNOWN")

                    println("Ratio Error:\t ${abs(sampleRateToFrequencyKNOWN - sizeToIndex)}")

                    println()

                    println("Calculated frequency :\t $indexToSizeTimesSampleRate")
                    println("Actual frequency :\t ${freq.d}")

                    println("Frequency Error:\t ${abs(freq.d - indexToSizeTimesSampleRate)}")

                    println()

                    val amplitude = abs(hypot(imaginary, real))
                    println("Amplitude: $amplitude")

                    println()

                    val atan2 = atan2(imaginary, real)
                    val phaseCalc = (atan2 + HALF_PI) / PI
                    println("Atan: $atan2")
                    println("Calculated Phase: $phaseCalc")
                    println("Actual Phase: $phase")

                    println("Phase Error:\t ${abs(phaseCalc - phase)}")

                    println()

                    val truncationAmount = originalSize.d / newSize.d
                    println("Truncation Amount: $truncationAmount")

                    // TODO: 28-Mar-19 Find a way to make the imaginary be 0.0!!!! and then do the same to the real

                    // TODO: 27-Mar-19 Phase is PERFECT when we use SAMPLE_RATE_POWER_OF_TWO, using anything else changes it
                    // the change is not because of seconds, but only because of sample rate, implying that the size
                    // of the array matters since only a power of two gets a preserved size, it'll probably end up
                    // being something to with ratios like the frequency thing again


                    result.put(indexToSizeTimesSampleRate, phaseCalc)

                    println()

                }

        val original = generateSineWave(sineWave, 1.0)

        result.map {
            sineWave(it.key, 0.5, it.value)
        }.addAllSineWavesEvenly(2.0).play()

    }

    @DisplayName("Test Perfect Phase Calculation")
    @Test
    fun testPerfectPhaseCalculation() {
        val sampleRate = SAMPLE_RATE

        val freq = 440

        val phase = 0.25

        val sineWave = sineWave(freq, 0.5, phase)

        val data = generateSineWave(sineWave, 1.0, sampleRate, 1)

        val sampleRateToFrequencyKNOWN = sampleRate.d / freq.d

        val frequencyToSampleRateKNOWN = freq.d / sampleRate

        val originalSize = data.size

        val newSize = previousPowerOfTwo(data.size)

        val result = mutableMapOf<Double, Double>() //freq to phase

        data.truncated()                // Truncate to allow FFT
                .fourierTransformed()   // FFT, makes values Complex with 0.0 for imaginary parts
                .rounded()              // Round everything to Int to avoid tiny numbers close to 0
                .removeZeros()              // Remove entries equal to (0.0, 0.0)
                .splitInHalf()          // Get first half since data is identical in both
                .reducePartials()       // Remove unnecessary partials
                .forEach {
                    val index = it.key.d
                    val real = it.value.real
                    val imaginary = it.value.imaginary

                    val indexToSize = index.d / newSize.d

                    val sizeToSampleRate = newSize.d / sampleRate.d

                    val indexToSampleRate = index.d / sampleRate.d

                    val sizeToIndex = newSize.d / index.d

                    val indexToSizeTimesSampleRate = indexToSize * sampleRate.d


                    println(it)

                    println()

                    println("Index / Size :\t $indexToSize")
                    println("Size / SampleRate :\t $sizeToSampleRate")
                    println("Index / SampleRate :\t $indexToSampleRate")
                    println("Size / Index :\t $sizeToIndex")
                    println("SampleRate / Frequency :\t $sampleRateToFrequencyKNOWN")

                    println("Ratio Error:\t ${abs(sampleRateToFrequencyKNOWN - sizeToIndex)}")

                    println()

                    println("Calculated frequency :\t $indexToSizeTimesSampleRate")
                    println("Actual frequency :\t ${freq.d}")

                    println("Frequency Error:\t ${abs(freq.d - indexToSizeTimesSampleRate)}")

                    println()

                    val amplitude = abs(hypot(imaginary, real))
                    println("Amplitude: $amplitude")

                    println()

                    val atan2 = atan2(imaginary, real)
                    val phaseCalc = (atan2 + HALF_PI) / PI
                    println("Atan: $atan2")
                    println("Calculated Phase: $phaseCalc")
                    println("Actual Phase: $phase")

                    println("Phase Error:\t ${abs(phaseCalc - phase)}")

                    println()

                    val truncationAmount = originalSize.d / newSize.d
                    println("Truncation Amount: $truncationAmount")

                    result.put(indexToSizeTimesSampleRate, phaseCalc)

                    println()

                }

        (0 until 1000).forEach { i ->
            val newPhase = i.d / 1000.0
            sineWave.phase = newPhase

            generateSineWave(sineWave, 1.0)
                    .fullExecution().forEach {
                        val real = it.value.real
                        val imaginary = it.value.imaginary

                        val atan2 = atan2(imaginary, real)
                        val phaseCalc = (atan2 + HALF_PI) / PI

                        if (phaseCalc == phase) {
                            println("Atan: $atan2")
                            println("Calculated Phase: $phaseCalc")
                            println("Actual Phase: $phase")
                            println("New Phase: $newPhase at $i")

                            // TODO: 04-Apr-19 63 seems to be the magic i index where everything works with frequency 440
                            // with many different sample rates
                            // 63 for 440
                            // 126 for 880


                            /*
                            * amplitude, magnitude = hypot function, use this to get the peaks
                            *
                            *
                            * frequency is the index(es) with the highest magnitude (hypot func)
                            *
                            *
                            * get the indexes with the highest real part (I believe we've already done this)
                            * then get the indexes with the highest imag part, remember to normalize this based
                            * on the originalSize, SampleRate and newSize as we did, so that when the size is different
                            * we still get the right frequencies, as long as it's a possible frequency in this sample
                            * rate, remember Nyquist theory
                            *
                            *
                            * FFT is basically, for each index,
                            * get me the amplitude (of a sine wave of that frequency) from the original input
                            *
                            * whenever phase changes, the real and imag change but the max amplitudes never change,
                            * hence why when we change the phase our current function still detects the frequency
                            * even tough it disregards imag right now
                            *
                            * phase is still the atan2 function
                            *
                            * */





                            println()
                        }
                    }
            println("Finished $i")
            println()
        }

        val original = generateSineWave(sineWave, 1.0)

        /*result.map {
            sineWave(it.key, 0.5, it.value)
        }.addAllSineWavesEvenly(2.0).play()*/

    }

    @DisplayName("Test Perfect Phase Calculation Test")
    @Test
    fun testPerfectPhaseCalculationTest() {
        val sampleRate = SAMPLE_RATE

        val freq = 440

        val phase = 0.5

        val amplitude = 1.0

        val sineWave = sineWave(freq, amplitude, phase)

        val data = generateIntSineWave(sineWave, 1.0, sampleRate, 1)

        val sampleRateToFrequencyKNOWN = sampleRate.d / freq.d

        val frequencyToSampleRateKNOWN = freq.d / sampleRate

        val originalSize = data.size

        val newSize = previousPowerOfTwo(data.size)

        val result = mutableMapOf<Frequency, Phase>() //freq to phase

        var maxReal = -1 to minDouble

        var minReal = -1 to maxDouble

        var maxImag = -1 to minDouble

        var minImag = -1 to maxDouble

        var maxAmp = -1 to minDouble

        var minAmp = -1 to maxDouble

        var minPhase = -1 to maxDouble

        var maxPhase = -1 to minDouble

        val maxPossibleAmplitudeOriginalSize = originalSize * MAX_AMPLITUDE

        val maxPossibleAmplitudeNewSize = newSize * MAX_AMPLITUDE

        var totalAmp = 0.0

        var maxAmpEntry = -1 to minComplex

        val phases = mutableMapOf<Index, Phase>()

        data.truncated()                // Truncate to allow FFT
                .fourierTransformed()   // FFT, makes values Complex with 0.0 for imaginary parts
                .rounded()              // Round everything to Int to avoid tiny numbers close to 0
                .mapIndexed()
                .splitInHalf()          // Get first half since data is identical in both
                .forEach {
                    val index = it.key.d
                    val real = it.value.real
                    val imaginary = it.value.imaginary

                    val indexToSize = index.d / newSize.d

                    val sizeToSampleRate = newSize.d / sampleRate.d

                    val indexToSampleRate = index.d / sampleRate.d

                    val sizeToIndex = newSize.d / index.d

                    val indexToSizeTimesSampleRate = indexToSize * sampleRate.d

                    val amplitudeCalc = abs(hypot(imaginary, real))

                    val atan2 = atan2(imaginary, real)

                    // val phaseCalc = (atan2 + HALF_PI) / PI
                    val phaseCalc = abs((atan2 + HALF_PI) * (1 / PI))

                    if (real >= maxReal.second) {
                        maxReal = index.i to real
                    }
                    if (real <= minReal.second) {
                        minReal = index.i to real
                    }

                    if (imaginary >= maxImag.second) {
                        maxImag = index.i to imaginary
                    }
                    if (imaginary <= minImag.second) {
                        minImag = index.i to imaginary
                    }

                    if (amplitudeCalc >= maxAmp.second) {
                        maxAmp = index.i to amplitudeCalc
                        maxAmpEntry = it.toPair()
                    }
                    if (amplitudeCalc <= minAmp.second) {
                        minAmp = index.i to amplitudeCalc
                    }

                    if (phaseCalc <= minPhase.second) {
                        minPhase = index.i to phaseCalc
                    }

                    if (phaseCalc >= maxPhase.second) {
                        maxPhase = index.i to phaseCalc
                    }

                    totalAmp += amplitudeCalc

                    println(it)

                    "Phase" label phaseCalc

                    phases[index.i] = phaseCalc

                }

        "Max Real" label maxReal
        "Min Real" label minReal
        "Max Imag" label maxImag
        "Min Imag" label minImag
        "Max Amp " label maxAmp
        "Min Amp " label minAmp

        "Max Phase " label maxPhase

        "Max Possible Amp Orig" label maxPossibleAmplitudeOriginalSize
        "Max Possible Amp New " label maxPossibleAmplitudeNewSize
        "Total Amp " label totalAmp

        printLine("Max Amp over")
        "\tMax Possible Amp Orig" label maxAmp.second / maxPossibleAmplitudeOriginalSize
        "\tMax Possible Amp New " label maxAmp.second / maxPossibleAmplitudeNewSize
        "\tTotal Amp " label maxAmp.second / totalAmp

        val phaseCalc = abs((atan2(maxAmpEntry.second.imaginary, maxAmpEntry.second.real) + HALF_PI) / PI)

        // TODO: 08-Apr-19 PhaseCalc always returns us either, 0.5, 0.25 or 0.75 with any sample rate
        // I think this has something to do with the special cases of the atan2 function

        "Phase at Max Amp" label phaseCalc

        // TODO: 08-Apr-19 Amp may be wrong because of truncation , maxAmp / Total Amp is perfect when POWER_OF_TWO

        // TODO: 05-Apr-19 Idea: Output this to a file and or plot it somewhere so that we can better understand the data
        // and use the output in the paper

        // TODO: 05-Apr-19 Is there data loss that our algorithm can't overcome?? Try all this with Longs
        // I think so man! Total Amp is not equal the amp of the only frequency here, and there are some frequencies
        // with tiny amplitudes, these are noise, YES THERE IS!

        /*
         * amplitude, magnitude = hypot function, use this to get the peaks
         *
         *
         * frequency is the index(es) with the highest magnitude (hypot func)
         *
         *
         * get the indexes with the highest real part (I believe we've already done this)
         * then get the indexes with the highest imag part, remember to normalize this based
         * on the originalSize, SampleRate and newSize as we did, so that when the size is different
         * we still get the right frequencies, as long as it's a possible frequency in this sample
         * rate, remember Nyquist theory
         *
         *
         * FFT is basically, for each index,
         * get me the amplitude (of a sine wave of that frequency) from the original input
         *
         * whenever phase changes, the real and imag change but the max amplitudes never change,
         * hence why when we change the phase our current function still detects the frequency
         * even tough it disregards imag right now
         *
         * phase is still the atan2 function
         *
         * */


        /*(0 until 1000).forEach { i ->
            val newPhase = i.d / 1000.0
            sineWave.phase = newPhase

            generateSineWave(sineWave, 1.0)
                    .fullExecution().forEach {
                        val real = it.value.real
                        val imaginary = it.value.imaginary

                        val atan2 = atan2(imaginary, real)
                        val phaseCalc = (atan2 + HALF_PI) / PI

                        if (phaseCalc == phase) {
                            println("Atan: $atan2")
                            println("Calculated Phase: $phaseCalc")
                            println("Actual Phase: $phase")
                            println("New Phase: $newPhase at $i")

                            // TODO: 04-Apr-19 63 seems to be the magic i index where everything works with frequency 440
                            // with many different sample rates
                            // 63 for 440
                            // 126 for 880


                            println()
                        }
                    }
            println("Finished $i")
            println()
        }*/

        val original = generateSineWave(sineWave, 1.0)

        /*result.map {
            sineWave(it.key, 0.5, it.value)
        }.addAllSineWavesEvenly(2.0).play()*/

    }
}

fun main() {

}