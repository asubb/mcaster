import kotlin.math.max
import kotlin.math.min

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
        val numberOfTicks = max(
                measure.timeSignature.denominator,
                lcm(
                        measure.notes.values
                                .flatMap { it }
                                .map { it.denominator.toLong() }
                                .toSet()
                                .toLongArray()
                ).toInt()
        )

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

private fun gcd(a: Long, b: Long): Long {
    var a = a
    var b = b
    while (b > 0) {
        val temp = b
        b = a % b // % is remainder
        a = temp
    }
    return a
}

private fun gcd(input: LongArray): Long {
    var result = input[0]
    for (i in 1 until input.size) result = gcd(result, input[i])
    return result
}

private fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
}

private fun lcm(input: LongArray): Long {
    var result = input[0]
    for (i in 1 until input.size) result = lcm(result, input[i])
    return result
}