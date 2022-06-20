package com.socret360.akara.utils

import android.util.Log

object StringUtil {
    val TAG = "StringUtil"

    fun editDistance(s1: String, s2: String): Int {
        val numCols = s1.count() + 1
        val numRows = s2.count() + 1

        val memoize = Array(numRows) { Array<Int?>(numCols) { null } }

        for (colIdx in 0 until numCols) {
            memoize[0][colIdx] = colIdx
        }

        for (rowIdx in 0 until numRows) {
            memoize[rowIdx][0] = rowIdx
        }

        for (rowIdx in 1 until numRows) {
            for (colIdx in 1 until numCols) {
                val leftN = memoize[rowIdx][colIdx - 1] ?: 999
                val topN = memoize[rowIdx - 1][colIdx] ?: 999
                val crossN = memoize[rowIdx - 1][colIdx - 1] ?: 999

                val minN = minOf(crossN, topN, leftN)

                if (s1[colIdx - 1] != s2[rowIdx - 1]) {
                    memoize[rowIdx][colIdx] = minN + 1
                } else {
                    memoize[rowIdx][colIdx] = crossN
                }
            }
        }

        return memoize[numRows-1][numCols-1]!!
    }
}