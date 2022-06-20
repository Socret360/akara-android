package com.socret360.akara

import android.content.Context
import android.util.Log
import kotlin.concurrent.thread
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.languageid.LanguageIdentification

import com.socret360.akara.models.Word
import com.socret360.akara.models.Language
import com.socret360.akara.models.Sequence
import com.socret360.akara.utils.LanguageUtil
import com.socret360.akara.wordbreaker.WordBreaker
import com.socret360.akara.spellchecker.SpellChecker
import com.socret360.akara.nextwordpredictor.NextWordPredictor
import kotlin.system.measureTimeMillis

interface OnSuggestionListener {
    fun onCompleted(
        suggestions: List<String>,
        corrections: List<String>,
        sequences: List<Sequence>,
        words: List<String>
    )

    fun onError()
}

class Akara(context: Context) {
    private val TAG = "Akara"
    private val englishWordBreaker: WordBreaker = WordBreaker
        .Builder()
        .setLanguage(Language.ENGLISH)
        .build(context)
    private val khmerWordBreaker: WordBreaker = WordBreaker
        .Builder()
        .setLanguage(Language.KHMER)
        .build(context)
    private val englishNextWordPredictor: NextWordPredictor = NextWordPredictor
        .Builder()
        .setLanguage(Language.ENGLISH)
        .build(context)
    private val khmerNextWordPredictor: NextWordPredictor = NextWordPredictor
        .Builder()
        .setLanguage(Language.KHMER)
        .build(context)
    private val khmerSpellChecker: SpellChecker = SpellChecker
        .Builder()
        .setLanguage(Language.KHMER)
        .build(context)
    private val englishSpellChecker: SpellChecker = SpellChecker
        .Builder()
        .setLanguage(Language.ENGLISH)
        .build(context)
    private val languageDetector: LanguageIdentifier = LanguageIdentification.getClient()

    fun suggest(sentence: String, listener: OnSuggestionListener) {
        thread {
            val time = measureTimeMillis {
                val sequences = getSequences(sentence)
                val sequencesOfInterest = getSequencesOfInterest(sequences)
                val words = getWordsFromSequences(sequencesOfInterest)
                val suggestions = arrayListOf<String>()
                val corrections = arrayListOf<String>()
                val isLastWordCorrect = if (words.last().language == Language.KHMER) {
                    khmerSpellChecker.isCorrect(words.last().text)
                } else {
                    englishSpellChecker.isCorrect(words.last().text)
                }

                if (words.size == 1) {
                    if (isLastWordCorrect) {
                        suggestions.addAll(getNextWordSuggestions(words, true).map { it.text })
                    } else {
                        corrections.addAll(khmerSpellChecker.corrections(words.last().text).filter { !suggestions.contains(it) })
                    }
                } else {
                    if (isLastWordCorrect) {
                        suggestions.addAll(getNextWordSuggestions(words, true).map { it.text })
                    } else {
                        suggestions.addAll(getNextWordSuggestions(words, false).map { it.text })
                        corrections.addAll(khmerSpellChecker.corrections(words.last().text).filter { !suggestions.contains(it) })
                    }
                }

                listener.onCompleted(
                    suggestions.filter { khmerSpellChecker.isCorrect(it) },
                    corrections,
                    sequences,
                    words.map { it.text }
                )
            }

            Log.d(TAG, "suggestion time: $time")
        }
    }

    private fun getSequences(sentence: String): ArrayList<Sequence> {
        val chunks = sentence.trim().split(" ")
        val sequences = arrayListOf<Sequence>()

        var tmp = ""
        var prevLanguage: Language? = null

        for (i in 0..chunks.size) {
            if (i < chunks.size) {
                val chunk = chunks[i]
                val currentLanguage = getLanguage(chunk)
                if (prevLanguage == null) {
                    tmp += chunk
                } else if (prevLanguage == currentLanguage && currentLanguage == Language.ENGLISH) {
                    tmp += " $chunk"
                } else {
                    sequences.add(Sequence(prevLanguage, tmp))
                    tmp = chunk
                }
                prevLanguage = currentLanguage
            } else {
                sequences.add(Sequence(prevLanguage!!, tmp))
            }
        }

        return sequences
    }

    private fun getSequencesOfInterest(sequences: ArrayList<Sequence>): ArrayList<Sequence> {
        var prevLanguage: Language? = null
        val tmp: ArrayList<Sequence> = arrayListOf()
        for (i in sequences.size-1 downTo 0) {
            val currentLanguage = sequences[i].language
            if (prevLanguage != null && prevLanguage != currentLanguage) {
                return tmp
            }

            tmp.add(0, sequences[i])
            prevLanguage = currentLanguage
        }

        return tmp
    }

    private fun getWordsFromSequences(sequences: ArrayList<Sequence>): ArrayList<Word> {
        val words = arrayListOf<Word>()
        for (seq in sequences) {
            val wordBreaks = if (seq.language == Language.ENGLISH) {
                englishWordBreaker.split(seq.text)
            } else {
                khmerWordBreaker.split(seq.text)
            }

            if (wordBreaks != null) {
                for (w in wordBreaks) {
                    words.add(Word(w, seq.language))
                }
            }
        }
        return words
    }

    private fun getLanguage(text: String): Language {
        val task = languageDetector.identifyLanguage(text)
        val language = Tasks.await(task)

        if (language == "km") {
            return Language.KHMER
        }

        if (language == "en") {
            return Language.ENGLISH
        }

        for (char in text.toCharArray()) {
            if (LanguageUtil.EN_ALPHABET.contains(char)) {
                return Language.ENGLISH
            }
        }

        return Language.OTHER
    }

    private fun getNextWordSuggestions(words: ArrayList<Word>, isSequenceCompleted: Boolean): ArrayList<Word> {
        var inputText = words.joinToString(separator = "%") {
            it.text
        }

        if (isSequenceCompleted) {
            inputText += "%"
        }

        val suggestions = if (words[0].language == Language.KHMER) {
            ArrayList(khmerNextWordPredictor.predict(inputText).map {
                Word(it, Language.KHMER)
            })
        } else {
            arrayListOf<Word>()
        }

        return suggestions
    }
}