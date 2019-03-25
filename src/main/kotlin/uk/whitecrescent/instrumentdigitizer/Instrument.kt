package uk.whitecrescent.instrumentdigitizer

import com.jsyn.unitgen.SineOscillator

/**
 * Represents an instrument made purely out of multiple sine waves
 */
class Instrument(val overtoneRatios: List<OvertoneRatio>)

/*
 * frequencyRatio gives us what to multiply by the fundamental frequency to get this frequency
 * amplitudeRatio gives us what to multiply by the fundamental amplitude to get this amplitude
 * phase is just phase, no relations/ratios yet
 */
data class OvertoneRatio(val frequencyRatio: Double, val amplitudeRatio: Double, val phase: Double = 0.0)


/**
 * Use this to create multiple oscillating sine waves using [com.jsyn.unitgen.SineOscillator]
 * which require a frequency and amplitude for the sine wave, we may also have phase on the wave
 */
data class SineWave(var frequency: Double, var amplitude: Double, var phase: Double) {

    fun toSineOscillator() = SineOscillator().apply {
        frequency.set(this@SineWave.frequency)
        amplitude.set(this@SineWave.amplitude)
        phase.set(this@SineWave.phase)
    }
}

fun sineWave(frequency: Number, amplitude: Double = 1.0, phase: Double = 0.0) =
        SineWave(frequency.toDouble(), amplitude, phase)

fun getSineOscillators(amount: Int): List<SineOscillator> {
    return Array(amount) { SineOscillator(0.0, 0.0) }.asList()
}

fun getSineOscillators(wavesList: List<SineWave>): List<SineOscillator> {
    return wavesList.map { SineOscillator(it.frequency, it.amplitude) }
}