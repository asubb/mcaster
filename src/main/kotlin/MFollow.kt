import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.util.*
import javax.sound.midi.*

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

    val mapper = jacksonObjectMapper()
    val track = mapper.readValue<Track>(File("l1.json"))


    val trackPlan = track.sequence
            .groupBy { it.offset }
            .toSortedMap(compareBy({ it }))
            .iterator()
    var tick = if (trackPlan.hasNext()) trackPlan.next() else null
    val factor = 2f
    var progress = 0L
    do {
        tick?.value
//                ?.filter { it.pad != PadType.OPEN_HH }
                ?.forEach {
                    //                    println(it)
                    mdOut.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 9, it.pad.midiNote, 100), 0)
                    mdOut.receiver.send(ShortMessage(ShortMessage.NOTE_OFF, 9, it.pad.midiNote, 100),
                            (it.duration.toFloat() / factor).toLong())
                }
        tick = if (trackPlan.hasNext()) trackPlan.next() else null
        val offset = tick?.key?.toFloat()?.div(factor)?.toLong()
        val nextEventIn = offset?.minus(progress) ?: 0
        Thread.sleep(nextEventIn)
        progress += nextEventIn
    } while (tick != null)
    println("track played")

    Thread.sleep(1000)
    println("finished waiting for last note to play")

    infos
            .map { MidiSystem.getMidiDevice(it) }
//            .filter { it.isOpen }
            .forEach { device ->
                device.receivers.forEach { it.close() }
                if (device is Synthesizer) {
                    device.channels.forEach { it.allNotesOff() }
                }
                device.close()
            }

//        val midiDevice = MidiSystem.getMidiDevice(info)
//        try {
//            if (!midiDevice.isOpen) {
//                println("Opening device")
//                try {
//                    midiDevice.open()
//                } catch (e: Exception) {
//                    e.printStackTrace(System.out)
//                }
//
//            }
//            if (
//                    info.name.contains("LoopBe")
//                    && midiDevice.maxReceivers < 0 && mdOut == null) {
//                mdOut = midiDevice
//                println("Got out device: $mdOut")
//            }
//            if (info.name.contains("Fast Track") && midiDevice.maxTransmitters < 0) {
//                mdIn = midiDevice
//                println("Got in device: $mdIn")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace(System.out)
//        }
//
//        println("--DONE-\n")
//    }

}

data class MEvent(
        val pad: PadType,
        val duration: Int,
        val offset: Int
)

data class Track(val sequence: List<MEvent>)


enum class PadType(@JsonValue val midiNote: Int, @JsonValue val title: String) {
    SNARE(40, "snare"),
    KICK(35, "kick"),
    OPEN_HH(46, "opened-hh"),
    CLOSE_HH(42, "closed-hh");

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(title: String): PadType = PadType.values().first { it.title == title }

        fun byInt(note: Int): PadType = PadType.values().first { it.midiNote == note }
    }
}

