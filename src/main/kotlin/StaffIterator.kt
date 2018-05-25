private const val debug = false

fun iterateOver(staff: Staff, eventHandler: (Int, PadType, NoteAppearence, SimpleFraction) -> Unit) {
    staff.measures.forEachIndexed { idx, measure ->
        if (debug) println("Measure: ${measure.tickFraction.numerator}/${measure.tickFraction.denominator}")
        (0..(measure.tickFraction.numerator - 1)).forEach { pos ->
            val tickFraction = SimpleFraction(pos, measure.tickFraction.denominator)
            if (debug) println(">> tick: $tickFraction")
            measure.notes.entries.asSequence()
                    .filter { it.value.any { it == tickFraction } }
                    .map {
                        val v = it.value.first { it == tickFraction }
                        if (debug) println("\t${it.key}: $v")
                        it.key to v
                    }
                    .forEach {
                        eventHandler.invoke(idx, it.first, it.second, tickFraction)
                    }

        }
        if (debug) println(">>>\n")
    }
}

fun createPlayPlan(staff: Staff): List<Any> {
    val playPlan = ArrayList<Any>()
    var prevTick = SimpleFraction(0, 1)
    var prevMeasureIdx = 0
    iterateOver(staff) { idx, padType, noteAppearance, tickFraction ->
        val measure = staff.measures[idx]
        val timeToWait = tickInterval(measure)
        val absoluteTickVal = SimpleFraction(
                idx * tickFraction.denominator + tickFraction.numerator,
                tickFraction.denominator
        )
        if (prevTick != absoluteTickVal) {
            if (prevMeasureIdx != idx) {
                // there was an end of the measure, need to wait till the end
                playPlan.add(tickInterval(staff.measures[prevMeasureIdx]))
                prevMeasureIdx = idx
            } else {
                playPlan.add(timeToWait)
            }
            prevTick = absoluteTickVal
        }
        playPlan.add(padType)

    }
    return playPlan
}

fun tickInterval(measure: Measure): Long {
    val beatsIntervalMs = 1 / (measure.bpm / 60.0 / 1000.0)
    return (beatsIntervalMs * 4 / measure.tickFraction.denominator).toLong()
}

fun createTextStaffView(staff: Staff, extFactor: Int, measureTicks: Int? = null): Map<String, List<CharArray>> {
    val staffView = HashMap<String, MutableList<CharArray>>()
    // init note lines
    val padTypes = staff.measures
            .flatMap { it.notes.keys }
            .map { it }
            .distinct()
    padTypes
            .map { it.abbreviation }
            .forEach {
                staffView[it] = ArrayList((0..staff.measures.size).map { CharArray(0) })
            }

    // init staff with lines
    staff.measures.forEachIndexed { idx, measure ->
        padTypes.forEach {
            if (staffView[it.abbreviation]!![idx].isEmpty()) {
                staffView[it.abbreviation]!![idx] = "-".repeat((measureTicks
                        ?: measure.tickFraction.numerator) * extFactor).toCharArray()
            }
        }
    }

    // fill in with notes
    iterateOver(staff) { idx, padType, noteAppearance, tickFraction ->
        val i =
                tickFraction.numerator *
                        (measureTicks ?: staff.measures[idx].tickFraction.numerator) /
                        tickFraction.denominator *
                        extFactor
        if (padType.note != null) {
            staffView[padType.abbreviation]!![idx][i] = padType.note
        }
    }
    return staffView
}