package tests

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SawtoothOscillator
import com.jsyn.unitgen.UnitGenerator
import com.jsyn.unitgen.UnitVoice
import com.softsynth.shared.time.TimeStamp
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.instrumentdigitizer.print
import javax.sound.midi.MidiSystem


@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Test")
    @Test
    fun test() {
        MidiSystem.getSynthesizer().defaultSoundbank.apply {
            //name.print
            //description.print
            //vendor.print
            //version.print
            //resources.asList().print
            //instruments.asList().print
        }
        MidiSystem.getSynthesizer()::class.print
        JSyn.createSynthesizer()
    }

    @DisplayName("Test")
    @Test
    fun test1() {
        var synth: Synthesizer
        var ugen: UnitGenerator
        var voice: UnitVoice
        var lineOut: LineOut


        // Create a context for the synthesizer.
        synth = JSyn.createSynthesizer()
        // Set output latency to 123 msec because this is not an interactive app.
        synth.getAudioDeviceManager().setSuggestedOutputLatency(0.123)

        // Add a tone generator.
        ugen = SawtoothOscillator()
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

}