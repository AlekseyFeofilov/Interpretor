package com.example.interpreter.customView

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class LockableScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ScrollView(context, attrs) {
    var scrollable = true
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?) =
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> scrollable && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    
    override fun onInterceptTouchEvent(ev: MotionEvent?) =
        scrollable && super.onInterceptTouchEvent(ev)
}