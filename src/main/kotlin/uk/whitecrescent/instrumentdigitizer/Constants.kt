package uk.whitecrescent.instrumentdigitizer

import javax.sound.sampled.AudioFormat
import kotlin.math.PI

const val HALF_PI = PI / 2.0

const val RESOURCES_DIR = "src/main/resources/"

const val A3_VIOLIN_FILE_PATH = RESOURCES_DIR + "violin_a3.wav"

const val OUTPUT_PATH_WAV = RESOURCES_DIR + "out.wav"
const val OUTPUT_PATH_TEXT = RESOURCES_DIR + "out.txt"
const val OUTPUT_PATH_CSV = RESOURCES_DIR + "out.csv"
const val OUTPUT_PATH_OUT = RESOURCES_DIR + "out.out"

const val TRANSFORMED_OUTPUT_PATH_OUT = RESOURCES_DIR + "transformed.out"

const val SAMPLE_RATE = 44100
const val SAMPLE_RATE_POWER_OF_TWO = 65536

val MAX_AMPLITUDE = Byte.MAX_VALUE.d

const val MINIMUM_DIFFERENCE = 1E-3
const val DESIRED_DIFFERENCE = 1E-7

val WAVE_DEFAULT_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)

val minDouble = Double.MIN_VALUE
val maxDouble = Double.MAX_VALUE

val SAMPLE_INSTRUMENT = Instrument(listOf(
        OvertoneRatio(1.0, 0.40, 0.5),
        OvertoneRatio(2.0, 0.10, 0.1),
        OvertoneRatio(3.0, 0.05, 0.2),
        OvertoneRatio(4.0, 0.05, 0.3),
        OvertoneRatio(5.0, 0.05, 0.4),
        OvertoneRatio(6.0, 0.05, 0.6),
        OvertoneRatio(7.0, 0.025, 0.8),
        OvertoneRatio(8.0, 0.025, 0.9)
))

val BASIC_INSTRUMENT = Instrument(listOf(OvertoneRatio(1.0, 1.0, 0.5)))