package uk.whitecrescent.instrumentdigitizer

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

    private lateinit var synthesizer: Synthesizer

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
        println("Latency: ${synthesizer.latency / 1000} milliseconds")
    }

    private fun stopSynthesizer() {
        synthesizer.close()
    }

    private fun startMidi() {
        MidiSystem.getMidiDeviceInfo().forEach {
            ignoreException<MidiUnavailableException> {
                MidiSystem.getMidiDevice(it).apply {
                    transmitter.receiver = InstrumentReceiver(SAMPLE_INSTRUMENT)
                    open()
                    println("$deviceInfo was opened")
                    println("$SAMPLE_INSTRUMENT was loaded")
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

class InstrumentReceiver(val instrument: Instrument = BASIC_INSTRUMENT) : Receiver {

    val format = EASY_FORMAT
    val line = AudioSystem.getSourceDataLine(format).apply {
        open(format)
        start()
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        require(message is ShortMessage)
        when (message.command) {
            ShortMessage.NOTE_ON -> {
                val key = Key.fromNumber(message.data1)
                thread {
                    val buffer = instrument.getByteArray(key.frequency, 1.0, 1.0)
                    line.write(buffer, 0, buffer.size)
                }
                println("Note on")
                println("TimeStamp: $timeStamp")
                println("Channel: ${message.channel}")
                println("Command: ${message.command}")
                println("Data1 (Note): ${message.data1}") // Note value
                println("Data2 (Vel) : ${message.data2}") // Velocity
                println("$key  ${key.frequency}")
                println()
            }
            ShortMessage.NOTE_OFF -> {
                thread {
                    line.flush()
                }
                println("Note off")
                println()
            }
            ShortMessage.PITCH_BEND -> {
                println("Pitch Bend")
                println("Command: ${message.command}")
                println("Data1: ${message.data1}") // Note value
                println("Data2: ${message.data2}") // Velocity
                println()
            }
        }
        println("TimeStamp: $timeStamp")
        println("Channel: ${message.channel}")
        println("Command: ${message.command}")
        println("Data1: ${message.data1}") // Note value
        println("Data2: ${message.data2}") // Velocity
        println()
    }

    override fun close() {
        println("Closing Receiver")
    }

}

class ChannelReceiver(private val channel: MidiChannel) : Receiver {

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
        primaryStage.title = "Instrument Digitizer App"
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
