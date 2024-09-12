package edu.uark.westonc.calculatorapp


import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var tvOutput: TextView
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnDivide: Button
    private lateinit var btnResult: Button
    private val logTag = "MainActivity"
    private val calculatorViewModel: CalculatorViewModel by viewModels {
        CalculatorViewModel.CalculatorViewModelFactory((application as CalculatorApplication).calculatorModel)
    }
    override fun onResume() {
        super.onResume()
        Log.d(logTag, "onResume Called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(logTag, "onStart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(logTag, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(logTag, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(logTag, "onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(logTag, "onRestart Called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate Called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvOutput = findViewById(R.id.output)

        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivide = findViewById(R.id.btnDivide)
        btnResult = findViewById(R.id.btnResult)

        // Operation Buttons
        btnPlus.setOnClickListener {
            calculatorViewModel.opBtnPressed(CalcOperators.OP_PLUS)
            highlightButton(btnPlus)
        }

        btnMinus.setOnClickListener {
            calculatorViewModel.opBtnPressed(CalcOperators.OP_MINUS)
            highlightButton(btnMinus)
        }

        btnMultiply.setOnClickListener {
            calculatorViewModel.opBtnPressed(CalcOperators.OP_TIMES)
            highlightButton(btnMultiply)
        }

        btnDivide.setOnClickListener {
            calculatorViewModel.opBtnPressed(CalcOperators.OP_DIVIDE)
            highlightButton(btnDivide)
        }

        btnResult.setOnClickListener {
            calculatorViewModel.opBtnPressed(CalcOperators.OP_EQUAL)
            resetButtonColors()
        }

        // Number Buttons
        findViewById<Button>(R.id.btnZero).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_0)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnOne).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_1)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnTwo).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_2)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnThree).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_3)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnFour).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_4)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnFive).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_5)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnSix).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_6)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnSeven).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_7)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnEight).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_8)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnNine).setOnClickListener {
            calculatorViewModel.numberBtnPressed(CalcNumbers.NUM_9)
            resetButtonColors()
        }

        // Special Buttons
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            calculatorViewModel.specialBtnPressed(CalcSpecialOps.OP_CLEAR)
            resetButtonColors()
        }

        findViewById<Button>(R.id.btnDecimal).setOnClickListener { calculatorViewModel.specialBtnPressed(CalcSpecialOps.OP_DECIMAL) }

        findViewById<Button>(R.id.btnBackspace).setOnClickListener { calculatorViewModel.specialBtnPressed(CalcSpecialOps.OP_BACKSPACE) }

        findViewById<Button>(R.id.btnPosNeg).setOnClickListener { calculatorViewModel.specialBtnPressed(CalcSpecialOps.OP_POSNEG) }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                calculatorViewModel.uiState.collect { state ->
                    // Update UI elements
                    tvOutput.text = state.output
                    animateTextView(tvOutput)
                }
            }
        }
    }

    private fun highlightButton(button: Button) {
        resetButtonColors()
        button.setBackgroundColor(resources.getColor(R.color.button_pressed_color, null))
//        button.setTextColor(resources.getColor(R.color.white, null))
    }

    private fun resetButtonColors() {
        btnPlus.setBackgroundColor(resources.getColor(R.color.button_default_color, null))
//        btnPlus.setTextColor(resources.getColor(R.color.black, null))
        btnMinus.setBackgroundColor(resources.getColor(R.color.button_default_color, null))
//        btnMinus.setTextColor(resources.getColor(R.color.black, null))
        btnMultiply.setBackgroundColor(resources.getColor(R.color.button_default_color, null))
//        btnMultiply.setTextColor(resources.getColor(R.color.black, null))
        btnDivide.setBackgroundColor(resources.getColor(R.color.button_default_color, null))
//        btnDivide.setTextColor(resources.getColor(R.color.black, null))
    }

    private fun animateTextView(textView: TextView) {
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeIn.duration = 200 // duration in milliseconds
        fadeIn.start()
    }
}