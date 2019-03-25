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
    B
}

enum class Octave {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
}

data class Key(val note: Note, val octave: Octave) {

    val number: Int
        get() = getNumber(this)

    val frequency: Double
        get() {
            val exp = (number.toDouble() - 49.0) / 12.0
            return 2.0.pow(exp) * 440.0
        }


    companion object {
        fun fromNumber(number: Int): Key {
            return getKey(number)
        }

        fun fromFrequency(frequency: Double): Key {
            val number = (12 * log2(frequency / 440.0)) + 49
            return fromNumber(number.toInt())
        }
    }
}