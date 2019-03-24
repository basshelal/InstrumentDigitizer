package tests

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SineOscillator
import com.jsyn.unitgen.UnitGenerator
import com.jsyn.unitgen.UnitVoice
import com.softsynth.shared.time.TimeStamp
import com.synthbot.jasiohost.AsioChannel
import com.synthbot.jasiohost.AsioDriver
import com.synthbot.jasiohost.AsioDriverListener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.instrumentdigitizer.DESIRED_DIFFERENCE
import uk.whitecrescent.instrumentdigitizer.Functions
import uk.whitecrescent.instrumentdigitizer.ReaderWriter
import uk.whitecrescent.instrumentdigitizer.SAMPLE_RATE
import uk.whitecrescent.instrumentdigitizer.fullExecution
import uk.whitecrescent.instrumentdigitizer.generateSineWave
import uk.whitecrescent.instrumentdigitizer.generateTwoSineWaves
import uk.whitecrescent.instrumentdigitizer.getSineOscillators
import uk.whitecrescent.instrumentdigitizer.maxImaginary
import uk.whitecrescent.instrumentdigitizer.maxReal
import uk.whitecrescent.instrumentdigitizer.minImaginary
import uk.whitecrescent.instrumentdigitizer.minReal
import uk.whitecrescent.instrumentdigitizer.padded
import uk.whitecrescent.instrumentdigitizer.play
import uk.whitecrescent.instrumentdigitizer.reducePartials
import uk.whitecrescent.instrumentdigitizer.sineWave
import uk.whitecrescent.instrumentdigitizer.splitInHalf
import uk.whitecrescent.instrumentdigitizer.toComplexArray
import uk.whitecrescent.instrumentdigitizer.toIntMap
import uk.whitecrescent.instrumentdigitizer.ttrr
import uk.whitecrescent.instrumentdigitizer.writeSineWaveAudio
import uk.whitecrescent.instrumentdigitizer.writeToWaveFile
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.absoluteValue

@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Test JSyn Synth")
    @Test
    fun testJSynSynth() {
        val synth: Synthesizer
        val ugen: UnitGenerator
        val voice: UnitVoice
        val lineOut: LineOut


        // Create a context for the synthesizer.
        synth = JSyn.createSynthesizer()
        // Set output latency to 123 msec because this is not an interactive app.
        synth.audioDeviceManager.setSuggestedOutputLatency(0.123)

        // Add a tone generator.
        ugen = SineOscillator()
        synth.add(ugen)
        voice = ugen
        // Add an output mixer.
        lineOut = LineOut()
        synth.add(lineOut)

        // Connect the oscillator to the left and right audio output.
        voice.output.connect(0, lineOut.input, 0)
        voice.output.connect(0, lineOut.input, 1)

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start()

        // Get synthesizer time in seconds.
        val timeNow = synth.currentTime

        // Advance to a near future time so we have a clean start.
        var timeStamp = TimeStamp(timeNow + 0.5)

        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        synth.startUnit(lineOut, timeStamp)

        // Schedule a note on and off.
        var freq = 200.0 // hertz
        val duration = 1.4
        val onTime = 1.0
        voice.noteOn(freq, 0.5, timeStamp)
        voice.noteOff(timeStamp.makeRelative(onTime))

        // Schedule this to happen a bit later.
        timeStamp = timeStamp.makeRelative(duration)
        freq *= 1.5 // up a perfect fifth
        voice.noteOn(freq, 0.5, timeStamp)
        voice.noteOff(timeStamp.makeRelative(onTime))

        timeStamp = timeStamp.makeRelative(duration)
        freq *= 4.0 / 5.0 // down a major third
        voice.noteOn(freq, 0.5, timeStamp)
        voice.noteOff(timeStamp.makeRelative(onTime))

        // Sleep while the song is being generated in the background thread.
        try {
            println("Sleep while synthesizing.")
            synth.sleepUntil(timeStamp.time + 2.0)
            println("Woke up...")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Stop everything.
        synth.stop()

    }

    @DisplayName("Test JSyn Synth 2")
    @Test
    fun testJSynSynth2() {
        val synth = JSyn.createSynthesizer()
        val lineOut = LineOut()
        synth.add(lineOut)

        val oscillators = getSineOscillators(10)
        oscillators.forEach { synth.add(it) }
        oscillators.forEach {
            it.output.connect(0, lineOut.input, 0)
            it.output.connect(0, lineOut.input, 1)
        }
        lineOut.start()
        synth.start()

        oscillators.onEach { it.noteOn(220.0, 0.1) }

        Thread.sleep(2000)

        oscillators.onEach {
            it.noteOff()
            it.noteOn(440.0, 0.1)
        }

        Thread.sleep(2000)

        oscillators.onEach {
            it.noteOff()
            it.noteOn(880.0, 0.1)
        }

        Thread.sleep(2000)

        synth.stop()
    }

    @DisplayName("Test ASIO")
    @Test
    fun testASIO() {
        val driver = AsioDriver.getDriver(AsioDriver.getDriverNames().first())
        val channel0 = driver.getChannelOutput(0)
        val channel1 = driver.getChannelOutput(1)

        val listener = object : AsioDriverListener {
            override fun resetRequest() {
                println("Reset Request")
            }

            override fun latenciesChanged(inputLatency: Int, outputLatency: Int) {
                println("Latencies Changes")
            }

            override fun resyncRequest() {
                println("Resync Request")
            }

            override fun bufferSwitch(sampleTime: Long, samplePosition: Long, activeChannels: MutableSet<AsioChannel>?) {
                println("Buffer Switched")

                var i = 0
                var sampleIndex = 0
                val sampleRate = driver.sampleRate
                val output = FloatArray(driver.bufferPreferredSize)
                while (i < driver.bufferPreferredSize) {
                    output[i] = Math.sin((2.0 * Math.PI * sampleIndex * 440.0) / sampleRate).toFloat()
                    i++
                    sampleIndex++
                }

                activeChannels?.forEach {
                    it.write(output)
                }
            }

            override fun sampleRateDidChange(sampleRate: Double) {
                println("Sample Rate Did Change")
            }

            override fun bufferSizeChanged(bufferSize: Int) {
                println("Buffer Size Changed")
            }

        }

        driver.addAsioDriverListener(listener)
        driver.createBuffers(setOf(channel0, channel1))
        driver.start()


        Thread.sleep(5000)


        driver.stop()
        driver.disposeBuffers()
        driver.exit()
        driver.shutdownAndUnloadDriver()
    }

    @DisplayName("Test Write Data")
    @Test
    fun testWriteData() {
        writeSineWaveAudio()
    }

    @DisplayName("Test Generate Sine Wave")
    @Test
    fun testGenerateSineWave() {
        val wave = generateSineWave(220.0, 1.0, 0.0, 1, 1000, 1)
        wave.forEach { println(it) }

        assertEquals(1000, wave.size)
    }

    @DisplayName("Test Play Sine Wave")
    @Test
    fun testPlaySineWave() {
        val buffer = generateSineWave(440.0, 1.0, 0.5, 2, SAMPLE_RATE, 1)
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
        val buffer = generateTwoSineWaves(440, 220, 0.0, 0.5, 2, SAMPLE_RATE, 1)
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

    @DisplayName("Test Old Fourier")
    @Test
    fun testOldFourier() {
        val reader = ReaderWriter()
        val buffer = reader.read()
        val complexArray = Functions.fourierTransform(buffer.padded())
        complexArray.forEach { print(it) }
        reader.close()
    }

    @DisplayName("Test Fourier Forward and Inverse")
    @Test
    fun testFourierForwardAndInverse() {
        val buffer = generateSineWave(220.0, 1.0, 0.0, 10, SAMPLE_RATE, 2)

        val original = buffer.padded()
        val originalComplex = original.toComplexArray()
        val transformed = Functions.fourierTransform(original)
        val inversed = Functions.inverseFourierTransform(transformed)

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
        val buffer = generateSineWave(440.0, 1.0, 0.0, 1, 1024, 1)

        val original = buffer.padded()
        val originalComplex = original.toComplexArray()
        val transformed = Functions.fourierTransform(original)
        val inversed = Functions.inverseFourierTransform(transformed)

        /*writeTextToFile(buffer, outPath = RESOURCES_DIR + "SineWave.out")
        writeTextToFile(transformed, outPath = RESOURCES_DIR + "Transformed.out")*/

        println(transformed.map { it.real }.max())
        println(transformed.map { it.imaginary }.max())

    }

    @DisplayName("Test Write To Wave File")
    @Test
    fun testWriteToWaveFile() {
        val sineWave = sineWave(440, 1.0, 0.5)
        val buffer = generateSineWave(sineWave, 10, SAMPLE_RATE, 1)
        writeToWaveFile(buffer, "MyFile")
        buffer.play()
    }

    @DisplayName("Test Full Execution")
    @Test
    fun testFullExecution() {
        val sineWave = generateSineWave(440.0, 1.0, 0.5, 1, 1024, 1)

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

        generateTwoSineWaves(440, 220, 0.5, 0.5, 1, 1024, 1)
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
        val buffer = generateSineWave(sineWave, 1, 65536)
        // ^ doesnt work properly when sample rate is not power of 2 and when seconds is not 1

        buffer.fullExecution().forEach { println(it) }

        sineWave.play()
    }

}