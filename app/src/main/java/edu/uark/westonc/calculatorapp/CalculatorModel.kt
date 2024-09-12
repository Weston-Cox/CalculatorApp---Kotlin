package edu.uark.westonc.calculatorapp

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import java.util.Stack


class CalculatorModel {
    private val operandStack = Stack<String>()
    private val operatorStack = Stack<String>()
    private val lowerBound = BigDecimal("1E-6")
    private val upperBound = BigDecimal("1E6")
    // Convert to scientific notation using invariant Locale.ROOT
    private var formatter: NumberFormat =
        DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT))


//********************************************************************************************************************
// pushOperator(operator:String)
// Description: This function pushes the operator onto the operatorStack. If the operatorStack is not empty and the top
//              operator is not the same as the operator being pushed, the function swaps the operators. If the operandStack
//              has two elements and the operatorStack is not empty, the function calls performOperation() to perform the
//              operation. The function returns an empty string.
//********************************************************************************************************************
    fun pushOperator(operator: String):String {
        if (operatorStack.size > 0 && operatorStack.peek() != operator) {
            if (operandStack.size == 1) {
                operatorStack.pop()
                operatorStack.push(operator)
            } else {
                val temp = operatorStack.pop()
                operatorStack.push(operator) // Swap the operators
                operatorStack.push(temp)
            }
        }
        operatorStack.push(operator)

        if (operandStack.size == 2 && operatorStack.size > 0) {
            return performOperation()
        }

        return ""
    }

//********************************************************************************************************************
// pushOperand(operand:String)
// Description: This function pushes the operand onto the operandStack.
//********************************************************************************************************************
    fun pushOperand(operand: String):String {
        operandStack.push(operand)
        return ""
    }

//********************************************************************************************************************
// performOperation(equalSign:Boolean = false)
// Description: This function performs the operation specified by the operator at the top of the operatorStack. If the
//              operatorStack is empty or the operandStack is empty, the function returns "Failed to Perform Operation".
//              If the operatorStack is not empty and the operandStack has only one element, the function pops the
//              operator and the operand from the stacks and performs the operation with the second operator being 1 or 0.
//              If the operatorStack is not empty and the operandStack has two elements, the function pops the operator
//              and the operands from the stacks and performs the operation. If the equalSign parameter is true, the
//              function returns the result of the operation as a string. Otherwise, the function pushes the result
//              of the operation onto the operandStack and returns the result as a string.
//********************************************************************************************************************
    fun performOperation(equalSign:Boolean = false): String {
        val operator:String
        val operand1:String
        val operand2:String

        if (operatorStack.empty() || operandStack.empty()) {
            return "Failed to Perform Operation"
        } else if (operandStack.size == 1) {
            operator = operatorStack.pop()
            operand1 = operandStack.pop()
            operand2 = if (operator == "/") "1" else "0"
        } else {
            operator = operatorStack.pop()
            operand2 = operandStack.pop()
            operand1 = operandStack.pop()
        }

        if (!equalSign) {
            when (operator) {
                "+" -> operandStack.push(addOperands(operand1, operand2))
                "-" -> operandStack.push(subtractOperands(operand1, operand2))
                "*" -> operandStack.push(multiplyOperands(operand1, operand2))
                "/" -> operandStack.push(divideOperands(operand1, operand2))
            }
            return operandStack.peek()
        }
        else {
            when (operator) {
                "+" -> return addOperands(operand1, operand2)
                "-" -> return subtractOperands(operand1, operand2)
                "*" -> return multiplyOperands(operand1, operand2)
                "/" -> return divideOperands(operand1, operand2)
            }
        }
        return "0"
    }

//********************************************************************************************************************
// clearOperands()
// Description: This function clears the operandStack and operatorStack. It returns "0" as the output, and is called by
//              pressing the clear button on the calculator.
//********************************************************************************************************************
    fun clearOperands(): String {
        operandStack.clear()
        operatorStack.clear()

        return "0"
    }

//********************************************************************************************************************
// addOperands(operand1, operand2)
// Description: This function adds two operands together and returns the result as a string. If the result is within the
//              range of 1E-6 to 1E6, the result is returned as a plain string. Otherwise, the result is returned in
//              scientific notation.
//********************************************************************************************************************
    private fun addOperands(operand1: String, operand2: String): String {
        val result = BigDecimal(operand1).add(BigDecimal(operand2))

        if (result.abs() in lowerBound..upperBound) {
            return result.stripTrailingZeros().toPlainString()
        }
        return result.toPlainString()
    }

//********************************************************************************************************************
// subtractOperands(operand1, operand2)
// Description: This function subtracts operand2 from operand1 and returns the result as a string. If the result is within
//              the range of 1E-6 to 1E6, the result is returned as a plain string. Otherwise, the result is returned in
//              scientific notation.
//********************************************************************************************************************
    private fun subtractOperands(operand1: String, operand2: String): String {
        val result = BigDecimal(operand1).subtract(BigDecimal(operand2))


        if (result.abs() in lowerBound..upperBound) {
            return result.stripTrailingZeros().toPlainString()
        }
        return formatter.format(result)
    }

//********************************************************************************************************************
// multiplyOperands(operand1, operand2)
// Description: This function multiplies two operands together and returns the result as a string. If the result is within
//              the range of 1E-6 to 1E6, the result is returned as a plain string. Otherwise, the result is returned in
//              scientific notation.
//********************************************************************************************************************
    private fun multiplyOperands(operand1: String, operand2: String): String {
        val result = BigDecimal(operand1).multiply(BigDecimal(operand2))

        if (result.abs() in lowerBound..upperBound) {
            return result.stripTrailingZeros().toPlainString()
        }
        return formatter.format(result)
    }

//********************************************************************************************************************
// divideOperands(operand1, operand2)
// Description: This function divides operand1 by operand2 and returns the result as a string. If the result is within
//              the range of 1E-6 to 1E6, the result is returned as a plain string. Otherwise, the result is returned in
//              scientific notation. If operand2 is 0, the function returns "Division by zero error".
//********************************************************************************************************************
    private fun divideOperands(operand1: String, operand2: String): String {
        if (operand2 == "0") return "Division by zero error"

        val result = BigDecimal(operand1).divide(BigDecimal(operand2), 10, RoundingMode.HALF_UP)
        return if (result.stripTrailingZeros().scale() <= 0) {
            result.toBigInteger().toString()
        } else {
            if (result.abs() in lowerBound..upperBound) {
                result.stripTrailingZeros().toPlainString()
            } else {
                formatter.format(result)
            }
        }
    }
}