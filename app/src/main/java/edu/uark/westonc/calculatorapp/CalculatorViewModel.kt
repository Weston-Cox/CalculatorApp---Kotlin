package edu.uark.westonc.calculatorapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class CalculatorUiState(
    var operationState: Boolean = false,
    var numberState: Boolean = false,
    val result:Double = 0.0,
    val output:String = "0"
)

enum class CalcOperators{
    OP_PLUS,OP_MINUS,OP_TIMES,OP_DIVIDE, OP_EQUAL
}

enum class CalcNumbers{
    NUM_0,NUM_1,NUM_2,NUM_3,NUM_4,NUM_5,NUM_6,NUM_7,NUM_8,NUM_9
}

enum class CalcSpecialOps{
    OP_CLEAR, OP_DECIMAL, OP_BACKSPACE, OP_POSNEG
}


class CalculatorViewModel(private val calculatorModel: CalculatorModel):ViewModel() {
    // THIS HELPS THE ACTIVITY TO SURVIVE ORIENTATION CHANGES
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState:StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var doNotAppend = true

//********************************************************************************************************************
// opBtnPressed(operation: CalcOperators)
// Description: This function is called when an operator button is pressed. It pushes the current output onto the operandStack
//              and then pushes the operator onto the operatorStack. If the operator is the equal sign, the function calls
//              performOperation(). The function returns a new CalculatorUiState.
//********************************************************************************************************************
    fun opBtnPressed(operation: CalcOperators) {
        _uiState.update { currentState ->
            val currentOutput = currentState.output
            if (currentOutput != "") calculatorModel.pushOperand(currentOutput)
            val newOutput = when (operation) {
                CalcOperators.OP_PLUS -> calculatorModel.pushOperator("+")
                CalcOperators.OP_MINUS -> calculatorModel.pushOperator("-")
                CalcOperators.OP_TIMES -> calculatorModel.pushOperator("*")
                CalcOperators.OP_DIVIDE -> calculatorModel.pushOperator("/")
                CalcOperators.OP_EQUAL -> calculatorModel.performOperation(true)
            }

            doNotAppend = true

            currentState.copy(
                operationState = true,
                output = newOutput,
            )
        }
    }

//********************************************************************************************************************
// numberBtnPressed(number: CalcNumbers)
// Description: This function is called when a number button is pressed. It appends the number to the current output.
//              If the current output is "0" or "-0", the function replaces the output with the new number. The function
//              returns a new CalculatorUiState.
//********************************************************************************************************************
    fun numberBtnPressed(number: CalcNumbers){
        _uiState.update { currentState->
            val newOperand = when(number){
                CalcNumbers.NUM_0 -> 0
                CalcNumbers.NUM_1 -> 1
                CalcNumbers.NUM_2 -> 2
                CalcNumbers.NUM_3 -> 3
                CalcNumbers.NUM_4 -> 4
                CalcNumbers.NUM_5 -> 5
                CalcNumbers.NUM_6 -> 6
                CalcNumbers.NUM_7 -> 7
                CalcNumbers.NUM_8 -> 8
                CalcNumbers.NUM_9 -> 9
            }

            if ((currentState.output == "0" && !currentState.output.contains("-")) || doNotAppend) {
                doNotAppend = false
                currentState.copy(
                    numberState = true,
                    output = newOperand.toString(),
                )
            } else if (currentState.output == "-0") {
                currentState.copy(
                    numberState = true,
                    output = "-$newOperand"
                )
            } else {
                currentState.copy(
                    numberState = true,
                    output = (currentState.output + newOperand) // just append the new number
                )
            }
        }
    }

    //********************************************************************************************************************
// specialBtnPressed(specialOp: CalcSpecialOps)
// Description: This function is called when a special button is pressed. It performs the operation associated with the
//              special button. The function returns a new CalculatorUiState.
//********************************************************************************************************************
    fun specialBtnPressed(specialOp: CalcSpecialOps){
        _uiState.update { currentState->
            val currentOutput = currentState.output
            val newOutput = when(specialOp){
                CalcSpecialOps.OP_CLEAR -> calculatorModel.clearOperands()
                CalcSpecialOps.OP_DECIMAL -> if(!currentOutput.contains(".") || currentOutput == "0") "$currentOutput." else currentOutput
                CalcSpecialOps.OP_BACKSPACE -> if (currentOutput.contains("E")) currentOutput.substringBefore("E") else if(currentOutput.length > 1) currentOutput.substring(0,currentOutput.length-1) else "0"
                CalcSpecialOps.OP_POSNEG -> if(currentOutput.startsWith("-")) currentOutput.substring(1) else "-$currentOutput"
            }
            doNotAppend = false
            currentState.copy(
                output = newOutput
            )
        }
    }

//********************************************************************************************************************
// CalculatorViewModelFactory(model: CalculatorModel) CLASS
// Description: This class is used to create a new instance of the CalculatorViewModel class. It takes a CalculatorModel
//              object as a parameter and returns a new CalculatorViewModel object.
//********************************************************************************************************************
    class CalculatorViewModelFactory(private val model: CalculatorModel) : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(CalculatorViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return CalculatorViewModel(model) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}



