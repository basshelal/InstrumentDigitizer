package uk.whitecrescent.instrumentdigitizer

/**
 * Represents an instrument made purely out of multiple sine waves
 */
data class Instrument(val name: String, val overtoneRatios: List<OvertoneRatio>)

/*
 * frequencyRatio gives us what to multiply by the fundamental frequency to get this frequency
 * amplitude is absolute Amplitude
 * phase is absolute phase
 */
data class OvertoneRatio(val frequencyRatio: Double, val amplitude: Amplitude, val phase: Phase = 0.0)


/**
 * Use this to create multiple oscillating sine waves using [com.jsyn.unitgen.SineOscillator]
 * which require a frequency and amplitude for the sine wave, we may also have phase on the wave
 */
data class SineWave(var frequency: Frequency, var amplitude: Amplitude, var phase: Phase)

fun sineWave(frequency: Number, amplitude: Amplitude = 1.0, phase: Phase = 0.0) =
        SineWave(frequency.d, amplitude, phase)