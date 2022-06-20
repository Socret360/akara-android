package com.socret360.akara.models

class Correction {
    var word: String
    var weight: Int

    constructor(
        word: String,
        weight: Int,
    ) {
        this.word = word
        this.weight = weight
    }
}