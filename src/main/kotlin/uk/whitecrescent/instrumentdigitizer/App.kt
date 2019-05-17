@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.instrumentdigitizer

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import sun.audio.AudioDevice
import tornadofx.add
import tornadofx.button
import java.io.File
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
    var instrument = BASIC_INSTRUMENT
    val instrumentReceiver = InstrumentReceiver(instrument)

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
                    transmitter.receiver = instrumentReceiver
                    open()
                    println("$deviceInfo was opened")
                    println("$BASIC_INSTRUMENT was loaded")
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

    fun changeInstrument(ins: Instrument) {
        instrument = ins
        MidiSystem.getMidiDeviceInfo().forEach {
            ignoreException<MidiUnavailableException> {
                MidiSystem.getMidiDevice(it).apply {
                    instrumentReceiver.instrument = instrument
                    open()
                    println("$deviceInfo was opened")
                    println("$instrument was loaded")
                }
            }
        }
    }
}

class InstrumentReceiver(var instrument: Instrument = SAMPLE_INSTRUMENT) : Receiver {

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

class App : Application() {

    lateinit var synth: Synth
    lateinit var sample: File
    lateinit var instrument: Instrument

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Instrument Digitizer App"
        primaryStage.width = 100.0
        primaryStage.height = 100.0
        primaryStage.scene = Scene(StackPane().apply {
            add(
                    button("Upload Sample") {
                        FileChooser().apply {
                            title = "Upload Sample"
                            initialDirectory = File(RESOURCES_DIR)
                            setOnAction {
                                sample = showOpenDialog(primaryStage) ?: File(A3_VIOLIN_FILE_PATH)
                                instrument = execute(sample)
                                synth.changeInstrument(SAMPLE_INSTRUMENT)
                            }
                        }
                    })
        })
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


    private inline fun execute(sample: File): Instrument {
        println(sample.path)
        val buffer = readFromWaveFileRawPath(sample.path)
        val fourierOutput = execute(buffer, SAMPLE_RATE)
        val overtones = overtoneRatios(fourierOutput)
        return Instrument(sample.path, overtones)
    }

}

fun main() {
    Application.launch(App::class.java)
}
