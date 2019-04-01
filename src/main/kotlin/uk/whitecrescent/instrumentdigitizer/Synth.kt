package uk.whitecrescent.instrumentdigitizer

import com.jsyn.JSyn
import com.jsyn.devices.javasound.MidiDeviceTools
import com.jsyn.instruments.DualOscillatorSynthVoice
import com.jsyn.midi.MidiSynthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.util.MultiChannelSynthesizer
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiDevice
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
        val instruments = synthesizer.defaultSoundbank.instruments
        synthesizer.loadInstrument(instruments.first())
        println("Latency: ${synthesizer.latency / 1000} milliseconds")
    }

    private fun stopSynthesizer() {
        synthesizer.close()
    }

    // TODO: 20-Jan-19 Latency is high when playing! These might be fixed by using ASIO
    // To use and understand ASIO a little better go see the ExampleHost class better
    // basically we're going to be dealing with very low level stuff
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
            println("Play MIDI keyboard: " + keyboard.deviceInfo.description)

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

fun main() {
    UseMidiKeyboard()
}