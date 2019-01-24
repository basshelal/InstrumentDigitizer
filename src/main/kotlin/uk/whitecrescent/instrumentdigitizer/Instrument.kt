package uk.whitecrescent.instrumentdigitizer

import com.jsyn.unitgen.SineOscillator

class Instrument(val harmonics: Int) {
    val waves: List<Wave> = emptyList()
}


/**
 * Use this to create multiple oscillating waves, specifically sine waves using [com.jsyn.unitgen.SineOscillator]
 * which require a frequency and amplitude for the sine wave.
 */
data class Wave(val frequency: Double, val amplitude: Double)

fun getSineOscillators(amount: Int): List<SineOscillator> {
    return Array(amount, { SineOscillator(0.0, 0.0) }).asList()
}

fun getSineOscillators(wavesList: List<Wave>): List<SineOscillator> {
    return wavesList.map { SineOscillator(it.frequency, it.amplitude) }
}