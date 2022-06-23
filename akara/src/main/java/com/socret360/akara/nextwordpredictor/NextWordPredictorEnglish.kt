package com.socret360.akara.nextwordpredictor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.textservice.*
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import java.util.*


class NextWordPredictorEnglish(val context: Context): NextWordPredictorAdapter {
    private val TAG = "NextWordPredictorEnglish"

    init {

    }

    override fun predict(input: String): ArrayList<String> {
        return arrayListOf()
    }

    override fun close() {

    }
}