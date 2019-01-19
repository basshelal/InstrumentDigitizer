import javafx.application.Application
import javafx.stage.Stage
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage


class App : Application() {


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, *args)
        }
    }

    override fun start(primaryStage: Stage) {
        //primaryStage.show()
    }

    override fun init() {
        println("Initializing...")
        handleMidi()
    }

    private fun handleMidi() {
        var device: MidiDevice
        val infos = MidiSystem.getMidiDeviceInfo()

        infos.forEach {
            device = MidiSystem.getMidiDevice(it)
            println(it)
            val transmitters = device.transmitters
            transmitters.forEach {
                it.receiver = MidiInputReceiver(device.deviceInfo.toString())
            }

            try {
                val trans = device.transmitter
                trans.receiver = MidiInputReceiver(device.deviceInfo.toString())

                //open each device
                device.open()
            } catch (e: MidiUnavailableException) {
                println(e)
            }

            //if code gets this far without throwing an exception
            //print a success message
            println(device.deviceInfo.toString() + " Was Opened")
        }
    }
}

inline class MidiInputReceiver(val name: String) : Receiver {
    override fun send(msg: MidiMessage, timeStamp: Long) {
        require(msg is ShortMessage)
        println(msg.data1)
    }

    override fun close() {}
}

