package com.socret360.akara

import android.util.Log
import android.content.Context
import kotlin.concurrent.thread
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.languageid.LanguageIdentification

import com.socret360.akara.models.Word
import com.socret360.akara.models.Language
import com.socret360.akara.models.Sequence
import com.socret360.akara.utils.LanguageUtil
import com.socret360.akara.wordbreaker.WordBreaker

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
    private val languageDetector: LanguageIdentifier = LanguageIdentification.getClient()

    fun suggest(sentence: String) {
        thread {
            Log.d(TAG, "Input Sentence")
            Log.d(TAG, sentence)
            val sequences = getSequences(sentence)
            val sequencesOfInterest = getSequencesOfInterest(sequences)
            val words = getWordsFromSequences(sequencesOfInterest)

            Log.d(TAG, "Input Sequences")
            Log.d(TAG, sequences.toString())
            Log.d(TAG, "Sequences of Interests")
            Log.d(TAG, sequencesOfInterest.toString())
            Log.d(TAG, "Words")
            Log.d(TAG, words.toString())
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
                if (prevLanguage == null || prevLanguage == currentLanguage) {
                    tmp += if (currentLanguage == Language.ENGLISH && prevLanguage != null) {
                        " $chunk"
                    } else {
                        chunk
                    }
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
}