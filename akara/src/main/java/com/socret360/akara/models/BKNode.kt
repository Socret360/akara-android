package com.socret360.akara.models

import com.gitlab.mvysny.konsumexml.Konsumer

data class BKNode(val word: String, val weight: Int, val children: List<BKNode>) {
    companion object {
        fun xml(k: Konsumer): BKNode {
            k.checkCurrent("node")
            return BKNode(
                k.attributes["word"],
                k.attributes["weight"].toInt(),
                k.child("children") { children("node") { BKNode.xml(this) } }
            )
        }
    }
}