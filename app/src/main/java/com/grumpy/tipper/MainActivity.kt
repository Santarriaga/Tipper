package com.grumpy.tipper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import com.grumpy.tipper.databinding.ActivityMainBinding
import java.text.NumberFormat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var splitCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize views
        changeSplitCount()
        initializeOutput()



        // Respond to input text change
        binding.billAmount.editText?.doOnTextChanged { _, _, _, _ ->
            calculateTip()
        }
        //close keyboard
        binding.billAmountEditText.setOnKeyListener{ view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }

        binding.increaseButton.setOnClickListener {
            splitCount += 1
            changeSplitCount()
            calculateTip()
        }
        binding.decreaseButton.setOnClickListener{
            splitCount -= 1
            if(splitCount <= 0){
                splitCount = 1
            }
            changeSplitCount()
            calculateTip()
        }

        //change slider label to percentage
        binding.slider.setLabelFormatter { value: Float->
            return@setLabelFormatter "%${value.roundToInt()}"
        }
        // Responds to when slider's value is changed
        binding.slider.addOnChangeListener { _, _, _ ->
            calculateTip()
        }
        // To listen for a switch's checked/unchecked state changes
        binding.roundUpSwitch.setOnCheckedChangeListener { _, _ ->
            calculateTip()
        }
    }



    private fun changeSplitCount(){
        val count = splitCount.toString()
        binding.countSplit.text = getString(R.string.split_count,count)

    }

    private fun initializeOutput(){
        binding.tipResult.text = getString(R.string.tip_amount, " ")
        binding.splitTotal.text = getString(R.string.split_total," ")
        binding.billTotal.text = getString(R.string.total_bill," ")
    }

    private fun calculateTip(){
        val value = binding.slider.value
        val stringInTextField = binding.billAmountEditText.text.toString()
        val cost = stringInTextField.toDoubleOrNull()

        if(cost == null){
            initializeOutput()
            return
        }

        val tipPercentage = value/100
        var tipAmount = tipPercentage * cost
        if(binding.roundUpSwitch.isChecked){
            tipAmount = kotlin.math.ceil(tipAmount)
        }

        val tipFormatted = NumberFormat.getCurrencyInstance().format(tipAmount)
        binding.tipResult.text = getString(R.string.tip_amount, tipFormatted)

        val splitTotal = (cost+tipAmount)/splitCount
        val splitFormatted = NumberFormat.getCurrencyInstance().format(splitTotal)
        binding.splitTotal.text = getString(R.string.split_total,splitFormatted)

        val totalBill = cost + tipAmount
        val totalFormatted = NumberFormat.getCurrencyInstance().format(totalBill)
        binding.billTotal.text = getString(R.string.total_bill,totalFormatted)
    }

    // close keyboard after entering number
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }

}