package com.example.interpreter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.interpreter.databinding.FragmentBlocksBinding
import com.example.interpreter.databinding.FragmentRightPanelBinding
import com.example.interpreter.databinding.FragmentWorkspaceBinding
import com.example.interpreter.vm.instruction.Bool

var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private val touchPoint = Point(0f, 0f)
    private var isPanelMoving = false
    private lateinit var console: ConstraintLayout
    private lateinit var blocksPanel: ConstraintLayout
    private lateinit var bindingWorkspace: FragmentWorkspaceBinding
    private lateinit var bindingConsole: FragmentRightPanelBinding
    private lateinit var bindingBlocksPanel: FragmentBlocksBinding
    private val stackOfBlocks = mutableListOf<View>()
    
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingWorkspace = FragmentWorkspaceBinding.bind(view)
        bindingConsole = FragmentRightPanelBinding.bind(view)
        bindingBlocksPanel = FragmentBlocksBinding.bind(view)
        
        isConsoleHidden = true
        isBlocksPanelHidden = true
        
        bindingBlocksPanel.closeButton.setOnClickListener { moveBlocksFragment(300) }
        bindingBlocksPanel.blocksButton.setOnClickListener {
            if (isBlocksPanelHidden && isConsoleHidden) {
                moveBlocksFragment(400)
            }
        }
        
        blocksPanel = bindingBlocksPanel.blocksContainer
        blocksPanel.y = metrics.bounds.height().toFloat()
        console = bindingConsole.console
        bindingConsole.buttonPanel.setOnTouchListener(getDragNDrop())
        
        if (isButtonForConsoleVisibility) {
            bindingConsole.consoleButton.setOnClickListener {
                if (isBlocksPanelHidden) {
                    isPanelMoving = true
                    isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
                        takeConsole(bindingConsole.buttonPanel, 200)
                        false
                    } else {
                        hideConsole(bindingConsole.buttonPanel, 200)
                        true
                    }
                    isPanelMoving = false
                }
            }
        } else {
            bindingConsole.consoleButton.visibility = View.INVISIBLE
        }
        for (i in 0 until bindingBlocksPanel.listOfBlocks.childCount) {
//            bindingBlocksPanel.listOfBlocks.getChildAt(i).setOnClickListener { button ->
//
//            }
        }
    }
    
    private fun createBlockByClickedButton(button: Button): View =
        when (button) {
            bindingBlocksPanel.closeButton -> bindingBlocksPanel.panel
            else -> bindingBlocksPanel.panel
        }
    
    private fun addBlockToStack(view: View) {
        stackOfBlocks.add(view)
    }
    
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun getDragNDrop() = View.OnTouchListener { view, event ->
        if (isBlocksPanelHidden) {
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
                            hideConsole(view, 200)
                            true
                        } else {
                            takeConsole(view, 200)
                            false
                        }
                        isPanelMoving = false
                    }
                    true
                }
            }
        } else true
    }
    
    private fun moveBlocksFragment(time: Long) {
        isBlocksPanelHidden = if (isBlocksPanelHidden) {
            val from = Point(blocksPanel.x, blocksPanel.y)
            val to = Point(blocksPanel.x, 0.05f * metrics.bounds.height())
            moveContainer(from, to, time, blocksPanel)
            false
        } else {
            val from = Point(blocksPanel.x, blocksPanel.y)
            val to = Point(blocksPanel.x, metrics.bounds.height().toFloat())
            moveContainer(from, to, time, blocksPanel)
            true
        }
    }
    
    private fun takeConsole(view: View, time: Long) {
        val from = Point(console.x, console.y)
        val to = Point(0.2f * metrics.bounds.width(), console.y)
        moveContainer(from, to, time, console)
    }
    
    private fun hideConsole(view: View, time: Long) {
        val from = Point(console.x, console.y)
        val to = Point(metrics.bounds.width() - view.width.toFloat(), console.y)
        moveContainer(from, to, time, console)
    }
    
    private fun moveContainer(from: Point, to: Point, time: Long, container: ConstraintLayout) {
        object : CountDownTimer(time, 1) {
            override fun onTick(millisUntilFinished: Long) {
                val newPoint = Point(container.x + (to.x - from.x) / (time * 60f / 1000f),
                                     container.y + (to.y - from.y) / (time * 60f / 1000f))
                val direction = Point(to.x - from.x, to.y - from.y)
                if( newPoint.x * direction.x <= to.x * direction.x) {
                    container.x += (to.x - from.x) / (time * 60f / 1000f)
                }
                if( newPoint.y * direction.y <= to.y * direction.y) {
                    container.y += (to.y - from.y) / (time * 60f / 1000f)
                }
            }
            
            override fun onFinish() {
                container.x = to.x
                container.y = to.y
            }
        }.start()
    }
}