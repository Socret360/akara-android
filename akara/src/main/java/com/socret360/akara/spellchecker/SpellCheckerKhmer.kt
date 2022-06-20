package com.socret360.akara.spellchecker

import android.content.Context
import com.socret360.akara.models.BKNode
import com.gitlab.mvysny.konsumexml.konsumeXml

import com.socret360.akara.models.BKRoot
import com.socret360.akara.utils.StringUtil

class SpellCheckerKhmer(val context: Context, val N: Int = 2): SpellCheckerAdapter {
    private val TAG = "SpellCheckerKhmer"
    private var root: BKNode?

    init {
        this.root = this.loadModelFile()
    }

    override fun isCorrect(word: String): Boolean {
        val stack = arrayListOf(this.root)

        while (stack.isNotEmpty()) {
            val currNode = stack.removeLast()!!
            val distanceCurrNode = StringUtil.editDistance(word, currNode.word)

            if (distanceCurrNode == 0) {
                return true
            }

            for (child in currNode.children) {
                if (child.weight >= (distanceCurrNode - N) && child.weight <= (distanceCurrNode + N)) {
                    stack.add(child)
                }
            }
        }

        return false
    }

    override fun corrections(word: String, numSuggestions: Int): ArrayList<String> {
        val stack = arrayListOf(this.root)
        val corrections = arrayListOf<BKNode>()

        while (stack.isNotEmpty()) {
            val currNode = stack.removeLast()!!
            val distanceCurrNode = StringUtil.editDistance(word, currNode.word)

            if (distanceCurrNode <= N && !corrections.any {
                it.word == currNode.word && it.weight == distanceCurrNode
                }) {
                corrections.add(BKNode(currNode.word, distanceCurrNode, arrayListOf()))
            }

            for (child in currNode.children) {
                if (child.weight >= (distanceCurrNode - N) && child.weight <= (distanceCurrNode + N)) {
                    stack.add(child)
                }
            }
        }

        return ArrayList(corrections.sortedBy {
            it.weight
        }.map {
            it.word
        })
    }

    override fun close() {
        this.root = null
    }

    private fun loadModelFile(): BKNode {
        var xmlContent = ""
        val inputStream = context.assets.open("khmer_spell_checker_model.xml")
        inputStream.bufferedReader().forEachLine {
            if (it.isNotEmpty()) {
                xmlContent += it
            }
        }

        val cfg = xmlContent.konsumeXml().use {
                k -> k.child("root") { BKRoot.xml(this) }
        }

        return cfg.node
    }
}

