package uk.whitecrescent.instrumentdigitizer

import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer

class Synth : Liveable {

    lateinit var synthesizer: Synthesizer
    lateinit var channel: MidiChannel

    override fun create(): Synth {
        startSynthesizer()
        startMidi()
        return this
    }

    override fun destroy(): Synth {
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