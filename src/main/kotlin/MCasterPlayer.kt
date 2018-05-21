import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer

fun main(args: Array<String>) {

    val infos = MidiSystem.getMidiDeviceInfo()
    for (info in infos) {
        println("Name:" + info.getName())
        println("\tDescription: " + info.description)
        println("\tVendor: " + info.getVendor())
    }

    val mdOut = infos
            .map {
                val dev = MidiSystem.getMidiDevice(it)!!
                if (!dev.isOpen) {
                    dev.open()
                }
                dev
            }
            .first { it.deviceInfo.name.contains("Gervill") }

    val staff = example1

    fun play(pad: PadType) {
        mdOut.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 9, pad.midiNote, 100), 0)
        mdOut.receiver.send(ShortMessage(ShortMessage.NOTE_OFF, 9, pad.midiNote, 100), 100)
    }

    val playPlan = createPlayPlan(staff)

    println("Play plan: $playPlan")
    playPlan.forEach {
        when (it) {
            is Long -> Thread.sleep(it)
            is PadType -> play(it)
            else -> throw IllegalStateException("Not recognized $it")
        }
    }

    println("track played")

    Thread.sleep(1000)
    println("finished waiting for last note to play")

    infos
            .map { MidiSystem.getMidiDevice(it) }
            .forEach { device ->
                device.receivers.forEach { it.close() }
                if (device is Synthesizer) {
                    device.channels.forEach { it.allNotesOff() }
                }
                device.close()
            }
    System.exit(0)
}
