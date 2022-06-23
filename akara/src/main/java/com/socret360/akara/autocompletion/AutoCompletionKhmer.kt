package com.socret360.akara.autocompletion

import android.content.Context
import android.util.Log
import com.socret360.akara.models.TrieNode
import com.socret360.akara.utils.LanguageUtil
import com.socret360.akara.utils.StringUtil

class AutoCompletionKhmer(val context: Context): AutoCompletionAdapter {
    private val TAG = "AutoCompletionKhmer"
    private val charMap = LanguageUtil.KH_CONSTS + LanguageUtil.KH_SUB + LanguageUtil.KH_VOWELS + LanguageUtil.KH_SEPARATOR + LanguageUtil.KH_SYMS + LanguageUtil.KH_DIAC
    private var root: TrieNode = TrieNode(
        null,
        false,
        arrayOfNulls<TrieNode>(charMap.size)
    )
    private var results: ArrayList<String>? = null

    init {
        this.loadModelFile()
    }

    override fun predict(text: String): ArrayList<String> {
        val prefixNode = getPrefixNode(text) ?: return arrayListOf()
        results = arrayListOf<String>()
        getCompletionsRec(prefixNode, text)
        val finalResults = results!!.map {
            mapOf<String, Any>("text" to it, "distance" to StringUtil.editDistance(it, text))
        }.filter {
            it["distance"] as Int > 2
        }.sortedBy {
            it["distance"] as Int
        }

        return ArrayList(finalResults.map { it["text"] as String })
    }

    override fun isCorrect(word: String): Boolean {
        val prefixNode = getPrefixNode(word)
        return prefixNode != null && prefixNode.isEnd
    }

    override fun close() {

    }

    private fun loadModelFile() {
        val inputStream = context.assets.open("khmer_words_dict.txt")
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                val word = it.trim()
                var currentNode = this.root
                for (char in word.toCharArray()) {
                    val charIndex = this.charMap.indexOf(char)
                    if (!currentNode.hasChar(charIndex)) {
                        currentNode.children[charIndex] = TrieNode(
                            char,
                            false,
                            arrayOfNulls<TrieNode>(charMap.size)
                        )
                    }

                    currentNode = currentNode.children[charIndex]!!
                }

                currentNode.isEnd = true
            }
        }
    }

    private fun getPrefixNode(text: String): TrieNode? {
        var currNode = this.root
        for (char in text.toCharArray()) {
            val charIndex = this.charMap.indexOf(char)
            if (currNode.hasChar(charIndex)) {
                currNode = currNode.children[charIndex]!!
            } else {
                return null
            }
        }

        return currNode
    }

    private fun getCompletionsRec(currNode: TrieNode, currPrefix: String) {
        if (currNode.isEnd) {
            results!!.add(currPrefix)
        }

        for (charNode in currNode.children) {
            if (charNode != null) {
                getCompletionsRec(charNode, currPrefix + charNode.char)
            }
        }
    }
}