package com.socret360.akara.autocompletion

import android.content.Context
import android.util.Log
import com.socret360.akara.models.TrieNode
import com.socret360.akara.utils.LanguageUtil

class AutoCompletionKhmer(val context: Context): AutoCompletionAdapter {
    private val TAG = "AutoCompletionKhmer"
    private val charMap = LanguageUtil.KH_CONSTS + LanguageUtil.KH_SUB + LanguageUtil.KH_VOWELS + LanguageUtil.KH_SEPARATOR + LanguageUtil.KH_SYMS + LanguageUtil.KH_DIAC
    private var root: TrieNode = TrieNode(
        null,
        false,
        arrayOfNulls<TrieNode>(charMap.size)
    )

    init {
        this.loadModelFile()
    }

    override fun predict(text: String): ArrayList<String> {
        val prefix = text.lowercase()
        val prefixNode = getPrefixNode(prefix) ?: return arrayListOf()

        val completions = arrayListOf<String>()
        for (currNode in prefixNode.children) {
            if (currNode != null) {
                var t = ""
                val stack = arrayListOf<TrieNode>(currNode)

                while (stack.isNotEmpty()) {
                    val curr = stack.removeLast()
                    t += curr.char

                    if (curr.isEnd) {
                        completions.add(t)
                        t = ""
                    }
                }
            }
        }

        return ArrayList(completions.map {
            prefix + it
        }.sortedByDescending { it.length })
    }

    override fun close() {

    }

    private fun loadModelFile() {
        val inputStream = context.assets.open("khmer_words_dict.txt")
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                val word = it.replace("\n", "")
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
}