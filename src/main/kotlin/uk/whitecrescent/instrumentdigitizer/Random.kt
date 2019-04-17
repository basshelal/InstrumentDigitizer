package uk.whitecrescent.instrumentdigitizer

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class Graph : Application() {

    override fun start(primaryStage: Stage) {

        val xAxis = NumberAxis("Years", 1960.0, 2020.0, 10.0)

        val yAxis = NumberAxis("No.of schools", 0.0, 350.0, 50.0)

        val lineChart = LineChart(xAxis, yAxis)

        val series: XYChart.Series<Number, Number> = XYChart.Series()
        series.name = "No of schools in an year"

        series.data.add(XYChart.Data(1970, 15))
        series.data.add(XYChart.Data(1980, 30))
        series.data.add(XYChart.Data(1990, 60))
        series.data.add(XYChart.Data(2000, 120))
        series.data.add(XYChart.Data(2013, 240))
        series.data.add(XYChart.Data(2014, 300))


        lineChart.data.add(series)


        primaryStage.scene = Scene(StackPane(lineChart), 600.0, 300.0)

        primaryStage.width = 400.0
        primaryStage.height = 400.0
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
