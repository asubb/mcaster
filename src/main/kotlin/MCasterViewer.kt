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

    // init staff with lines
    staff.measures.forEachIndexed { idx, measure ->
        padTypes.forEach {
            if (staffView[it.abbreviation]!![idx].isEmpty()) {
                staffView[it.abbreviation]!![idx] = "-".repeat(measure.tickFraction.numerator * extFactor).toCharArray()
            }
        }
    }

    // fill in with notes
    iterateOver(staff) { idx, padType, noteAppearance, tickFraction ->
        val i = tickFraction.numerator * extFactor
        staffView[padType.abbreviation]!![idx][i] = padType.note
    }

    staffView.entries.forEach {
        print(it.key + "||")
        print(it.value.joinToString(separator = "|", transform = { it.joinToString(separator = "") }))
        println()
    }
}

