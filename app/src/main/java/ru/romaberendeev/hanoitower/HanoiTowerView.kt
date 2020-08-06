package ru.romaberendeev.hanoitower

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.util.LinkedList
import kotlin.random.Random

class HanoiTowerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val columnPaint = Paint()
    private val ringsPaint = Paint()
    private val firstColumnRect = Rect()
    private val secondColumnRect = Rect()
    private val thirdColumnRect = Rect()
    private val baseRect = Rect()
    private val columnWidth = dpToPx(8).toInt()
    private val rings: MutableList<LinkedList<Ring>> = mutableListOf()
    private val columns: List<Rect> = listOf(firstColumnRect, secondColumnRect, thirdColumnRect)
    private val random = Random(System.currentTimeMillis())

    private var baseTop: Int = 0
    private var ringHeight = dpToPx(8)
    private var maxRingWidth = 0
    private var maxRing = 4

    init {
        columnPaint.color = Color.GREEN
        ringsPaint.color = Color.RED
    }

    fun init(ringCount: Int) {
        maxRing = ringCount
        rings.clear()
        rings.add(LinkedList())
        rings.add(LinkedList())
        rings.add(LinkedList())
        (ringCount downTo 1).forEach { ring ->
            rings[2].add(Ring(ring, getRandomColor()))
        }
        invalidate()
    }

    fun moveRing(from: Int, to: Int) {
        val ring = rings[from].pollLast() ?: return
        val topRing = rings[to].peekLast()
        if (topRing != null && topRing.width < ring.width) {
            rings[from].add(ring)
            return
        }
        rings[to].add(ring)
        invalidate()
    }

    fun isSolved(): Boolean {
        return rings[1].isEmpty() && rings[2].isEmpty()
    }

    fun ringsCountOnColumn(column: Int): Int {
        return rings[column].count()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        baseTop = h - dpToPx(16).toInt()
        baseRect.set(0,baseTop, w, h)
        firstColumnRect .set(1 * w / 6 - columnWidth / 2, 0, 1 * w / 6 + columnWidth / 2, baseTop)
        secondColumnRect.set(3 * w / 6 - columnWidth / 2, 0, 3 * w / 6 + columnWidth / 2, baseTop)
        thirdColumnRect .set(5 * w / 6 - columnWidth / 2, 0, 5 * w / 6 + columnWidth / 2, baseTop)
        maxRingWidth = w / 7 - columnWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(baseRect, columnPaint)
        canvas.drawRect(firstColumnRect, columnPaint)
        canvas.drawRect(secondColumnRect, columnPaint)
        canvas.drawRect(thirdColumnRect, columnPaint)

        rings.forEachIndexed { columnIndex, ringsOnColumn ->
            ringsOnColumn.forEachIndexed { ringIndex, ring ->
                val columnRect = columns[columnIndex]
                val ringWidth = ring.width.toFloat() / maxRing * maxRingWidth
                val left = columnRect.left - ringWidth
                val right = columnRect.right + ringWidth
                val bottom = baseTop - ringHeight * ringIndex
                val top = bottom - ringHeight
                ringsPaint.color = ring.color
                canvas.drawRoundRect(left, top, right, bottom, ringHeight / 2f, ringHeight/2f, ringsPaint)
            }
        }
        // rings[2].forEachIndexed { index, ring ->
        //     val columnRect = columns[2]
        //     val ringWidth = ring.width.toFloat() / maxRing * maxRingWidth
        //     val left = columnRect.left - ringWidth
        //     val right = columnRect.right + ringWidth
        //     val bottom = baseTop - ringHeight * index
        //     val top = bottom - ringHeight
        //     ringsPaint.color = ring.color
        //     canvas.drawRoundRect(left, top, right, bottom, ringHeight / 2f, ringHeight/2f, ringsPaint)
        // }
    }

    private fun dpToPx(dp: Int): Float {
        return context.resources.displayMetrics.density * dp
    }

    private fun getRandomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    private data class Ring(
        val width: Int,
        val color: Int
    )
}