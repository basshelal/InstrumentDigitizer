package uk.whitecrescent.instrumentdigitizer

import javafx.application.Application
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import tornadofx.add

class Graph : Application() {

    override fun start(primaryStage: Stage) {
        val stackPane = StackPane()
        val lineChart = LineChart<Number, Number>(NumberAxis(), NumberAxis())
        stackPane.add(lineChart)
        primaryStage.add(stackPane)

        primaryStage.width = 100.0
        primaryStage.height = 100.0
        primaryStage.show()
    }

    override fun init() {

    }

    override fun stop() {
        System.exit(0)
    }
}

fun main() {
    Application.launch(Graph::class.java)
}
