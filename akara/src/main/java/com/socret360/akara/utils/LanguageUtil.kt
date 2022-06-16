package com.socret360.akara.utils

object LanguageUtil {
    val EN_ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz"
    val EN_ALPHABET_UPPER = EN_ALPHABET_LOWER.toUpperCase()
    val EN_ALPHABET = EN_ALPHABET_LOWER.toCharArray() + EN_ALPHABET_UPPER.toCharArray()
    val SEPARATOR = "\u200b".toCharArray()
    val KH_CONSTS = "កខគឃងចឆជឈញដឋឌឍណតថទធនបផពភមយរលវឝឞសហឡអឣឤឥឦឧឨឩឪឫឬឭឮឯឰឱឲឳ".toCharArray()
    val KH_VOWELS = "឴឵ាិីឹឺុូួើឿៀេែៃោៅ\u17c6\u17c7\u17c8".toCharArray()
    val KH_SUB = "្".toCharArray()
    val KH_DIAC = "\u17c9\u17ca\u17cb\u17cc\u17cd\u17ce\u17cf\u17d0".toCharArray()
    val KH_SYMS = "៕។៛ៗ៚៙៘,.?!@#%^&*()_+-=[]/\\<>".toCharArray()
    val KH_NUMBERS = "០១២៣៤៥៦៧៨៩0123456789".toCharArray()

    fun removeSpecialChars() {}
}