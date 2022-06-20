package com.socret360.akara.models

import com.gitlab.mvysny.konsumexml.Konsumer

data class BKRoot(val node: BKNode) {
    companion object {
        fun xml(k: Konsumer): BKRoot {
            k.checkCurrent("root")
            return BKRoot(k.child("node") { BKNode.xml(this) })
        }
    }
}