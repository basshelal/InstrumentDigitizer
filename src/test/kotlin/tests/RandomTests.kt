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
import uk.whitecrescent.instrumentdigitizer.generateSineWave
import uk.whitecrescent.instrumentdigitizer.getSineOscillators
import uk.whitecrescent.instrumentdigitizer.padded
import uk.whitecrescent.instrumentdigitizer.toComplex
import uk.whitecrescent.instrumentdigitizer.toIntMap
import uk.whitecrescent.instrumentdigitizer.writeSineWaveAudio
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
        val wave = generateSineWave(220, 1, 1000, 1)
        wave.forEach { println(it) }

        assertEquals(1000, wave.size)
    }

    @DisplayName("Test Play Sine Wave")
    @Test
    fun testPlaySineWave() {
        val buffer = generateSineWave(440, 2, SAMPLE_RATE, 1)
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
        val buffer = generateSineWave(220, 10, SAMPLE_RATE, 2)

        val original = buffer.padded()
        val originalComplex = original.toComplex()
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
        // TODO: 17-Mar-19 Make sense of the outputs!!!
    }

    @DisplayName("Test Write Sine to CSV")
    @Test
    fun testWriteSineToCSV() {
        val buffer = generateSineWave(440, 1, 1024, 1)

        val original = buffer.padded()
        val originalComplex = original.toComplex()
        val transformed = Functions.fourierTransform(original)
        val inversed = Functions.inverseFourierTransform(transformed)

        /*writeTextToFile(buffer, outPath = RESOURCES_DIR + "SineWave.out")
        writeTextToFile(transformed, outPath = RESOURCES_DIR + "Transformed.out")*/

        println(transformed.map { it.real }.max())
        println(transformed.map { it.imaginary }.max())

    }

}