package com.socret360.akara.wordbreaker

import android.content.Context
import android.util.Log
import java.io.FileInputStream
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

class WordBreakerKhmer(private val context: Context): WordBreakerAdapter {
    private val TAG = "WordBreakerKhmer"
    private var N_UNIQUE_CHARS: Int = 133
    private var N_UNIQUE_POS: Int = 16
    private var MAX_SENTENCE_LENGTH: Int = 687

    private var charToIndex = HashMap<String, Int>()
    private var indexToChar = HashMap<Int, String>()
    private var posToIndex = HashMap<String, Int>()
    private var indexToPos = HashMap<Int, String>()
    private var model: Interpreter

    init {
        model = loadModelFile()
        prepareCharMap()
        preparePosMap()
    }

    override fun split(sentence: String): ArrayList<String> {
        val inputVector = Array(MAX_SENTENCE_LENGTH) {
            FloatArray(N_UNIQUE_CHARS) { 0.0f }
        }

        val results = Array(1) {
            Array(MAX_SENTENCE_LENGTH) {
                FloatArray(N_UNIQUE_POS)
            }
        }

        val wordBreaks = arrayListOf<String>()

        var sequenceIdx = 0
        for (char in sentence) {
            val charIdx = if (charToIndex.containsKey(char.toString())) {
                charToIndex[char.toString()]!!
            } else {
                charToIndex["UNK"]
            }
            inputVector[sequenceIdx][charIdx!!] = 1.0f
            sequenceIdx += 1
        }

        model.run(arrayOf(inputVector), results)
        val predicted = results[0]

        sequenceIdx = 0
        var tmp: String = ""
        for (char in sentence) {
            val posVector = predicted[sequenceIdx]
            var posIndex = posVector.indexOfFirst {
                posVector.maxOrNull() == it
            }
            posIndex = if (posIndex == -1) {
                15
            } else {
                posIndex
            }

            if (indexToPos[posIndex] == "NS") {
                tmp += char.toString()
            } else {
                if (tmp.isNotEmpty()) {
                    wordBreaks.add(tmp)
                    tmp = ""
                }

                tmp += char.toString()
            }
            sequenceIdx += 1
        }

        if (tmp.isNotEmpty()) {
            wordBreaks.add(tmp)
        }

        return wordBreaks
    }

    private fun loadModelFile(): Interpreter {
        val fd = context.assets.openFd("khmer_word_seg_model.tflite")
        val inputStream = FileInputStream(fd.fileDescriptor)
        val fileChannel = inputStream.channel
        return Interpreter(
            fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fd.startOffset,
                fd.declaredLength
            )
        )
    }

    private fun prepareCharMap() {
        val inputStream = context.assets.open("khmer_word_seg_char_map.txt")
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                val tokens = it.split("\t")
                if (tokens.size > 1) {
                    val character = tokens[0]
                    val index = tokens[1]
                    charToIndex[character] = index.toInt()
                    indexToChar[index.toInt()] = character
                }
            }
        }
        charToIndex["UNK"] = N_UNIQUE_CHARS - 1
        indexToChar[N_UNIQUE_CHARS - 1] = "UNK"
    }

    private fun preparePosMap() {
        val inputStream = context.assets.open("khmer_word_seg_pos_map.txt")
        var index = 0
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                val pos = it.replace("\n", "")
                posToIndex[pos] = index
                indexToPos[index] = pos
            }
            index += 1
        }
    }
}