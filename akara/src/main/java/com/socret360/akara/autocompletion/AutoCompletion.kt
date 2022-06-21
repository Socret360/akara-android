package com.socret360.akara.autocompletion
import android.content.Context
import com.socret360.akara.models.Language
import com.socret360.akara.nextwordpredictor.NextWordPredictorAdapter
import com.socret360.akara.nextwordpredictor.NextWordPredictorEnglish
import com.socret360.akara.nextwordpredictor.NextWordPredictorKhmer

class AutoCompletion(builder: Builder) {
    private var model: AutoCompletionAdapter = builder.model

    fun predict(text: String): ArrayList<String> {
        return model.predict(text)
    }

    fun close() {
        model.close()
    }

    class Builder() {
        private lateinit var language: Language
        internal lateinit var model: AutoCompletionAdapter

        fun setLanguage(language: Language): Builder {
            this.language = language
            return this
        }

        fun build(context: Context): AutoCompletion {
            when(this.language) {
                Language.ENGLISH -> {
                    this.model = AutoCompletionEnglish(context)
                }
                Language.KHMER -> {
                    this.model = AutoCompletionKhmer(context)
                }
            }

            return AutoCompletion(this)
        }
    }
}