package com.socret360.akara.example

import android.os.Bundle
import com.socret360.akara.Akara
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var akara: Akara
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        akara = Akara(this)
//        akara.suggest("S12 Starbucks TK ខ្ញុំទៅ")
//        akara.suggest("ខ្ញុំទៅ S12 Starbucks TK")
//        akara.suggest("ខ្ញុំទៅ S12 Starbucks TK 12")
//        akara.suggest("សាកលវិទ្យាល័យ ប៊ែលធី អន្តរជាតិ សូមធ្វើការកោតសរសើរចំពោះសមត្ថភាព ទេពកោសល្យ និងចំណេះដឹង របស់កញ្ញា ហេង សុទ្ធនិស្ស័យ អតីតនិស្សិតឆ្នើមនៃសាកលវិទ្យាល័យ ប៊ែលធី អន្តរជាតិ សិក្សាជំនាញ ទំនាក់ទំនងអន្តរជាតិ ដែលកញ្ញាបានចូលរួមប្រកួតក្នុងកម្មវិធី បវរកញ្ញាចក្រវាលកម្ពុជា ឆ្នាំ២០២២ (Miss Universe Cambodia 2022) និងឈានដល់វគ្គផ្ដាច់ព្រ័ត្រ ក្នុងវេលាយប់នេះ។  សូមជូនពរកញ្ញា ហេង សុទ្ធនិស្ស័យ ទទួលបានជោគជ័យនៅក្នុងការប្រកួតលេីកនេះ។")
        akara.suggest("BELTEI International University would like to praise the ability, talent, and knowledge of Miss Heng Sothnisay, an outstanding alumnus of BELTEI International University majoring in International Relations, who competed in the Miss Universe Cambodia 2022 pageant and reached to the final round tonight. Wishing Miss Heng Sothnisay success in this competition.")
    }
}