package uk.whitecrescent.instrumentdigitizer

import com.jsyn.unitgen.SineOscillator

/**
 * Represents an instrument made purely out of multiple sine waves
 */
class Instrument(val overtones: Int) {
    val waves: List<SineWave> = emptyList()
}


/**
 * Use this to create multiple oscillating sine waves using [com.jsyn.unitgen.SineOscillator]
 * which require a frequency and amplitude for the sine wave, we may also have phase on the wave
 */
data class SineWave(val frequency: Double, val amplitude: Double, val phase: Double)

fun getSineOscillators(amount: Int): List<SineOscillator> {
    return Array(amount, { SineOscillator(0.0, 0.0) }).asList()
}

fun getSineOscillators(wavesList: List<SineWave>): List<SineOscillator> {
    return wavesList.map { SineOscillator(it.frequency, it.amplitude) }
}