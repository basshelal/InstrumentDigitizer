package tests

import com.jsyn.JSyn
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
}