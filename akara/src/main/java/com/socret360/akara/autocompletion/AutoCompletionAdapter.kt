package com.socret360.akara.autocompletion

interface AutoCompletionAdapter {
    fun predict(text: String): ArrayList<String>
    fun close()
}