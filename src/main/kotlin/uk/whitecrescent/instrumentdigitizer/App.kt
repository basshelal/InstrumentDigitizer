package uk.whitecrescent.instrumentdigitizer

import com.jsyn.JSyn
import com.jsyn.devices.javasound.MidiDeviceTools
import com.jsyn.instruments.DualOscillatorSynthVoice
import com.jsyn.midi.MidiSynthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.util.MultiChannelSynthesizer
import javafx.application.Application
import javafx.stage.Stage
import sun.audio.AudioDevice
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.thread

class Synth {

    lateinit var synthesizer: Synthesizer
    lateinit var channel: MidiChannel

    fun create(): Synth {
        startSynthesizer()
        startMidi()
        return this
    }

    fun destroy(): Synth {
        stopSynthesizer()
        stopMidi()
        return this
    }

    private fun startSynthesizer() {
        synthesizer = MidiSystem.getSynthesizer()
        synthesizer.open()
        channel = synthesizer.channels.first()
        synthesizer.loadInstrument(synthesizer.defaultSoundbank.instruments.first())
        println("Latency: ${synthesizer.latency / 1000} milliseconds")
    }

    private fun stopSynthesizer() {
        synthesizer.close()
    }

    private fun startMidi() {
        MidiSystem.getMidiDeviceInfo().forEach {
            ignoreException<MidiUnavailableException> {
                MidiSystem.getMidiDevice(it).apply {
                    transmitter.receiver = CustomReceiver(channel)
                    open()
                    println("$deviceInfo was opened")
                }
            }
        }
    }

    private fun stopMidi() {
        MidiSystem.getMidiDeviceInfo().forEach {
            MidiSystem.getMidiDevice(it).apply {
                if (isOpen) AudioDevice.device.close()
                println("${AudioDevice.device} is closed")
            }
        }
    }
}

class UseMidiKeyboard {

    init {
        val synth = JSyn.createSynthesizer()

        val voiceDescription = DualOscillatorSynthVoice.getVoiceDescription()

        val multiSynth = MultiChannelSynthesizer()
        multiSynth.setup(synth, 0, 16, 16, voiceDescription)
        val midiSynthesizer = MidiSynthesizer(multiSynth)

        // Create a LineOut for the entire synthesizer.
        val lineOut = LineOut()
        synth!!.add(lineOut)
        multiSynth.output.connect(0, lineOut.input, 0)
        multiSynth.output.connect(1, lineOut.input, 1)

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start()
        lineOut.start()

        val format = EASY_FORMAT
        val line = AudioSystem.getSourceDataLine(format)

        line.apply {
            open(format)
            start()
        }

        val keyboard = MidiDeviceTools.findKeyboard()
        // Just use default synthesizer.
        if (keyboard != null) {
            // If you forget to open them you will hear no sound.
            keyboard.open()
            // Put the receiver in the transmitter.
            // This gives fairly low latency playing.
            println("Play MIDI keyboard: ${keyboard.deviceInfo.description}")

            keyboard.transmitter.receiver = object : Receiver {
                override fun close() {
                    print("Closed.")
                }

                override fun send(message: MidiMessage, timeStamp: Long) {
                    require(message is ShortMessage)
                    when (message.command) {
                        ShortMessage.NOTE_ON -> {
                            val key = Key.fromNumber(message.data1)
                            val buffer = BASIC_INSTRUMENT.getByteArray(key.frequency, 1.0, 1.0)
                            thread {
                                line.write(buffer, 0, buffer.size)
                            }

                            println("TimeStamp: $timeStamp")
                            println("Channel: ${message.channel}")
                            println("Command: ${message.command}")
                            println("Data1 (Note): ${message.data1}") // Note value
                            println("Data2 (Vel) : ${message.data2}") // Velocity
                            println("$key  ${key.frequency}")
                        }
                        ShortMessage.NOTE_OFF -> {
                            line.flush()
                        }
                        ShortMessage.PITCH_BEND -> {
                            println("Pitch Bend")
                            println("Command: ${message.command}")
                            println("Data1: ${message.data1}") // Note value
                            println("Data2: ${message.data2}") // Velocity
                        }
                    }

                }
            }
        } else {
            println("Could not find a keyboard.")
        }
    }
}

class CustomReceiver(val channel: MidiChannel) : Receiver {

    override fun send(message: MidiMessage, timeStamp: Long) {
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                println("Note on")
                channel.noteOn(message.data1, message.data2)
            }
            ShortMessage.NOTE_OFF -> {
                println("Note off")
                channel.noteOff(message.data1)
            }
            ShortMessage.PITCH_BEND -> {
                println("Pitch bend")
                // from 0 to 16383, 8192 is middle
                channel.pitchBend = 8192
            }
        }
        println("TimeStamp: $timeStamp")
        println("Channel: ${message.channel}")
        println("Command: ${message.command}")
        println("Data1: ${message.data1}") // Note value
        println("Data2: ${message.data2}") // Velocity
    }

    override fun close() {
        println("Closing Receiver")
    }

}

class App : Application() {

    lateinit var synth: Synth

    override fun start(primaryStage: Stage) {
        primaryStage.width = 100.0
        primaryStage.height = 100.0
        primaryStage.show()
    }

    override fun init() {
        println("Initializing...")
        synth = Synth().create()
    }

    override fun stop() {
        println("Stopping...")
        synth.destroy()
        System.exit(0)
    }
}

fun main() {
    Application.launch(App::class.java)
}
