package com.example.laba2

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.chart.*
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.media.AudioClip
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class ViewController {

    private var enteredNumbers = mutableListOf<Float>()
//    private var rangeNumbers = mutableListOf<Float>()

    private val buzzer = AudioClip(javaClass.getResource("/com/example/laba2/warning.mp3")!!.toExternalForm())

    private val MAX_DIFF_PERCENT = 40.0
    private val NUMBERS_COUNT = 10


    @FXML
    private lateinit var barChart: BarChart<String, Float>

    @FXML
    private lateinit var clearSelectButton: Button

    @FXML
    private lateinit var enterNumberField: TextField

    @FXML
    private lateinit var enteredNumbersLabel: Label

    @FXML
    private lateinit var lineChart: LineChart<String, Float>

    @FXML
    private lateinit var newNumbersButton: Button

    @FXML
    private lateinit var selectButton: Button

    @FXML
    private lateinit var selectDivisionField: TextField

    @FXML
    private lateinit var selectFromField: TextField

    @FXML
    private lateinit var selectToField: TextField



    @FXML
    fun initialize() {
//        newNumbersButton.fire()
//        for (i in 1..10) {
//            enterNumberField.text = (i * 10 + 50).toString()
//            onNumberEntered()
//        }




    }

    fun onNewNumbers() {
        enterNumberField.isDisable = false

        enteredNumbers = mutableListOf()
        updateNumbersList()

        selectFromField.clear()
        selectToField.clear()
        selectDivisionField.clear()

        selectFromField.isDisable = true
        selectToField.isDisable = true
        selectDivisionField.isDisable = true
        selectButton.isDisable = true
        clearSelectButton.isDisable = true


        lineChart.data.clear()
        barChart.data.clear()

        // TODO очистить графики


    }

    fun onNumberEntered() {
        val newNumber: Float
        try {
            newNumber = enterNumberField.text.toFloat()
            if (enteredNumbers.size > 0) {
                val percent = abs(max(newNumber, enteredNumbers.last()) * 100.0 / min(newNumber, enteredNumbers.last()) - 100.0)
                if (percent > MAX_DIFF_PERCENT) {
                    showWarning("Число $newNumber отличается от предыдущего (${enteredNumbers.last()}) более чем на $MAX_DIFF_PERCENT процентов (на ${"%.1f".format(percent)}%).")
                    enterNumberField.clear()
                    return
                }
            }

            enteredNumbers.add(newNumber)
            enterNumberField.clear()
            updateNumbersList()

            if (enteredNumbers.size == NUMBERS_COUNT) {
                enterNumberField.isDisable = true

                selectFromField.isDisable = false
                selectToField.isDisable = false
                selectDivisionField.isDisable = false
                selectButton.isDisable = false
                clearSelectButton.isDisable = false


                setLineChartData(enteredNumbers)

            }



        } catch (e: NumberFormatException) {
            showWarning("\"${enterNumberField.text}\" не является числом.")
            enterNumberField.clear()
        }

    }


    fun onSelectRange() {
        if (selectFromField.text.isEmpty() || selectToField.text.isEmpty() || selectDivisionField.text.isEmpty()) {
            showWarning("Заполните все поля для выбора диапазона.")
        } else {

            try {
                val from: Float = selectFromField.text.toFloat()
                val to: Float = selectToField.text.toFloat()
                val division: Float = selectDivisionField.text.toFloat()

                if (to < from) {
                    showWarning("Нижняя граница должна быть меньше верхней.")
                    return
                }

                if (division <= 0) {
                    showWarning("Кратность чисел должна быть положительной.")
                    return
                }

                val rangeNumbers = mutableListOf<Float>()
                for (number in enteredNumbers) {
                    if (number in from..to && number % division == 0F) {
                        rangeNumbers += number
                    }
                }

                setLineChartData(rangeNumbers)
                setBarChartData(rangeNumbers)

            } catch (e: NumberFormatException) {
                showWarning("Вы ввели некорректные данные для выбора диапазона.")
            }
        }
    }

    fun clearRangeSelection() {
        selectFromField.clear()
        selectToField.clear()
        selectDivisionField.clear()

        setLineChartData(enteredNumbers)
        barChart.data.clear()
    }

    private fun setLineChartData(list: MutableList<Float>) {
        lineChart.data.clear()

//        val average = list.sum() / list.size

        val series = Series<String, Float>()
//        val averageSeries = Series<String, Float>()


        for (i in 1..list.size) {
            series.data.add(XYChart.Data(i.toString(), list[i - 1]))
//            averageSeries.data.add(XYChart.Data(i.toString(), average))
        }


        lineChart.data.add(series)
//        lineChart.data.add(averageSeries)
    }

    private fun setBarChartData(list: MutableList<Float>) {

        barChart.data.clear()
        val series = Series<String, Float>()
        for (i in 1..list.size) {
            series.data.add(XYChart.Data(i.toString(), list[i - 1]))
        }
        barChart.data.add(series)
    }


    private fun updateNumbersList() {
        enteredNumbersLabel.text = "Вы ввели числа: $enteredNumbers"
    }

    private fun showWarning(text: String) {
        buzzer.play()

        val dialog = Dialog<String>()
        dialog.title = "Ошибка!"
        dialog.contentText = text
        dialog.dialogPane.buttonTypes.add(ButtonType("Ок", ButtonBar.ButtonData.OK_DONE))
        dialog.showAndWait()

    }


}
