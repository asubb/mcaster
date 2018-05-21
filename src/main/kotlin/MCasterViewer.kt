fun main(args: Array<String>) {
//    val defaultTerminalFactory = DefaultTerminalFactory()
//    val term = defaultTerminalFactory.createTerminalEmulator()
//    term.putCharacter('h')
//    term.putCharacter('e')
//    term.flush()
//    val mapper = jacksonObjectMapper()
//    val staff = mapper.readValue<Staff>(File("l1.json"))

    val staff = example1

    val maxMeasure = staff.measures
            .maxBy { it.tickFraction.numerator }!!

    val extFactor = 2
    val staffView = createTextStaffView(staff, extFactor, maxMeasure.tickFraction.numerator)

    staffView.entries.forEach {
        print(it.key + "||")
        print(it.value.joinToString(separator = "|", transform = { it.joinToString(separator = "") }))
        println()
    }

    print(" ".repeat(4))

    val interval = tickInterval(maxMeasure) / extFactor
    staff.measures.forEach { measure ->
        (0..(maxMeasure.tickFraction.numerator * extFactor - 1)).forEach {
            print("^")
            Thread.sleep(interval)
        }
        print("+")
    }

}

