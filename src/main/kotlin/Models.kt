import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class SimpleFraction(
        val numerator: Int,
        val denominator: Int
)

typealias NoteAppearence = SimpleFraction

data class Measure(
        val timeSignature: SimpleFraction,
        val notes: Map<PadType, List<NoteAppearence>>
)

data class Staff(val measures: List<Measure>)


enum class PadType(@JsonValue val midiNote: Int, @JsonValue val title: String, val abbreviation: String, val note: Char) {
    SNARE(40, "snare", "Sn", '*'),
    KICK(35, "kick", "Ki", '@'),
    OPEN_HH(46, "opened-hh", "HH", 'O'),
    CLOSE_HH(42, "closed-hh", "HH", 'X');

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(title: String): PadType = PadType.values().first { it.title == title }

        fun byInt(note: Int): PadType = PadType.values().first { it.midiNote == note }
    }
}

private val n0d4 = NoteAppearence(0, 4)
private val n1d4 = NoteAppearence(1, 4)
private val n2d4 = NoteAppearence(2, 4)
private val n3d4 = NoteAppearence(3, 4)
private val n4d4 = SimpleFraction(4, 4)
private val n0d16 = NoteAppearence(0, 16)
private val n1d16 = NoteAppearence(1, 16)
private val n2d16 = NoteAppearence(2, 16)
private val n3d16 = NoteAppearence(3, 16)
private val n4d16 = NoteAppearence(4, 16)
private val n5d16 = NoteAppearence(5, 16)
private val n6d16 = NoteAppearence(6, 16)
private val n7d16 = NoteAppearence(7, 16)
private val n8d16 = NoteAppearence(8, 16)
private val n9d16 = NoteAppearence(9, 16)
private val n10d16 = NoteAppearence(10, 16)
private val n11d16 = NoteAppearence(11, 16)
val example1 = Staff(
        listOf(
                Measure(
                        n4d4,
                        mapOf(
                                PadType.CLOSE_HH to listOf(n0d4, n1d4, n2d4),
                                PadType.OPEN_HH to listOf(n3d4),
                                PadType.KICK to listOf(n0d4),
                                PadType.SNARE to listOf(n2d4)
                        )
                ),
                Measure(
                        n4d4,
                        mapOf(
                                PadType.CLOSE_HH to listOf(n0d4, n1d4, n2d4),
                                PadType.OPEN_HH to listOf(n3d4),
                                PadType.KICK to listOf(n0d4),
                                PadType.SNARE to listOf(n2d4)
                        )
                ),
                Measure(
                        n4d4,
                        mapOf(
                                PadType.SNARE to listOf(n0d16, n1d16, n2d16, n3d16, n4d16, n5d16, n6d16, n7d16, n8d16, n9d16, n10d16, n11d16)
                        )
                )
        )
)
