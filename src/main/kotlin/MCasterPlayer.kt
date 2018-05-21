import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer
import kotlin.collections.ArrayList

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


    val playPlan = ArrayList<Any>()
    var prevTick = SimpleFraction(0, 1)
    iterateOver(staff) { idx, padType, noteAppearance, tickFraction ->
        val measure = staff.measures[idx]
        val timeToWait = tickInterval(measure)
        val abs = SimpleFraction(idx * tickFraction.denominator + tickFraction.numerator, tickFraction.denominator)
        if (prevTick != abs) {
            playPlan.add(timeToWait)
            prevTick = abs
        }
        playPlan.add(padType)

    }

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

private fun tickInterval(measure: Measure): Long {
    val beatsIntervalMs = 1 / (measure.bpm / 60.0 / 1000.0)
    return (beatsIntervalMs * 4 / measure.tickFraction.denominator).toLong()
}

