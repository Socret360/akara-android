package com.socret360.akara.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.socret360.akara.Akara
import com.socret360.akara.models.Sequence
import com.socret360.akara.OnSuggestionListener
import com.socret360.akara.models.SuggestionType

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    lateinit var akara: Akara

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        akara = Akara(this)
        val sentence = "ឯងនេះមួយយប់ៗដេកខ្វល់រឿងអនាគតមិនដឹងធ្វើអីចិញ្ចឹមខ្លួនមួយនេះរស់"
        var string = ArrayList(sentence.toCharArray().toMutableList())
        var input = ""
        var isProcessing = false
        while (string.isNotEmpty()) {
            if (!isProcessing) {
                isProcessing = true
                input += string.removeFirst()
                akara.suggest(input, object: OnSuggestionListener {
                    override fun onCompleted(
                        suggestionType: SuggestionType?,
                        suggestions: List<String>,
                        sequences: List<Sequence>,
                        words: List<String>
                    ) {
                        Log.d(TAG, "input: $input")
                        Log.d(TAG, "sequences: $sequences")
                        Log.d(TAG, "words: $words")
                        Log.d(TAG, "suggestions: ${suggestions.take(3)}")
                        Log.d(TAG, "suggestion_type: $suggestionType")
                        Log.d(TAG, "======")
                        isProcessing = false
                    }

                    override fun onError() {
                        Log.d(TAG, "$input: Cannot provide suggestions")
                        isProcessing = false
                    }
                })
            }
        }
    }
}