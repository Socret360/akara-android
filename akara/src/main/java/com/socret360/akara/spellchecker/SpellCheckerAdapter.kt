package com.socret360.akara.spellchecker

import com.socret360.akara.models.Correction

interface SpellCheckerAdapter {
//    fun isCorrect(word: String): Boolean
    fun corrections(word: String, numSuggestions: Int): ArrayList<String>
    fun close()
}