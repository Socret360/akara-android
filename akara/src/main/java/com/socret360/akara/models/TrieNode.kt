package com.socret360.akara.models

class TrieNode(
    var char: Char?,
    var isEnd: Boolean,
    var children: Array<TrieNode?>
) {
    fun hasChar(charIndex: Int): Boolean {
        return this.children[charIndex] != null
    }

    fun getChild(charIndex: Int): TrieNode? {
        return this.children[charIndex]
    }
}