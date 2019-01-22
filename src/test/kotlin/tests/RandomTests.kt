package tests

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.instrumentdigitizer.Reader
import uk.whitecrescent.instrumentdigitizer.print
import javax.sound.midi.MidiSystem

@DisplayName("Random Tests")
class RandomTests {

    @DisplayName("Test")
    @Test
    fun test() {
        Reader().read()
        MidiSystem.getSynthesizer().defaultSoundbank.apply {
            //name.print
            //description.print
            //vendor.print
            //version.print
            //resources.asList().print
            //instruments.asList().print

            resources.asList().last().dataClass.print
            instruments.asList().first().patch.print
        }
    }
}