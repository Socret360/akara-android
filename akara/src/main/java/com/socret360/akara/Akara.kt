package com.socret360.akara

import android.content.Context
import android.util.Log
import kotlin.concurrent.thread
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.socret360.akara.autocompletion.AutoCompletion

import com.socret360.akara.models.Word
import com.socret360.akara.models.Language
import com.socret360.akara.models.Sequence
import com.socret360.akara.models.SuggestionType
import com.socret360.akara.utils.LanguageUtil
import com.socret360.akara.wordbreaker.WordBreaker
import com.socret360.akara.spellchecker.SpellChecker
import com.socret360.akara.nextwordpredictor.NextWordPredictor
import kotlin.system.measureTimeMillis

interface OnSuggestionListener {
    fun onCompleted(
        suggestionType: SuggestionType?,
        suggestions: List<String>,
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
    private val englishAutoComplete: AutoCompletion = AutoCompletion
        .Builder()
        .setLanguage(Language.ENGLISH)
        .build(context)
    private val khmerAutoComplete: AutoCompletion = AutoCompletion
        .Builder()
        .setLanguage(Language.KHMER)
        .build(context)
    private val languageDetector: LanguageIdentifier = LanguageIdentification.getClient()

    fun suggest(sentence: String, listener: OnSuggestionListener) {
        thread {
            val time = measureTimeMillis {
                val sequences = getSequences(sentence)
                val sequencesOfInterest = getSequencesOfInterest(sequences)
                val words = getWordsFromSequences(sequencesOfInterest)
                val completions = getWordCompletions(words)

                if (completions.size > 0) {
                    listener.onCompleted(
                        SuggestionType.COMPLETION,
                        completions,
                        sequences,
                        words.map { it.text }
                    )
                } else {
                    val isLastWordCorrect = isWordCorrect(words.last())

                    if (isLastWordCorrect) {

                        listener.onCompleted(
                            SuggestionType.NEXT_WORD,
                            getNextWordSuggestions(words),
                            sequences,
                            words.map { it.text }
                        )

                    } else {

                        listener.onCompleted(
                            SuggestionType.CORRECTION,
                            getWordCorrections(words.last()),
                            sequences,
                            words.map { it.text }
                        )

                    }
                }
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
        for (i in sequences.size - 1 downTo 0) {
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
            seq.text = seq.text.filter { !LanguageUtil.NUMBERS.contains(it) }
            val wordBreaks = if (seq.language == Language.ENGLISH) {
                englishWordBreaker.split(seq.text.lowercase())
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

    private fun getWordCompletions(words: ArrayList<Word>): ArrayList<String> {
        return if (words.last().language == Language.ENGLISH) {
            englishAutoComplete.predict(words.last().text)
        } else {
            khmerAutoComplete.predict(words.last().text)
        }
    }

    private fun getNextWordSuggestions(words: ArrayList<Word>): List<String> {
        var inputText = words.joinToString(separator = "%") {
            it.text
        }
        inputText += "%"

        return if (words.last().language == Language.KHMER) {
            khmerNextWordPredictor.predict(inputText).map {
                Word(it, Language.KHMER)
            }
        } else {
            listOf<Word>()
        }.map { it.text }
    }

    private fun getWordCorrections(word: Word): List<String> {
        return if (word.language == Language.KHMER) {
            khmerSpellChecker.corrections(word.text)
        } else {
            englishSpellChecker.corrections(word.text)
        }
    }

    private fun isWordCorrect(word: Word): Boolean {
        return if (word.language == Language.KHMER) {
            khmerAutoComplete.isCorrect(word.text)
        } else {
            englishAutoComplete.isCorrect(word.text)
        }
    }
}