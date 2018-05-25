import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlin.math.max

data class SimpleFraction(
        val numerator: Int,
        val denominator: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is SimpleFraction
                && (other.numerator * this.denominator) - (other.denominator * this.numerator) == 0
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException("can't use this as a key")
    }
}

typealias NoteAppearence = SimpleFraction

data class Measure private constructor(
        val timeSignature: SimpleFraction,
        val bpm: Int,
        val notes: Map<PadType, List<NoteAppearence>>
) {

    companion object {
        fun create(
                timeSignature: SimpleFraction,
                bpm: Int,
                notes: Map<PadType, List<NoteAppearence>>
        ): Measure {
            val notesWithMetronome = HashMap(notes)
            notesWithMetronome[PadType.METRONOME_TICK] =
                    (0..timeSignature.numerator)
                            .map { NoteAppearence(it, timeSignature.denominator) }
            return Measure(timeSignature, bpm, notesWithMetronome)
        }
    }

    val tickFraction: SimpleFraction

    init {
        val mDen = max(
                timeSignature.denominator,
                lcm(
                        notes.values
                                .flatMap { it }
                                .map { it.denominator.toLong() }
                                .toSet()
                                .toLongArray()
                ).toInt()
        )

        tickFraction = SimpleFraction(
                timeSignature.numerator * mDen / timeSignature.denominator,
                mDen
        )
    }

}

data class Staff(
        val measures: List<Measure>
)

enum class PadType(@JsonValue val midiNote: Int, @JsonValue val title: String, val abbreviation: String, val note: Char?) {
    SNARE(40, "snare", "Sn", '*'),
    KICK(35, "kick", "Ki", '@'),
    OPEN_HH(46, "opened-hh", "HH", 'O'),
    CLOSE_HH(42, "closed-hh", "HH", 'X'),
    METRONOME_TICK(44, "", "Me", '`')
    ;

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
private val n12d16 = NoteAppearence(12, 16)
private val n13d16 = NoteAppearence(13, 16)
private val n14d16 = NoteAppearence(14, 16)
private val n15d16 = NoteAppearence(15, 16)
private val n0d12 = NoteAppearence(0, 12)
private val n1d12 = NoteAppearence(1, 12)
private val n2d12 = NoteAppearence(2, 12)
private val n3d12 = NoteAppearence(3, 12)
private val n4d12 = NoteAppearence(4, 12)
private val n5d12 = NoteAppearence(5, 12)
private val n6d12 = NoteAppearence(6, 12)
private val n7d12 = NoteAppearence(7, 12)
private val n8d12 = NoteAppearence(8, 12)
private val n9d12 = NoteAppearence(9, 12)
private val n10d12 = NoteAppearence(10, 12)
private val n11d12 = NoteAppearence(11, 12)

private val example1Bpm = 60
private val example1timeSignature = SimpleFraction(4, 4)
val example1 = Staff(
        listOf(
                Measure.create(
                        example1timeSignature,
                        example1Bpm,
                        mapOf(
                                PadType.CLOSE_HH to listOf(n0d4, n1d4, n2d4),
                                PadType.OPEN_HH to listOf(n3d4),
                                PadType.KICK to listOf(n0d4),
                                PadType.SNARE to listOf(n2d4)
                        )
                ),
                Measure.create(
                        example1timeSignature,
                        example1Bpm,
                        mapOf(
                                PadType.CLOSE_HH to listOf(n0d4, n1d4, n2d4),
                                PadType.OPEN_HH to listOf(n3d4),
                                PadType.KICK to listOf(n0d4),
                                PadType.SNARE to listOf(n2d4)
                        )
                ),
                Measure.create(
                        example1timeSignature,
                        example1Bpm,
                        mapOf(
                                PadType.SNARE to listOf(
                                        n0d16, n1d16, n2d16, n3d16,
                                        n4d16, n5d16, n6d16, n7d16,
                                        n8d16, n9d16, n10d16, n11d16,
                                        n12d16, n13d16, n14d16, n15d16
                                )
                        )
                ),
                Measure.create(
                        example1timeSignature,
                        example1Bpm,
                        mapOf(
                                PadType.SNARE to listOf(
                                        n0d12, n1d12, n2d12, n3d12,
                                        n4d12, n5d12, n6d12, n7d12,
                                        n8d12, n9d12, n10d12, n11d12
                                )
                        )
                )
        )
)
