package com.socret360.akara.autocompletion

interface AutoCompletionAdapter {
    fun predict(text: String): ArrayList<String>
    fun isCorrect(word: String): Boolean
    fun close()
}