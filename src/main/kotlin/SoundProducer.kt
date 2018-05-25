import java.io.Closeable
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer

interface SoundProducer : Closeable {
    fun play(pad: PadType, volume: Int)
}

class MidiSoundProducer(private val midiDevice: MidiDevice) : SoundProducer {
    init {
        midiDevice.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 9, 1, 1), 0)
        Thread.sleep(1000)
        println("Midi device is ready")
    }

    override fun play(pad: PadType, volume: Int) {
        midiDevice.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 9, pad.midiNote, volume), 0)
        midiDevice.receiver.send(ShortMessage(ShortMessage.NOTE_OFF, 9, pad.midiNote, 0), 200)
    }

    override fun close() {
        MidiSystem.getMidiDeviceInfo()
                .map { MidiSystem.getMidiDevice(it) }
                .filter { it.isOpen }
                .forEach { device ->
                    device.receivers.forEach { it.close() }
                    if (device is Synthesizer) {
                        device.channels.forEach { it.allNotesOff() }
                    }
                    device.close()
                }
    }
}