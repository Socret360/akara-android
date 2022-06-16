package com.socret360.akara.models

class Word {
    var text: String
    var language: Language

    constructor(
        text: String,
        language: Language
    ) {
        this.text = text
        this.language = language
    }

    override fun toString(): String {
        return "{${this.text}: ${this.language.name}}"
    }
}