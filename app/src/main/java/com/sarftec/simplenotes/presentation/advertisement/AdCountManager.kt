package com.sarftec.simplenotes.presentation.advertisement


class AdCountManager(private val pattern: List<Int>) {
    private var patternIterator = 0
    private var currentMax = pattern[patternIterator] - 1
    private var increments = -1

    fun canShow(): Boolean {
        increments++
        return if (increments >= currentMax) {
            increments = -1
            patternIterator++
            if (patternIterator >= pattern.size) patternIterator = 0
            currentMax = pattern[patternIterator] - 1
            true
        } else false
    }
}