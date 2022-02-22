package org.scotthamilton.trollslate.ui

import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import kotlin.math.*
import org.scotthamilton.trollslate.data.FontData
import org.scotthamilton.trollslate.data.HLine
import org.scotthamilton.trollslate.data.VLine

enum class RangeType {
    INDIVIDUALS,
    GROUP
}

data class TypedRange(val range: IntRange, val type: RangeType) {
    override fun toString() = (if (type == RangeType.INDIVIDUALS) "I" else "G") + "(${range})"
}

private fun indivRange(start: Int, end: Int) =
    TypedRange(IntRange(start, end), RangeType.INDIVIDUALS)

private fun groupRange(start: Int, end: Int) = TypedRange(IntRange(start, end), RangeType.GROUP)

private fun parseCodon(codon: String): Pair<List<TypedRange>, Point> {
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
                TODO()
            } else {
                listOf(indivRange(first.range.first + 1, first.range.last)) + it.drop(1) to startPos
            }
        } else {
            it.drop(1) to groupCode2Point(code, Point(0, 0))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun drawCodonLetter(
    codon: String,
    canvas_size: Size,
    color: Color,
    strokeWidth: Float,
    angle: Float,
    adaptiveStrokes: Boolean,
    drawScope: DrawScope
) {
    val (sortedRanges, firstPos) = parseCodon(codon)
    var curPosPc = firstPos
    sortedRanges.forEach {
        curPosPc =
            if (it.type == RangeType.INDIVIDUALS) {
                drawIndivCode(
                    codon.substring(it.range),
                    curPosPc,
                    canvas_size,
                    color,
                    strokeWidth,
                    angle,
                    adaptiveStrokes,
                    drawScope
                )
            } else {
                drawGroupCode(
                    codon.substring(it.range),
                    curPosPc,
                    canvas_size,
                    color,
                    strokeWidth,
                    angle,
                    adaptiveStrokes,
                    drawScope
                )
            }
    }
}

fun codonLetterToPath(codon: String, canvas_size: Size): Path {
    val (sortedRanges, firstPos) = parseCodon(codon)
    var curPosPc = firstPos
    val path = Path()
    path.moveTo(pcToPoint(curPosPc, canvas_size))
    sortedRanges.forEach {
        curPosPc =
            if (it.type == RangeType.INDIVIDUALS) {
                applyIndivCodeToPath(codon.substring(it.range), path, curPosPc, canvas_size)
            } else {
                applyGroupCodeToPath(codon.substring(it.range), path, curPosPc, canvas_size)
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
    backgroundColor: Color,
    strokeWidth: Float,
    angle: Float,
    adaptiveStrokes: Boolean
) {
    Canvas(
        modifier = modifier.background(backgroundColor).width(50.dp),
        onDraw = {
            drawRect(color = backgroundColor)
            drawCodonLetter(codon, size, textColor, strokeWidth, angle, adaptiveStrokes, this)
        }
    )
}

fun indivCode2Point(code: Char, start_cur_pos_pc: Point): Point? {
    val line = FontData.linesMap[code]
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

fun groupCode2Point(code: String, start_cur_pos_pc: Point): Point {
    val lines = code.map { FontData.linesMap[it] }.filterNotNull()
    return lines.fold(start_cur_pos_pc) { pos, line ->
        Point((if (line is VLine) line.pc else pos.x), (if (line is HLine) line.pc else pos.y))
    }
}

private fun Path.moveTo(point: PointF) {
    moveTo(point.x, point.y)
}

private fun Path.lineTo(point: PointF) {
    lineTo(point.x, point.y)
}

fun drawIndivCode(
    code: String,
    start_cur_pos_pc: Point,
    canvas_size: Size,
    color: Color,
    strokeWidth: Float,
    angle: Float,
    adaptiveStrokes: Boolean,
    drawScope: DrawScope
): Point {
    var curPosPc = start_cur_pos_pc
    code.forEach {
        indivCode2Point(it, curPosPc)?.let { new_pos ->
            drawPcLine(
                curPosPc,
                new_pos,
                canvas_size,
                color,
                strokeWidth,
                angle,
                adaptiveStrokes,
                drawScope
            )
            curPosPc = new_pos
        }
    }
    return curPosPc
}

fun applyIndivCodeToPath(
    code: String,
    path: Path,
    start_cur_pos_pc: Point,
    canvas_size: Size
): Point {
    var curPosPc = start_cur_pos_pc
    code.forEach {
        indivCode2Point(it, curPosPc)?.let { new_pos ->
            path.lineTo(pcToPoint(new_pos, canvas_size))
            curPosPc = new_pos
        }
    }
    return curPosPc
}

fun drawGroupCode(
    code: String,
    start_cur_pos_pc: Point,
    canvas_size: Size,
    color: Color,
    strokeWidth: Float,
    angle: Float,
    adaptiveStrokes: Boolean,
    drawScope: DrawScope
): Point {
    val newPos = groupCode2Point(code, start_cur_pos_pc)
    if (!(code.length >= 2 && code[1] == '_')) {
        drawPcLine(
            start_cur_pos_pc,
            newPos,
            canvas_size,
            color,
            strokeWidth,
            angle,
            adaptiveStrokes,
            drawScope
        )
    }
    return newPos
}

fun applyGroupCodeToPath(
    code: String,
    path: Path,
    start_cur_pos_pc: Point,
    canvas_size: Size
): Point {
    val newPos = groupCode2Point(code, start_cur_pos_pc)
    val p = pcToPoint(newPos, canvas_size)

    if (code.length >= 2 && code[1] == '_') {
        path.moveTo(p)
    } else {
        path.lineTo(p)
    }
    return newPos
}

data class FloatRange(val first: Float, val last: Float) {
    fun count(): Float = abs(last - first)
}

private fun flerp(from: FloatRange, to: FloatRange, value: Float): Float =
    (value - from.first) * to.count() / from.count() + to.first

private fun drawPcLine(
    start_cur_pos_pc: Point,
    newPos: Point,
    canvas_size: Size,
    color: Color,
    strokeWidth: Float,
    angle: Float,
    adaptiveStrokes: Boolean,
    drawScope: DrawScope
) {
    val start = pcToPoint(start_cur_pos_pc, canvas_size)
    val end = pcToPoint(newPos, canvas_size)
    if (adaptiveStrokes && abs(start.x - end.x) > 0.10f) {
        val (s, e) = if (start.x > end.x) end to start else start to end
        val angleSin = sin(angle * PI / 180f).toFloat()
        val midY = (newPos.y + start_cur_pos_pc.y) / 2f

        val (normAB, length) = (e - s).let { (it / it.length()) to it.length() }
        val factor = flerp(FloatRange(0f, 100f), FloatRange(0.2f, 0.4f), midY)
        val a = s + normAB * length * factor * 0.5f
        val b = e - normAB * length * factor * 0.5f
        val newLength = (b - a).length()

        var stroke =
            flerp(FloatRange(0f, 100f), FloatRange(1f / angleSin, 2.5f / angleSin), midY) *
                strokeWidth

        if (abs(start.y - end.y) < 0.10f) {
            val y = min(canvas_size.height - stroke + strokeWidth / 2f, s.y - strokeWidth / 2f)
            drawScope.drawRect(
                color = color,
                style = Fill,
                topLeft = Offset(s.x - strokeWidth / 2f, y),
                size = Size(length + strokeWidth, stroke)
            )
        } else {
            if (newLength < stroke) {
                stroke *= 0.1f
            }
            drawScope.drawLine(
                color = color,
                Offset(a.x, a.y),
                Offset(b.x, b.y),
                stroke,
                cap = StrokeCap.Square
            )
        }
    } else {
        drawScope.drawLine(
            color = color,
            Offset(start.x, start.y),
            Offset(end.x, end.y),
            strokeWidth,
            cap = StrokeCap.Square
        )
    }
}

private operator fun PointF.times(k: Float): PointF = PointF(x * k, y * k)

private operator fun PointF.div(length: Float) = PointF(x / length, y / length)

fun pcToPoint(pcPoint: Point, canvas_size: Size) =
    PointF(
        canvas_size.width * pcPoint.x.toFloat() / 100f,
        canvas_size.height * (100 - pcPoint.y).toFloat() / 100f
    )

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Letter(
    modifier: Modifier,
    letter: Char = 'A',
    backgroundColor: Color,
    textColor: Color,
    padding: Float,
    strokeWidth: Float = 5f,
    angle: Float = 80f,
    adaptiveStrokes: Boolean = true
) {
    val codon = FontData.lettersCodonTable[letter]
    if (codon == null) {
        println("[error] unsupported character `$letter`, can't display it out.")
    } else {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(start = padding.dp, end = padding.dp)
        ) {
            Codon2Letter(
                modifier.align(Alignment.Center).background(backgroundColor),
                codon,
                textColor,
                backgroundColor,
                strokeWidth,
                angle,
                adaptiveStrokes
            )
        }
    }
}
