package org.scotthamilton.trollslate.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.minus
import kotlin.math.*
import org.scotthamilton.trollslate.data.FontData
import org.scotthamilton.trollslate.data.HLine
import org.scotthamilton.trollslate.data.VLine
import java.io.OutputStream

enum class RangeType {
    INDIVIDUALS,
    GROUP
}

data class DrawData(
    val letter_size: Size,
    val offset: Offset = Offset(0f, 0f),
    val textColor: Color,
    val strokeWidth: Float,
    val angle: Float,
    val adaptiveStrokes: Boolean,
    val drawable: Any
)

data class TypedRange(val range: IntRange, val type: RangeType) {
    override fun toString() = (if (type == RangeType.INDIVIDUALS) "I" else "G") + "(${range})"
}

private fun indivRange(start: Int, end: Int) =
    TypedRange(IntRange(start, end), RangeType.INDIVIDUALS)

private fun groupRange(start: Int, end: Int) = TypedRange(IntRange(start, end), RangeType.GROUP)

private fun parseCodon(codon: String): Pair<List<TypedRange>, Point>? {
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

@RequiresApi(Build.VERSION_CODES.N)
fun drawCodonLetter(
    codon: String,
    drawData: DrawData,
) {
    val parsedCodon = parseCodon(codon)
    if (parsedCodon == null) {
        return
    } else {
        val (sortedRanges, firstPos) = parsedCodon
        var curPosPc = firstPos
        sortedRanges.forEach {
            curPosPc =
                if (it.type == RangeType.INDIVIDUALS) {
                    drawIndivCode(
                        codon.substring(it.range),
                        curPosPc,
                        drawData
                    )
                } else {
                    drawGroupCode(
                        codon.substring(it.range),
                        curPosPc,
                        drawData
                    )
                }
        }
    }
}

fun codonLetterToPath(codon: String, canvas_size: Size): Path {
    val parsedCodon = parseCodon(codon)
    return if (parsedCodon == null) {
        Path()
    } else {
        val (sortedRanges, firstPos) = parsedCodon
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
        path
    }
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
        modifier = modifier.background(backgroundColor),
        onDraw = {
            drawRect(color = backgroundColor, topLeft = Offset.Zero, size = size, style = Fill)
            drawCodonLetter(codon,
                DrawData(
                    letter_size = size,
                    textColor = textColor,
                    strokeWidth = strokeWidth,
                    angle = angle,
                    adaptiveStrokes = adaptiveStrokes,
                    drawable = this
                )
            )
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
    drawData: DrawData
): Point {
    var curPosPc = start_cur_pos_pc
    code.forEach {
        indivCode2Point(it, curPosPc)?.let { new_pos ->
            drawPcLine(
                curPosPc,
                new_pos,
                drawData
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
    drawData: DrawData
): Point {
    val newPos = groupCode2Point(code, start_cur_pos_pc)
    if (!(code.length >= 2 && code[1] == '_')) {
        drawPcLine(
            start_cur_pos_pc,
            newPos,
            drawData
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
    drawData: DrawData
) {
    val start = pcToPoint(start_cur_pos_pc, drawData.letter_size)
    val end = pcToPoint(newPos, drawData.letter_size)
    if (drawData.adaptiveStrokes && abs(start.x - end.x) > 0.10f) {
        val (s, e) = if (start.x > end.x) end to start else start to end
        val angleSin = sin(drawData.angle * PI / 180f).toFloat()
        val midY = (newPos.y + start_cur_pos_pc.y) / 2f

        val length = (e - s).length()

        val stroke =
            flerp(FloatRange(0f, 100f), FloatRange(1f / angleSin, 2.5f / angleSin), midY) *
                    drawData.strokeWidth

        val y = min(drawData.letter_size.height - stroke + drawData.strokeWidth / 2f, s.y - drawData.strokeWidth / 2f)
        drawData.drawable.drawRect(
            color = drawData.textColor,
            topLeft = Offset(s.x - drawData.strokeWidth / 2f, y)+drawData.offset,
            size = Size(length + drawData.strokeWidth, stroke)
        )
    } else {
        drawData.drawable.drawLine(
            color = drawData.textColor,
            drawData.strokeWidth,
            Offset(start.x, start.y)+drawData.offset,
            Offset(end.x, end.y)+drawData.offset
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
    strokeWidth: Float = 5f,
    angle: Float = 80f,
    adaptiveStrokes: Boolean = true
) {
    val codon = FontData.lettersCodonTable[letter]
    if (codon == null) {
        println("[error] unsupported character `$letter`, can't display it out.")
    } else {
        Codon2Letter(
            modifier.background(backgroundColor),
            codon,
            textColor,
            backgroundColor,
            strokeWidth,
            angle,
            adaptiveStrokes
        )
    }
}

private fun Any.drawLine(color: Color, strokeWidth: Float, start: Offset, end: Offset) {
    when (this) {
        is DrawScope -> {
            this.drawLine(
                color = color,
                start,
                end,
                strokeWidth,
                cap = StrokeCap.Square
            )
        }
        is Canvas -> {
            this.drawLine(
                start.x, start.y,
                end.x, end.y,
                Paint().apply {
                    setColor(color.toArgb())
                    style = Paint.Style.STROKE
                    setStrokeWidth(strokeWidth)
                    strokeCap = Paint.Cap.SQUARE
                }
            )
        }
    }
}

private fun Any.drawRect(color: Color, topLeft: Offset, size: Size) {
    when (this) {
        is DrawScope -> {
            this.drawRect(
                color = color,
                style = Fill,
                topLeft = topLeft,
                size = size
            )
        }
        is Canvas -> {
            this.drawRect(
                topLeft.x, topLeft.y,
                topLeft.x + size.width, topLeft.y + size.height,
                Paint().apply {
                    setColor(color.toArgb())
                    style = Paint.Style.FILL
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun drawLettersToPdf(
    letters: String,
    angle: Float = 80f,
    strokeWidth: Float = 5f,
    letter_size: Size,
    adaptiveStrokes: Boolean = true,
    out: OutputStream
) {
    val padding = letter_size.height*0.1f
    val widthSpace = letter_size.width*0.04f
    val canvasSize = Size(
        (letter_size.width+widthSpace)*letters.length.toFloat()+padding*2f,
        letter_size.height+padding*2f
    )
    if (letters.isEmpty()) {
        println("[error] no letters to write into the pdf: `$letters`.")
    } else {
        val doc = PdfDocument()
        val page = doc.startPage(
            PdfDocument.PageInfo.Builder(
                canvasSize.width.roundCeil(), canvasSize.height.roundCeil(), 1
            ).create()
        )
        letters.forEachIndexed { index, letter ->
            FontData.lettersCodonTable[letter]?.let {
                drawCodonLetter(
                    it,
                    DrawData(
                        letter_size = letter_size,
                        offset = Offset(
                            padding+index*(widthSpace + letter_size.width), padding
                        ),
                        textColor = Color.Black,
                        strokeWidth = strokeWidth,
                        angle = angle,
                        adaptiveStrokes = adaptiveStrokes,
                        drawable = page.canvas
                    )
                )
            }
        }
        doc.finishPage(page)
        doc.writeTo(out)
        doc.close()
    }
}

private fun Float.roundCeil(): Int = ceil(this).toInt()
