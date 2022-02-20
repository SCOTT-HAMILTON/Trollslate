package org.scotthamilton.trollslate

import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.util.SizeF
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class RangeType { INDIVIDUALS, GROUP }

data class TypedRange(
    val range: IntRange,
    val type: RangeType
) {
    override fun toString() =
        (if (type == RangeType.INDIVIDUALS) "I" else "G") + "(${range})"
}

private fun IndivRange(start: Int, end: Int) =
    TypedRange(IntRange(start, end), RangeType.INDIVIDUALS)
private fun GroupRange(start: Int, end: Int) =
    TypedRange(IntRange(start, end), RangeType.GROUP)

@RequiresApi(Build.VERSION_CODES.N)
fun Codon2LetterPath(
    codon: String,
    canvas_size: Size
): Path? {
    val croppedCodon = codon
    val groupRanges =
        listOf(GroupRange(-1, -1)) +
                """\(\w*\)""".toRegex().findAll(croppedCodon).map {
                    GroupRange(it.range.first, it.range.last)
                }.toList() +
                listOf(GroupRange(croppedCodon.length, croppedCodon.length))
    val indivRanges = groupRanges.foldRight(
        GroupRange(0, 0) to mutableListOf<TypedRange>()
    ) { t, r ->
        println("t=$t, r=$r")
        val prev = r.first
        if (prev.range != 0..0) {
            val newstart = t.range.last + 1
            val newend = r.first.range.first - 1
            if (newend >= newstart) {
                r.second.add(IndivRange(newstart, newend))
            }
        }
        t to r.second
    }.second
    val (sortedRanges, firstPos) = (indivRanges + groupRanges.dropLast(1).drop(1))
        .sortedBy { it.range.first }.let {
            val first = it.first()
            val code = croppedCodon.substring(first.range)
            if (first.type == RangeType.INDIVIDUALS) {
                val start_pos = IndivCode2Point(code.first(), Point(0, 0))
                if (start_pos == null) {
                    println("[error] invalid start pos `$code`")
                    return null
                } else {
                    listOf(
                        IndivRange(
                            first.range.first + 1,
                            first.range.last
                        )
                    ) + it.drop(1) to start_pos
                }
            } else {
                it.drop(1) to GroupCode2Point(code, Point(0, 0))
            }
        }
    val path = Path()
    var cur_pos_pc = firstPos
    println("lol codon=$codon, firstPos=$firstPos")
    path.moveTo(pcToPoint(cur_pos_pc, canvas_size))
    sortedRanges.forEach {
        if (it.type == RangeType.INDIVIDUALS) {
            cur_pos_pc =
                applyIndivCode2Path(
                    croppedCodon.substring(it.range),
                    path,
                    cur_pos_pc,
                    canvas_size
                )
        } else {
            cur_pos_pc =
                applyGroupCode2Path(
                    croppedCodon.substring(it.range),
                    path,
                    cur_pos_pc,
                    canvas_size
                )
        }
    }
    return path
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
private fun Codon2Letter(
    modifier: Modifier,
    codon: String,
    textColor: Color,
    backgroundColor: Color
) {
//    println("lol split output: indivRanges = $indivRanges, groupRanges: $groupRanges, sortedRanges=$sortedRanges")
    Canvas(
        modifier = modifier.background(backgroundColor).width(50.dp),
        onDraw = {
            drawRect(color = backgroundColor)
            Codon2LetterPath(codon, size)?.let {
                drawPath(it, color = textColor, style = Stroke(width = 5f))
            }
        }
    )
}

private fun Path.moveTo(point: PointF) {
    moveTo(point.x, point.y)
}

private fun Path.lineTo(point: PointF) {
    lineTo(point.x, point.y)
}

fun IndivCode2Point(code: Char, start_cur_pos_pc: Point) : Point? {
    val line  = FontData.linesMap[code]
    return if (line == null) {
        println("[error] unknown line `$code`")
        null
    } else {
        Point(
            (if (line is VLine) line.pc else start_cur_pos_pc.x),
            (if (line is HLine) line.pc else start_cur_pos_pc.y)
        )
    }
}

fun GroupCode2Point(code: String, start_cur_pos_pc: Point) : Point {
    val lines = code.map { FontData.linesMap[it] }.filterNotNull()
    return lines.fold(start_cur_pos_pc) { pos, line ->
        Point(
            (if (line is VLine) line.pc else pos.x),
            (if (line is HLine) line.pc else pos.y)
        )
    }
}

fun applyIndivCode2Path(code: String,
                        path: Path,
                        start_cur_pos_pc: Point,
                        canvas_size: Size) : Point {
    var cur_pos_pc = start_cur_pos_pc
    code.forEach {
        IndivCode2Point(it, cur_pos_pc)?.let { new_pos ->
            println("lol applyIndivCode2Path $code -> $new_pos")
            path.lineTo(pcToPoint(new_pos, canvas_size))
            cur_pos_pc = new_pos
        }
    }
    return cur_pos_pc
}
fun applyGroupCode2Path(code: String,
                        path: Path,
                        start_cur_pos_pc: Point,
                        canvas_size: Size) : Point {
    val new_pos = GroupCode2Point(code, start_cur_pos_pc)
    println("lol applyGroupCode2Path $code -> $new_pos")
    val p = pcToPoint(new_pos, canvas_size)

    if (code.length >= 2 && code[1] == '_')  {
        path.moveTo(p)
    } else {
        path.lineTo(p)
    }
    return new_pos
}

fun pcToPoint(pcPoint: Point, canvas_size: Size) =
    PointF(canvas_size.width.toFloat()*pcPoint.x.toFloat()/100f,
        canvas_size.height.toFloat()*(100-pcPoint.y).toFloat()/100f)

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Letter(
    modifier: Modifier,
    letter: Char = 'A',
//    canvas_size: SizeF,
    backgroundColor: Color,
    textColor: Color
) {
    val codon = FontData.lettersCodonTable[letter]
    if (codon == null) {
        println("[error] unsupported character `$letter`, can't display it out.")
    } else {
        Box (modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)) {
            Codon2Letter(modifier.align(Alignment.Center)
                .background(backgroundColor), codon, textColor, backgroundColor)
        }
    }
}