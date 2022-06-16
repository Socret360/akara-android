package com.socret360.akara.models

class Sequence {
    var language: Language
    var text: String

    constructor(
        language: Language,
        text: String,
    ) {
        this.language = language
        this.text = text
    }

    override fun toString(): String {
        return "{${this.text}: ${this.language.name}}"
    }
}