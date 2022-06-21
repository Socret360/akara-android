package com.socret360.akara.spellchecker

import android.content.Context
import com.socret360.akara.models.Language

class SpellChecker(builder: Builder) {
    private var model: SpellCheckerAdapter = builder.model

    fun isCorrect(word: String): Boolean {
        return model.isCorrect(word)
    }

    fun corrections(word: String, numSuggestions: Int = 3): ArrayList<String> {
        return model.corrections(word, numSuggestions)
    }

    fun close() {
        model.close()
    }

    class Builder() {
        private lateinit var language: Language
        internal lateinit var model: SpellCheckerAdapter

        fun setLanguage(language: Language): Builder {
            this.language = language
            return this
        }

        fun build(context: Context): SpellChecker {
            when(this.language) {
                Language.ENGLISH -> {
                    this.model = SpellCheckerEnglish(context)
                }
                Language.KHMER -> {
                    this.model = SpellCheckerKhmer(context)
                }
            }

            return SpellChecker(this)
        }
    }
}