package com.socret360.akara.nextwordpredictor

interface NextWordPredictorAdapter {
    fun predict(input: String): ArrayList<String>
}