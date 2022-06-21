package com.socret360.akara.nextwordpredictor

import android.content.Context
import com.socret360.akara.models.Language

class NextWordPredictor(builder: Builder) {
    private var model: NextWordPredictorAdapter = builder.model

    fun predict(input: String): ArrayList<String> {
        return model.predict(input)
    }

    fun close() {
        model.close()
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