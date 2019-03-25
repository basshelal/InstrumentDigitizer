package uk.whitecrescent.instrumentdigitizer

import uk.whitecrescent.instrumentdigitizer.Note.A
import uk.whitecrescent.instrumentdigitizer.Note.As
import uk.whitecrescent.instrumentdigitizer.Note.B
import uk.whitecrescent.instrumentdigitizer.Note.C
import uk.whitecrescent.instrumentdigitizer.Note.Cs
import uk.whitecrescent.instrumentdigitizer.Note.D
import uk.whitecrescent.instrumentdigitizer.Note.Ds
import uk.whitecrescent.instrumentdigitizer.Note.E
import uk.whitecrescent.instrumentdigitizer.Note.F
import uk.whitecrescent.instrumentdigitizer.Note.Fs
import uk.whitecrescent.instrumentdigitizer.Note.G
import uk.whitecrescent.instrumentdigitizer.Note.Gs
import uk.whitecrescent.instrumentdigitizer.Octave.EIGHT
import uk.whitecrescent.instrumentdigitizer.Octave.FIVE
import uk.whitecrescent.instrumentdigitizer.Octave.FOUR
import uk.whitecrescent.instrumentdigitizer.Octave.ONE
import uk.whitecrescent.instrumentdigitizer.Octave.SEVEN
import uk.whitecrescent.instrumentdigitizer.Octave.SIX
import uk.whitecrescent.instrumentdigitizer.Octave.THREE
import uk.whitecrescent.instrumentdigitizer.Octave.TWO
import uk.whitecrescent.instrumentdigitizer.Octave.ZERO

val MIDI_TO_KEY_MAP = mapOf(
        // Octave ZERO
        89 to Key(C, ZERO),
        90 to Key(Cs, ZERO),
        91 to Key(D, ZERO),
        92 to Key(Ds, ZERO),
        93 to Key(E, ZERO),
        94 to Key(F, ZERO),
        95 to Key(Fs, ZERO),
        96 to Key(G, ZERO),
        97 to Key(Gs, ZERO),
        1 to Key(A, ZERO),
        2 to Key(As, ZERO),
        3 to Key(B, ZERO),
        // Octave ONE
        4 to Key(C, ONE),
        5 to Key(Cs, ONE),
        6 to Key(D, ONE),
        7 to Key(Ds, ONE),
        8 to Key(E, ONE),
        9 to Key(F, ONE),
        10 to Key(Fs, ONE),
        11 to Key(G, ONE),
        12 to Key(Gs, ONE),
        13 to Key(A, ONE),
        14 to Key(As, ONE),
        15 to Key(B, ONE),
        // Octave TWO
        16 to Key(C, TWO),
        17 to Key(Cs, TWO),
        18 to Key(D, TWO),
        19 to Key(Ds, TWO),
        20 to Key(E, TWO),
        21 to Key(F, TWO),
        22 to Key(Fs, TWO),
        23 to Key(G, TWO),
        24 to Key(Gs, TWO),
        25 to Key(A, TWO),
        26 to Key(As, TWO),
        27 to Key(B, TWO),
        // Octave THREE
        28 to Key(C, THREE),
        29 to Key(Cs, THREE),
        30 to Key(D, THREE),
        31 to Key(Ds, THREE),
        32 to Key(E, THREE),
        33 to Key(F, THREE),
        34 to Key(Fs, THREE),
        35 to Key(G, THREE),
        36 to Key(Gs, THREE),
        37 to Key(A, THREE),
        38 to Key(As, THREE),
        39 to Key(B, THREE),
        // Octave FOUR
        40 to Key(C, FOUR),
        41 to Key(Cs, FOUR),
        42 to Key(D, FOUR),
        43 to Key(Ds, FOUR),
        44 to Key(E, FOUR),
        45 to Key(F, FOUR),
        46 to Key(Fs, FOUR),
        47 to Key(G, FOUR),
        48 to Key(Gs, FOUR),
        49 to Key(A, FOUR),
        50 to Key(As, FOUR),
        51 to Key(B, FOUR),
        // Octave FIVE
        52 to Key(C, FIVE),
        53 to Key(Cs, FIVE),
        54 to Key(D, FIVE),
        55 to Key(Ds, FIVE),
        56 to Key(E, FIVE),
        57 to Key(F, FIVE),
        58 to Key(Fs, FIVE),
        59 to Key(G, FIVE),
        60 to Key(Gs, FIVE),
        61 to Key(A, FIVE),
        62 to Key(As, FIVE),
        63 to Key(B, FIVE),
        // Octave SIX
        64 to Key(C, SIX),
        65 to Key(Cs, SIX),
        66 to Key(D, SIX),
        67 to Key(Ds, SIX),
        68 to Key(E, SIX),
        69 to Key(F, SIX),
        70 to Key(Fs, SIX),
        71 to Key(G, SIX),
        72 to Key(Gs, SIX),
        73 to Key(A, SIX),
        74 to Key(As, SIX),
        75 to Key(B, SIX),
        // Octave SEVEN
        76 to Key(C, SEVEN),
        77 to Key(Cs, SEVEN),
        78 to Key(D, SEVEN),
        79 to Key(Ds, SEVEN),
        80 to Key(E, SEVEN),
        81 to Key(F, SEVEN),
        82 to Key(Fs, SEVEN),
        83 to Key(G, SEVEN),
        84 to Key(Gs, SEVEN),
        85 to Key(A, SEVEN),
        86 to Key(As, SEVEN),
        87 to Key(B, SEVEN),
        // Octave EIGHT
        88 to Key(C, EIGHT),
        98 to Key(Cs, EIGHT),
        99 to Key(D, EIGHT),
        100 to Key(Ds, EIGHT),
        101 to Key(E, EIGHT),
        102 to Key(F, EIGHT),
        103 to Key(Fs, EIGHT),
        104 to Key(G, EIGHT),
        105 to Key(Gs, EIGHT),
        106 to Key(A, EIGHT),
        107 to Key(As, EIGHT),
        108 to Key(B, EIGHT)
)