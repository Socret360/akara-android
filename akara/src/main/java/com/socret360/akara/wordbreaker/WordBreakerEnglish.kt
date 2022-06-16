package com.socret360.akara.wordbreaker

import android.content.Context

class WordBreakerEnglish(context: Context): WordBreakerAdapter {
    override fun split(sentence: String): ArrayList<String> {
        return ArrayList(sentence.trim().split(" "))
    }
}