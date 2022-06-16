package com.socret360.akara.nextwordpredictor

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

class NextWordPredictorKhmer(private val context: Context): NextWordPredictorAdapter {
    private val SEQUENCE_LENGTH = 100
    private val N_UNIQUE_CHARS = 91
    private val PADDING_CHAR = '%'


    private var model: Interpreter
    private val charToIndex = HashMap<String, Int>()
    private val indexToChar = HashMap<Int, String>()


    init {
        this.model = loadModelFile()

        prepareCharMap()
    }

    private fun loadModelFile(): Interpreter {
        val fd = context.assets.openFd("khmer_nextword_pred_model.tflite")
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
        val inputStream = context.assets.open("khmer_nextword_pred_char_map.txt")
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                val tokens = it.split("\t")
                if(tokens.size == 2) {
                    val character = tokens[0]
                    val index = tokens[1]
                    charToIndex[character] = index.toInt()
                    indexToChar[index.toInt()] = character
                }
            }
        }
    }



    override fun predict(input: String): ArrayList<String> {
        val results = arrayListOf<String>()
        val initialCharacters = getNextChar(input)

        for (predictedChar in initialCharacters) {
            var tempInput: String = input + predictedChar
            var nextChar = getNextChar(tempInput)[0]
            while (nextChar != PADDING_CHAR + "") {
                tempInput += nextChar
                nextChar = getNextChar(tempInput)[0]
            }
            val chunks = tempInput.split(PADDING_CHAR);
            results.add(chunks[chunks.size - 1])
        }

        return results
    }

    private fun getNextChar(input: String, nChars: Int = 4): List<String> {
        var inputText = input.takeLast(SEQUENCE_LENGTH)
        inputText = inputText.padStart(SEQUENCE_LENGTH, PADDING_CHAR)

        val inputVector = convertInput(inputText)
        val results = Array(1) { FloatArray(N_UNIQUE_CHARS) }
        model.run(inputVector, results)
        val result = results[0]
        val sortedIndex = arrayListOf<FloatArray>()
        for( i in result.indices) {
            sortedIndex.add(floatArrayOf(i.toFloat(), result[i]))
        }

        sortedIndex.sortByDescending { it[1] }

        val listCharOutput = ArrayList<String>()

        for(i in 0 until nChars) {
            val char = indexToChar[sortedIndex[i][0].toInt()]
            listCharOutput.add(char!!)
        }

        return listCharOutput
    }

    private fun convertInput(text: String): Array<Array<FloatArray>> {
        val intValues = Array(1) {
            Array(SEQUENCE_LENGTH) {
                FloatArray(N_UNIQUE_CHARS){0f}
            }
        }
        val paddedText = if(text.length < SEQUENCE_LENGTH) {
            text.padStart(SEQUENCE_LENGTH, PADDING_CHAR)
        }else { // text is more than our max-char -> cut the start
            val startIndexToCut = text.length - SEQUENCE_LENGTH
            text.substring(startIndexToCut)
        }


        var index = 0
        for (char in paddedText) {
            val key = char + ""
            val charIndex = if(charToIndex[key] == null) {
                0
            } else {
                charToIndex[key]
            }
            intValues[0][index][charIndex!!] = 1.0f
            index++
        }

        return intValues
    }
}