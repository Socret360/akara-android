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

//        Log.d(TAG, StringUtil.editDistance("ស្ងើ", "ស្ងើច").toString())

//        akara.suggest("S12 Starbucks TK ខ្ញុំទៅ")
//        akara.suggest("S12 Starbucks TK ចំណេះដឹង")
//        akara.suggest("S12 Starbucks TK ចំណេះដឹ")
//        akara.suggest("ខ្ញុំទៅ S12 Starbucks TK")
//        akara.suggest("ខ្ញុំទៅ S12 Starbucks TK 12")
//        akara.suggest("សាកលវិទ្យាល័យ ប៊ែលធី អន្តរជាតិ សូមធ្វើការកោតសរសើរចំពោះសមត្ថភាព ទេពកោសល្យ និងចំណេះដឹង របស់កញ្ញា ហេង សុទ្ធនិស្ស័យ អតីតនិស្សិតឆ្នើមនៃសាកលវិទ្យាល័យ ប៊ែលធី អន្តរជាតិ សិក្សាជំនាញ ទំនាក់ទំនងអន្តរជាតិ ដែលកញ្ញាបានចូលរួមប្រកួតក្នុងកម្មវិធី បវរកញ្ញាចក្រវាលកម្ពុជា ឆ្នាំ២០២២ (Miss Universe Cambodia 2022) និងឈានដល់វគ្គផ្ដាច់ព្រ័ត្រ ក្នុងវេលាយប់នេះ។  សូមជូនពរកញ្ញា ហេង សុទ្ធនិស្ស័យ ទទួលបានជោគជ័យនៅក្នុងការប្រកួតលេីកនេះ")
        //ឯងនេះមួយយប់ៗដេកខ្វល់រឿងអនាគតមិនដឹងធ្វើអីចិញ្ចឹមខ្លួនមួយនេះរស់
        //ខ្ញុំទៅ S12 Starbucks TK
        val sentence = "ឯងនេះមួយយប់ៗដេកខ្វល់រឿងអនាគតមិនដឹងធ្វើអីចិញ្ចឹមខ្លួនមួយនេះរស់"
//        val sentence = "would like to praise the ability"
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
                        Log.d(TAG, "suggestions: ${suggestions}")
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

//        akara.suggest("សូមធ្វើការកោតសរសើរចំពោះសមត្ថភា")
//        akara.suggest("ខ្ញុំទៅ")
//        akara.suggest("BELTEI International University would like to praise the ability, talent, and knowledge of Miss Heng Sothnisay, an outstanding alumnus of BELTEI International University majoring in International Relations, who competed in the Miss Universe Cambodia 2022 pageant and reached to the final round tonight. Wishing Miss Heng Sothnisay success in this competition.")
    }
}