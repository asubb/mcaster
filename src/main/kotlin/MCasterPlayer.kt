import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {
    val mdOut = MidiSystem.getMidiDeviceInfo()
            .map {
                println("""
                    name: ${it.name}
                    vendor: ${it.vendor}
                    version: ${it.version}
                    description: ${it.description}

                    """.trimIndent())
                val dev = MidiSystem.getMidiDevice(it)!!
                if (!dev.isOpen) {
                    dev.open()
                }
                dev
            }
            .first { it.deviceInfo.name.contains("Gervill") }

    val playPlan = createPlayPlan(example1)
    println("Play plan: $playPlan")

    MidiSoundProducer(mdOut).use { producer ->
        playPlan.forEach {
            when (it) {
                is Long -> {
                    println(" $it");Thread.sleep(it)
                }
                is PadType -> {
                    print(it.note)
                    producer.play(it, if (it == PadType.METRONOME_TICK) 127 else 0)
                }

                else -> throw IllegalStateException("Not recognized $it")
            }
        }
        println("\ntrack played")

        Thread.sleep(1000)
        println("finished waiting for last note to play")
    }

    System.exit(0)
}
