package com.socret360.akara.wordbreaker

interface WordBreakerAdapter {
    fun split(sentence: String): ArrayList<String>
    fun close()
}