package com.socret360.akara.nextwordpredictor

import android.content.Context
import com.socret360.akara.models.Language
import com.socret360.akara.wordbreaker.WordBreaker
import com.socret360.akara.wordbreaker.WordBreakerAdapter
import com.socret360.akara.wordbreaker.WordBreakerEnglish
import com.socret360.akara.wordbreaker.WordBreakerKhmer

class NextWordPredictor(builder: Builder) {
    private var model: NextWordPredictorAdapter = builder.model

    fun predict(input: String): ArrayList<String> {
        return model.predict(input)
    }

    class Builder() {
        private lateinit var language: Language
        internal lateinit var model: NextWordPredictorAdapter

        fun setLanguage(language: Language): Builder {
            this.language = language
            return this
        }

        fun build(context: Context): NextWordPredictor {
            when(this.language) {
                Language.ENGLISH -> {
                    this.model = NextWordPredictorEnglish(context)
                }
                Language.KHMER -> {
                    this.model = NextWordPredictorKhmer(context)
                }
            }

            return NextWordPredictor(this)
        }
    }
}