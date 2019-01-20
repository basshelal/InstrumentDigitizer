import javafx.application.Application
import javafx.stage.Stage
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer


class App : Application() {


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, *args)
        }
    }

    lateinit var synthesizer: Synthesizer
    lateinit var channel: MidiChannel

    override fun start(primaryStage: Stage) {
        primaryStage.width = 100.toDouble()
        primaryStage.height = 100.toDouble()
        primaryStage.show()
    }

    override fun init() {
        println("Initializing...")
        startSynthesizer()
        startMidi()
    }

    override fun stop() {
        println("Stopping...")
        stopSynthesizer()
        stopMidi()
        System.exit(0)
    }

    private fun startSynthesizer() {
        synthesizer = MidiSystem.getSynthesizer()
        synthesizer.open()
        channel = synthesizer.channels.first()
        val instruments = synthesizer.defaultSoundbank.instruments
        synthesizer.loadInstrument(instruments.first())
        println("Latency: ${synthesizer.latency / 1000} milliseconds")
    }

    private fun stopSynthesizer() {
        synthesizer.close()
    }

    // TODO: 20-Jan-19 Latency is high when playing! These might be fixed by using ASIO
    private fun startMidi() {
        var device: MidiDevice
        val infos = MidiSystem.getMidiDeviceInfo()

        infos.forEach {
            device = MidiSystem.getMidiDevice(it)

            try {
                device.transmitter.receiver = object : Receiver {
                    override fun send(msg: MidiMessage, timeStamp: Long) {
                        require(msg is ShortMessage)
                        when (msg.command) {
                            ShortMessage.NOTE_ON -> {
                                channel.noteOn(msg.data1, msg.data2)
                            }
                            ShortMessage.NOTE_OFF -> {
                                channel.noteOff(msg.data1)
                            }
                            ShortMessage.PITCH_BEND -> {
                                println("Pitch Bend")
                            }
                        }
                        println("TimeStamp: $timeStamp")
                        println("Channel: ${msg.channel}")
                        println("Command: ${msg.command}")
                        println("Data1: ${msg.data1}") // Note value
                        println("Data2: ${msg.data2}") // Velocity
                    }

                    override fun close() {}
                }

                device.open()
            } catch (e: MidiUnavailableException) {
            }

            println(device.deviceInfo.toString() + " was opened")
        }
    }

    private fun stopMidi() {
        var device: MidiDevice
        val infos = MidiSystem.getMidiDeviceInfo()

        infos.forEach {
            device = MidiSystem.getMidiDevice(it)
            if (device.isOpen) device.close()
            println(device.deviceInfo.toString() + " is closed")
        }
    }

    @Suppress("EXPERIMENTAL_FEATURE_WARNING")
    inner class MidiInputReceiver(val name: String) : Receiver {
        override fun send(msg: MidiMessage, timeStamp: Long) {
            require(msg is ShortMessage)
            println("Channel: ${msg.channel}")
            println("Command: ${msg.command}")
            println("Data1: ${msg.data1}") // Note value
            println("Data2: ${msg.data2}") // Velocity
            if (msg.command == ShortMessage.NOTE_ON) {
                synthesizer.channels[0].noteOn(msg.data1, msg.data2)
            }
            if (msg.command == ShortMessage.NOTE_OFF) {
                synthesizer.channels[0].noteOff(msg.data1)
            }
        }

        override fun close() {}
    }
}
