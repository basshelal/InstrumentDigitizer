package tests

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SineOscillator
import com.jsyn.unitgen.UnitGenerator
import com.jsyn.unitgen.UnitVoice
import com.softsynth.shared.time.TimeStamp
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.instrumentdigitizer.getSineOscillators


@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Test")
    @Test
    fun test() {
        var synth: Synthesizer
        var ugen: UnitGenerator
        var voice: UnitVoice
        var lineOut: LineOut


        // Create a context for the synthesizer.
        synth = JSyn.createSynthesizer()
        // Set output latency to 123 msec because this is not an interactive app.
        synth.getAudioDeviceManager().setSuggestedOutputLatency(0.123)

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
        val timeNow = synth.getCurrentTime()

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

    @DisplayName("Test")
    @Test
    fun test1() {
        val synth = JSyn.createSynthesizer()
        val lineOut = LineOut()
        synth.add(lineOut)

        val oscillators = getSineOscillators(20)
        oscillators.forEach { synth.add(it) }
        oscillators.forEach {
            it.output.connect(0, lineOut.input, 0)
            it.output.connect(0, lineOut.input, 1)
        }
        synth.start()

        synth.startUnit(lineOut)

        oscillators.onEach { it.noteOn(220.0, 0.1) }

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        synth.stop()
    }

}