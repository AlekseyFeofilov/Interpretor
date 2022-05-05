package com.example.interpreter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

var isConsoleHidden = true

data class Point(var x:Float, var y:Float)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private val touchPoint = Point(0f, 0f)
    private var isPanelMoving = false
    private lateinit var console: FrameLayout
    private val fragmentPalette = BlocksFragment()
    
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        createFragment(fragmentPalette, R.id.container)
        hideFragment(fragmentPalette)
        isConsoleHidden = true
        
        val button = view.findViewById<Button>(R.id.blocksButton)
        button.setOnClickListener {
            if (fragmentPalette.isHidden && isConsoleHidden) {
                takeFragmentFromBottom(fragmentPalette)
            }
        }
    
        console = view.findViewById(R.id.console)
        val button2 = view.findViewById<Button>(R.id.button_panel)
        button2.setOnTouchListener(getDragNDrop())
    
        val consoleButton = view.findViewById<Button>(R.id.consoleButton)
        if(isButtonForConsoleVisibility) {
            consoleButton.setOnClickListener {
                if(fragmentPalette.isHidden) {
                    isPanelMoving = true
                    isConsoleHidden = if (console.x < 0.6 * metrics.bounds.width()) {
                        moveContainer(console.x, metrics.bounds.width() - button2.width.toFloat(), console)
                        true
                    } else {
                        moveContainer(console.x, 0.2f * metrics.bounds.width(), console)
                        false
                    }
                }
            }
        }
        else {
            consoleButton.visibility = View.INVISIBLE
        }
    }
    
    private fun createFragment(fragment: Fragment, containerId: Int) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(containerId, fragment).commit()
    }
    private fun hideFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.hide(fragment).commit()
    }
    
    private fun takeFragmentFromBottom(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom)
        fragment.onCreateAnimation(0, true, 1)
        transaction.addToBackStack(null)
        transaction.show(fragment).commit()
    }
    
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun getDragNDrop() = View.OnTouchListener { view, event ->
        if(fragmentPalette.isHidden) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isPanelMoving) {
                        touchPoint.x = event.x
                        //touchPoint.y = event.y
                    }
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isPanelMoving) {
                        if (console.x + event.x - touchPoint.x > 0.1 * metrics.bounds.width()) {
                            console.x += event.x - touchPoint.x
                            //view.x += event.x - touchPoint.x
                            //view.y += event.y - touchPoint.y
                        }
                    }
                    false
                }
                else -> {
                    if (!isPanelMoving) {
                        isPanelMoving = true
                        isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
                            moveContainer(console.x, metrics.bounds.width() - view.width.toFloat(), console)
                            true
                        } else {
                            moveContainer(console.x, 0.2f * metrics.bounds.width(), console)
                            false
                        }
                    }
                    true
                }
            }
        }
        else true
    }
    
    private fun moveContainer(from:Float, to:Float, container:FrameLayout) {
        var count = 0
        object : CountDownTimer(250, 1) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                if(count<15) {
                    container.x += (to - from) / 15
                }
            }
            override fun onFinish() {
                container.x = to
                isPanelMoving = false
            }
        }.start()
    }
}