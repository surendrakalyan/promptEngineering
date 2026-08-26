package com.example.promptengineeringlab

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etTask: EditText
    private lateinit var btnCompare: Button

    private lateinit var tvZeroShot: TextView
    private lateinit var tvFewShot: TextView
    private lateinit var tvExplanation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        etTask = findViewById(R.id.etTask)
        btnCompare = findViewById(R.id.btnCompare)

        tvZeroShot = findViewById(R.id.tvZeroShot)
        tvFewShot = findViewById(R.id.tvFewShot)
        tvExplanation = findViewById(R.id.tvExplanation)

        btnCompare.setOnClickListener {

            val task = etTask.text.toString().trim()

            if (task.isNotEmpty()) {
                comparePrompts(task)
            } else {
                etTask.error = "Please enter a task"
            }
        }
    }

    private fun comparePrompts(task: String) {

        btnCompare.isEnabled = false

        tvZeroShot.text = "Generating zero-shot response..."
        tvFewShot.text = "Generating few-shot response..."
        tvExplanation.text = "Generating explanation-based response..."

        lifecycleScope.launch {

            try {

                val response = ApiClient.apiService.comparePrompts(
                    CompareRequest(task)
                )

                if (response.isSuccessful) {

                    val result = response.body()

                    tvZeroShot.text =
                        result?.zero_shot
                            ?: "No zero-shot response."

                    tvFewShot.text =
                        result?.few_shot
                            ?: "No few-shot response."

                    tvExplanation.text =
                        result?.explanation_based
                            ?: "No explanation-based response."

                } else {

                    val error =
                        "Server error: ${response.code()}"

                    tvZeroShot.text = error
                    tvFewShot.text = error
                    tvExplanation.text = error
                }

            } catch (e: Exception) {

                val error =
                    "Connection error: ${e.message}"

                tvZeroShot.text = error
                tvFewShot.text = error
                tvExplanation.text = error

            } finally {

                btnCompare.isEnabled = true
            }
        }
    }
}