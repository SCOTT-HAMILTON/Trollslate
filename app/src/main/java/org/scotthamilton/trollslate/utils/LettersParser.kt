package org.scotthamilton.trollslate.utils

import android.graphics.Point
import org.scotthamilton.trollslate.ui.groupCode2Point
import org.scotthamilton.trollslate.ui.indivCode2Point

enum class RangeType {
    INDIVIDUALS,
    GROUP
}

data class TypedRange(val range: IntRange, val type: RangeType) {
    override fun toString() = (if (type == RangeType.INDIVIDUALS) "I" else "G") + "(${range})"
}

fun indivRange(start: Int, end: Int) = TypedRange(IntRange(start, end), RangeType.INDIVIDUALS)

fun groupRange(start: Int, end: Int) = TypedRange(IntRange(start, end), RangeType.GROUP)

fun parseCodon(codon: String): Pair<List<TypedRange>, Point>? {
    if (codon.isEmpty()) {
        return listOf<TypedRange>() to Point()
    }
    val groupRanges =
        listOf(groupRange(-1, -1)) +
            """\(\w*\)"""
                .toRegex()
                .findAll(codon)
                .map { groupRange(it.range.first, it.range.last) }
                .toList() +
            listOf(groupRange(codon.length, codon.length))
    val indivRanges =
        groupRanges
            .foldRight(groupRange(0, 0) to mutableListOf<TypedRange>()) { t, r ->
                val prev = r.first
                if (prev.range != 0..0) {
                    val newstart = t.range.last + 1
                    val newend = r.first.range.first - 1
                    if (newend >= newstart) {
                        r.second.add(indivRange(newstart, newend))
                    }
                }
                t to r.second
            }
            .second
    return (indivRanges + groupRanges.dropLast(1).drop(1)).sortedBy { it.range.first }.let {
        val first = it.first()
        val code = codon.substring(first.range)
        if (first.type == RangeType.INDIVIDUALS) {
            val startPos = indivCode2Point(code.first(), Point(0, 0))
            if (startPos == null) {
                println("[error] invalid start pos `$code`")
                null
            } else {
                listOf(indivRange(first.range.first + 1, first.range.last)) + it.drop(1) to startPos
            }
        } else {
            it.drop(1) to groupCode2Point(code, Point(0, 0))
        }
    }
}
