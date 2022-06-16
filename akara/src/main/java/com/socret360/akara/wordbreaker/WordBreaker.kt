package com.socret360.akara.wordbreaker

import android.content.Context
import com.socret360.akara.models.Language

class WordBreaker(builder: Builder) {
    private var model: WordBreakerAdapter = builder.model

    fun split(sentence: String): ArrayList<String>? {
        return model.split(sentence)
    }

    class Builder() {
        private lateinit var language: Language
        internal lateinit var model: WordBreakerAdapter

        fun setLanguage(language: Language): Builder {
            this.language = language
            return this
        }

        fun build(context: Context): WordBreaker {
            when(this.language) {
                Language.ENGLISH -> {
                    this.model = WordBreakerEnglish(context)
                }
                Language.KHMER -> {
                    this.model = WordBreakerKhmer(context)
                }
            }

            return WordBreaker(this)
        }
    }
}
