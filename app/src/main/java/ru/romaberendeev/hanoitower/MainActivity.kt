package ru.romaberendeev.hanoitower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var from = -1
    private val ringCount = 8
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        first.setOnClickListener { moveIfReady(0) }
        second.setOnClickListener { moveIfReady(1) }
        third.setOnClickListener { moveIfReady(2) }
        restart.setOnClickListener { solve() }
        solve()
    }

    fun solve() {
        tower.init(ringCount)
        val depth = tower.ringsCountOnColumn(2)
        thread {
            moveAll(from = 2, to = 0, swap = 1, depth = depth)
        }
    }

    private fun moveIfReady(number: Int) {
        if (from != -1) {
            tower.moveRing(from, number)
            from = -1
        } else {
            from = number
        }
    }

    private fun moveAll(from: Int, to: Int, swap: Int, depth: Int) {
        if (depth == 1) {
            moveRing(from, to)
            return
        }
        moveAll(from, swap, to, depth - 1)
        moveRing(from, to)
        moveAll(swap, to, from, depth - 1)
    }

    private fun moveRing(from: Int, to: Int) {
        runOnUiThread {
            tower.moveRing(from, to)
        }
        Thread.sleep(500)
    }
}
