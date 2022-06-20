package com.socret360.akara.nextwordpredictor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.textservice.*
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import java.util.*


class NextWordPredictorEnglish(val context: Context): NextWordPredictorAdapter,
    SpellCheckerSession.SpellCheckerSessionListener {
    private val TAG = "NextWordPredictorEnglish"

    private var model: SpellCheckerSession?
    private var task: TaskCompletionSource<ArrayList<String>>? = null

    init {
        val textServicesManager = context.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager
        this.model = textServicesManager.newSpellCheckerSession(null, Locale.ENGLISH, this, true)
    }

    override fun predict(input: String): ArrayList<String> {
        task = TaskCompletionSource<ArrayList<String>>()
        this.model!!.getSentenceSuggestions(arrayOf(TextInfo(input)), 3)
        return Tasks.await(task!!.task)
    }

    override fun close() {
        if (this.model != null) {
            this.model!!.close()
            this.model = null
        }
    }

    @SuppressLint("LongLogTag")
    override fun onGetSuggestions(p0: Array<out SuggestionsInfo>?) {
        Log.d(TAG, "onGetSuggestions")
    }

    @SuppressLint("LongLogTag")
    override fun onGetSentenceSuggestions(p0: Array<out SentenceSuggestionsInfo>?) {
        Log.d(TAG, "onGetSentenceSuggestions")
        val results = arrayListOf<String>()
        if (p0 != null) {
            for (i in p0.indices) {
                // Returned suggestions are contained in SuggestionsInfo
                for (j in 0 until p0[i].suggestionsCount) {
                    val si: SuggestionsInfo = p0[i].getSuggestionsInfoAt(j)
                    results.add(si.getSuggestionAt(j))
                }
            }
        }

        this.task!!.setResult(results)
    }
}