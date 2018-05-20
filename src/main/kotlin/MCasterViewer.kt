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

    staff.measures.forEachIndexed { idx, measure ->
        val numberOfTicks = measure.tickFraction.numerator

        padTypes
                .map { it to (measure.notes[it] ?: emptyList()) }
                .forEach { note ->
                    val extFactor = 3
                    if (staffView[note.first.abbreviation]!![idx].isEmpty()) {
                        staffView[note.first.abbreviation]!![idx] = "-".repeat(numberOfTicks * extFactor).toCharArray()
                    }
                    note.second.forEach {
                        val pos = it.numerator * numberOfTicks / it.denominator * extFactor
                        staffView[note.first.abbreviation]!![idx][pos] = note.first.note
                    }
                }
    }
    staffView.entries.forEach {
        print(it.key + "||")
        print(it.value.joinToString(separator = "|", transform = { it.joinToString(separator = "") }))
        println()
    }
}

