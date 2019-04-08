package uk.whitecrescent.instrumentdigitizer

import kotlin.math.log2
import kotlin.math.pow

enum class Note {
    C,
    Cs,
    D,
    Ds,
    E,
    F,
    Fs,
    G,
    Gs,
    A,
    As,
    B,
    X
}

enum class Octave {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NONE
}

data class Key(val note: Note, val octave: Octave) {

    val number: Int
        get() = getNumber(this)

    val frequency: Frequency
        get() {
            val exp = (number.d - 69.0) / 12.0
            return 2.0.pow(exp) * 440.0
        }

    infix fun centsDifferenceFrom(other: Key) = Key.centsDifference(this, other)

    infix fun addCents(cents: Int) = Key.addCents(this, cents)

    companion object {
        fun fromNumber(number: Int): Key {
            return getKey(number)
        }

        fun fromFrequency(frequency: Frequency): Key {
            val number = (12 * log2(frequency / 440.0)) + 69
            return fromNumber(number.i)
        }

        fun centsDifference(from: Key, to: Key): Int {
            return (1200 * log2(to.frequency / from.frequency)).i
        }

        fun addCents(from: Key, cents: Int): Frequency {
            return from.frequency * (2.0.pow(cents.d / 1200.d))
        }
    }
}