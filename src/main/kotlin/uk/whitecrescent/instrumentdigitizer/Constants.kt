package uk.whitecrescent.instrumentdigitizer

import javax.sound.sampled.AudioFormat

const val RESOURCES_DIR = "src/main/resources/"

const val A3_VIOLIN_FILE_PATH = RESOURCES_DIR + "violin_a3.wav"

const val OUTPUT_PATH_WAV = RESOURCES_DIR + "out.wav"
const val OUTPUT_PATH_TEXT = RESOURCES_DIR + "out.txt"
const val OUTPUT_PATH_CSV = RESOURCES_DIR + "out.csv"
const val OUTPUT_PATH_OUT = RESOURCES_DIR + "out.out"

const val TRANSFORMED_OUTPUT_PATH_OUT = RESOURCES_DIR + "transformed.out"

const val SAMPLE_RATE = 44100

const val MAX_AMPLITUDE = 127.0

const val MINIMUM_DIFFERENCE = 1E-3
const val DESIRED_DIFFERENCE = 1E-7

val WAVE_DEFAULT_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)

val SAMPLE_INSTRUMENT = Instrument(listOf(
        OvertoneRatio(1.0, 0.60),
        OvertoneRatio(2.0, 0.10),
        OvertoneRatio(3.0, 0.05),
        OvertoneRatio(4.0, 0.05),
        OvertoneRatio(5.0, 0.05),
        OvertoneRatio(6.0, 0.05),
        OvertoneRatio(7.0, 0.025),
        OvertoneRatio(8.0, 0.025)
))

val BASIC_INSTRUMENT = Instrument(listOf(OvertoneRatio(1.0, 1.0)))