fun iterateOver(staff: Staff, eventHandler: (Int, PadType, NoteAppearence, SimpleFraction) -> Unit) {
    staff.measures.forEachIndexed { idx, measure ->
        println("Measure: ${measure.tickFraction.numerator}/${measure.tickFraction.denominator}")
        (0..(measure.tickFraction.numerator - 1)).forEach { pos ->
            val tickFraction = SimpleFraction(pos, measure.tickFraction.denominator)
            println(">> tick: $tickFraction")
            measure.notes.entries.asSequence()
                    .filter { it.value.any { it == tickFraction } }
                    .map {
                        val v = it.value.first { it == tickFraction }
                        println("\t${it.key}: $v")
                        it.key to v
                    }
                    .forEach {
                        eventHandler.invoke(idx, it.first, it.second, tickFraction)
                    }

        }
        println(">>>\n")
    }
}
