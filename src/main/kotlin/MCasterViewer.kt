fun main(args: Array<String>) {
//    val defaultTerminalFactory = DefaultTerminalFactory()
//    val term = defaultTerminalFactory.createTerminalEmulator()
//    term.putCharacter('h')
//    term.putCharacter('e')
//    term.flush()
//    val mapper = jacksonObjectMapper()
//    val staff = mapper.readValue<Staff>(File("l1.json"))

    val staffView = HashMap<String, MutableList<CharArray>>()
    val staff = example1

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

    val extFactor = 2
    staff.measures.forEachIndexed { idx, measure ->
        println("Measure: ${measure.tickFraction.numerator}/${measure.tickFraction.denominator}")
        padTypes.forEach {
            if (staffView[it.abbreviation]!![idx].isEmpty()) {
                staffView[it.abbreviation]!![idx] = "-".repeat(measure.tickFraction.numerator * extFactor).toCharArray()
            }
        }
        (0..(measure.tickFraction.numerator - 1)).forEach { pos ->
            val tickFraction = SimpleFraction(pos, measure.tickFraction.denominator)
            println(">> tick: $tickFraction")
            measure.notes.entries.asSequence()
                    .filter { it.value.any { it == tickFraction } }
                    .map {
                        println("\t${it.key}: ${it.value.first { it == tickFraction }}")
                        it.key
                    }
                    .forEach {
                        val i = tickFraction.numerator * extFactor
                        staffView[it.abbreviation]!![idx][i] = it.note
                    }

        }
        println(">>>\n")


//        padTypes
//                .map { it to (measure.notes[it] ?: emptyList()) }
//                .forEach { note ->
//                    val extFactor = 2
//                    if (staffView[note.first.abbreviation]!![idx].isEmpty()) {
//                        staffView[note.first.abbreviation]!![idx] = "-".repeat(numberOfTicks.numerator * extFactor).toCharArray()
//                    }
//                    note.second.forEach {
//                        val pos = (it.numerator * numberOfTicks.numerator) / (it.denominator * numberOfTicks.denominator) * extFactor
//                        staffView[note.first.abbreviation]!![idx][pos] = note.first.note
//                    }
//                }
    }
    staffView.entries.forEach {
        print(it.key + "||")
        print(it.value.joinToString(separator = "|", transform = { it.joinToString(separator = "") }))
        println()
    }
}

