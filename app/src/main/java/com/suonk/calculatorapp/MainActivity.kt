package com.suonk.calculatorapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.text.set
import com.suonk.calculatorapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    //region =========================================== Numbers ============================================

    private lateinit var binding: ActivityMainBinding
    private var listOfNumbers = mutableListOf<String>()

    private var firstInt = 0.0
    private var secondInt = 0.0
    private var calculatorResult = 0.0

    private var isNotOperation = true
    private var calculationImmediately = false

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        initializeUI()
        numbersClick()
    }

    private fun initializeUI() {
        binding.calculationEditText.isEnabled = false

        eraseClick()
        clearAllClick()
        operationClick()
//        commaClick()
        equalClick()
    }

    //region ============================================ Clicks ============================================

    private fun eraseClick() {
        binding.apply {
            delete.setOnClickListener {
                if (calculationEditText.text?.isNotEmpty()!!) {
                    calculationEditText.text?.replace(
                        calculationEditText.text?.lastIndex!!,
                        calculationEditText.text?.lastIndex!!,
                        ""
                    )
                }
            }
        }
    }

    private fun clearAllClick() {
        binding.apply {
            deleteAll.setOnClickListener {
                calculationEditText.text?.clear()
                calculationTextView.text = ""
                listOfNumbers.clear()
                isNotOperation = true
                calculationImmediately = false
            }
        }
    }

    private fun operationClick() {
        binding.apply {
            divide.setOnClickListener {
                mathSymbolClick("/")
            }
            multiplication.setOnClickListener {
                mathSymbolClick("x")
            }
            subtraction.setOnClickListener {
                mathSymbolClick("-")
            }
            addition.setOnClickListener {
                mathSymbolClick("+")
            }
            percentage.setOnClickListener {
                if (checkOperationValidation()) {
                    firstInt = calculationEditText.text?.toString()?.toDouble()!!
                    percentageOperation()
                } else if (calculationTextView.text.isNotEmpty()) {
                    listOfNumbers.clear()
                    isNotOperation = false
                    calculationImmediately = true

                    percentageOperation()
                }
            }
        }
    }

    private fun commaClick() {
        binding.apply {
            comma.setOnClickListener {
                if (checkOperationValidation()) {
//                    binding.calculationEditText.text?.toString()?.toInt()
                    insertNumber(",")
                }
            }
        }
    }

    private fun equalClick() {
        binding.apply {
            equal.setOnClickListener {
                if (!isNotOperation) {
                    secondInt = calculationEditText.text?.toString()?.toDouble()!!
                    equalOperation()
                }
            }
        }
    }

    private fun mathSymbolClick(operator: String) {
        binding.apply {
            if (checkOperationValidation()) {
                firstInt = calculationEditText.text?.toString()?.toDouble()!!
                calculationEditText.text?.clear()

                if (firstInt.toString().contains(".0")) {
                    calculationTextView.text = "${firstInt.toInt()} $operator"
                } else {
                    calculationTextView.text = "$firstInt $operator"
                }
                listOfNumbers.clear()
                isNotOperation = false
            } else if (calculationTextView.text.isNotEmpty()) {
                if (firstInt.toString().contains(".0")) {
                    calculationTextView.text = "${calculatorResult.toInt()} $operator"
                } else {
                    calculationTextView.text = "$calculatorResult $operator"
                }
                listOfNumbers.clear()
                isNotOperation = false
                calculationImmediately = true
            }
        }
    }

    private fun checkOperationValidation(): Boolean {
        return listOfNumbers.isNotEmpty() && isNotOperation
    }

    //endregion

    //region ========================================== Operations ==========================================

    private fun divideOperation() {
        CoroutineScope(Dispatchers.Default).launch {
            calculatorResult = if (calculationImmediately) {
                calculatorResult.div(secondInt)
            } else {
                firstInt.div(secondInt)
            }

            displayCalculatorResult()
        }
    }

    private fun multiplyOperation() {
        CoroutineScope(Dispatchers.Default).launch {
            calculatorResult = if (calculationImmediately) {
                (calculatorResult * secondInt)
            } else {
                (firstInt * secondInt)
            }

            displayCalculatorResult()
        }
    }

    private fun subtractionOperation() {
        CoroutineScope(Dispatchers.Default).launch {
            calculatorResult = if (calculationImmediately) {
                calculatorResult.minus(secondInt)
            } else {
                firstInt.minus(secondInt)
            }

            displayCalculatorResult()
        }
    }

    private fun additionOperation() {
        CoroutineScope(Dispatchers.Default).launch {
            calculatorResult = if (calculationImmediately) {
                calculatorResult.plus(secondInt)
            } else {
                firstInt.plus(secondInt)
            }
            displayCalculatorResult()
        }
    }

    private fun percentageOperation() {
        CoroutineScope(Dispatchers.Default).launch {
            calculatorResult = if (calculationImmediately) {
                calculatorResult.div(100)
            } else {
                firstInt.div(100)
            }

            withContext(Dispatchers.Main) {
                binding.calculationTextView.text = "= $calculatorResult"
                binding.calculationEditText.text?.clear()
                listOfNumbers.clear()

                calculationImmediately = false
            }
        }
    }

    private suspend fun displayCalculatorResult() {
        withContext(Dispatchers.Main) {
            binding.apply {
                if (calculatorResult.toString().contains(".0")) {
                    calculationTextView.text = "= ${calculatorResult.toInt()}"
                } else {
                    calculationTextView.text = "= $calculatorResult"
                }
                calculationEditText.text?.clear()
            }

            calculationImmediately = false
        }
    }

    private fun equalOperation() {
        binding.calculationTextView.text?.apply {
            when {
                contains("/") -> {
                    divideOperation()
                }
                contains("x") -> {
                    multiplyOperation()
                }
                contains("-") -> {
                    subtractionOperation()
                }
                contains("+") -> {
                    additionOperation()
                }
            }
        }
        isNotOperation = true
        binding.calculationEditText.text!!.clear()
        listOfNumbers.clear()
    }

    //endregion

    //region =========================================== Numbers ============================================

    private fun numbersClick() {
        binding.apply {
            zero.setOnClickListener {
                insertNumber("0")
            }
            one.setOnClickListener {
                insertNumber("1")
            }
            two.setOnClickListener {
                insertNumber("2")
            }
            three.setOnClickListener {
                insertNumber("3")
            }
            four.setOnClickListener {
                insertNumber("4")
            }
            five.setOnClickListener {
                insertNumber("5")
            }
            six.setOnClickListener {
                insertNumber("6")
            }
            seven.setOnClickListener {
                insertNumber("7")
            }
            eight.setOnClickListener {
                insertNumber("8")
            }
            nine.setOnClickListener {
                insertNumber("9")
            }
        }
    }

    private fun insertNumber(number: String) {
        binding.apply {
            if (listOfNumbers.isEmpty()) {
                if (number != "0") {
                    listOfNumbers.add(number)
                    calculationEditText.text?.clear()
                }
            } else {
                calculationEditText.text?.clear()
                listOfNumbers.add(number)
            }

            for (i in listOfNumbers.indices) {
                calculationEditText.text?.insert(i, listOfNumbers[i])
            }
        }
    }

    //endregion
}