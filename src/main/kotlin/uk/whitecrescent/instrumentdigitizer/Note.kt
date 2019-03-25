package uk.whitecrescent.instrumentdigitizer

import kotlin.math.log2
import kotlin.math.pow

enum class Note {
    A,
    As,
    B,
    C,
    Cs,
    D,
    Ds,
    E,
    F,
    Fs,
    G,
    Gs,
}

enum class Octave {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
}

data class Key(val note: Note, val octave: Octave) {

    // TODO: 25-Mar-19 Much of this is not correct and untested!

    val number: Int
        get() = (octave.ordinal * 9) + (note.ordinal + 1)

    val frequency: Double
        get() {
            val exp = (number.toDouble() - 49.0) / 12.0
            return 2.0.pow(exp) * 440.0
        }


    companion object {
        fun fromNumber(number: Int): Key {
            val octave = ((number.toDouble() / 9.0) - 1).toInt()
            val key = (number.rem(12.0) - 1).toInt()
            return Key(Note.values()[key], Octave.values()[octave])
        }

        fun fromFrequency(frequency: Double): Key {
            val number = (12 * log2(frequency / 440.0)) + 49
            return fromNumber(number.toInt())
        }
    }
}