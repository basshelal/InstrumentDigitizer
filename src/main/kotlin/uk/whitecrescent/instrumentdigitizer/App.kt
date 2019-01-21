package uk.whitecrescent.instrumentdigitizer

import javafx.application.Application
import javafx.stage.Stage


class App : Application() {

    lateinit var synth: Synth

    override fun start(primaryStage: Stage) {
        primaryStage.width = 100.0
        primaryStage.height = 100.0
        primaryStage.show()
    }

    override fun init() {
        println("Initializing...")
        synth = Synth().create()
    }

    override fun stop() {
        println("Stopping...")
        synth.destroy()
        System.exit(0)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, *args)
        }
    }
}
