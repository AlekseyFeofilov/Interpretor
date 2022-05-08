package com.example.interpreter

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Bundle
import android.os.CountDownTimer
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.interpreter.databinding.*


var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private val touchPoint = Point(0f, 0f)
    
    private var isPanelMoving = false
    private var isMovingScreenOn = true
    
    private lateinit var console: ConstraintLayout
    private lateinit var blocksPanel: ConstraintLayout
    
    private lateinit var bindingWorkspace: FragmentWorkspaceBinding
    private lateinit var bindingConsole: FragmentRightPanelBinding
    private lateinit var bindingStack: StackForWorkspaceBinding
    private lateinit var bindingBlocksPanel: FragmentBlocksBinding
    private lateinit var bindingListOfBlocks: ListOfBlocksBinding
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bindingWorkspace = FragmentWorkspaceBinding.bind(view)
        bindingConsole = FragmentRightPanelBinding.bind(view)
        bindingBlocksPanel = FragmentBlocksBinding.bind(view)
        bindingStack = StackForWorkspaceBinding.bind(view)
        bindingListOfBlocks = ListOfBlocksBinding.bind(view)
        
        isConsoleHidden = true
        isBlocksPanelHidden = true
        
        // set on click listener for button that call panel with blocks
        bindingBlocksPanel.closeButton.setOnClickListener { moveBlocksFragment(300) }
        bindingBlocksPanel.blocksButton.setOnClickListener {
            if (isBlocksPanelHidden && isConsoleHidden) {
                moveBlocksFragment(400)
            }
        }
        
        // put panel with blocks down screen
        blocksPanel = bindingBlocksPanel.blocksContainer
        blocksPanel.y = metrics.bounds.height().toFloat()
        // set touch listener for button that pull console
        console = bindingConsole.console
        bindingConsole.buttonPanel.setOnTouchListener(consoleMoving())
        
        // show or hide button that call or close console
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
        
        // set on click listener for button that add block from panel to stack
        for (i in 0 until bindingListOfBlocks.listOfBlocks.childCount) {
            bindingListOfBlocks.listOfBlocks.getChildAt(i).setOnClickListener { button ->
                addBlockToStack(createBlockByClickedButton(button as Button))
                //stackOfBlocks[stackOfBlocks.size - 1].setOnDragListener()
            }
        }
        
        
        // turn on drag-n-drop
        bindingWorkspace.scrollBox.setOnDragListener(choiceDragListener())
        bindingStack.stackContainer.setOnDragListener(choiceDragListener())
    }
    
    private fun createBlockByClickedButton(button: Button): View =
        when (button) {
            bindingListOfBlocks.VARIABLE -> {
                val newButton = Button(context)
                newButton.background = resources.getDrawable(R.drawable.home_buttons)
                newButton.text = "VAR"
                newButton
            }
            bindingListOfBlocks.IF -> {
                val newButton = Button(context)
                newButton.background = resources.getDrawable(R.drawable.home_buttons)
                newButton.text = "IF"
                newButton
            }
            bindingListOfBlocks.WHILE -> {
                val newButton = Button(context)
                newButton.background = resources.getDrawable(R.drawable.home_buttons)
                newButton.text = "WHILE"
                newButton
            }
            bindingListOfBlocks.MATH -> {
                val newButton = Button(context)
                newButton.background = resources.getDrawable(R.drawable.home_buttons)
                newButton.text = "MATH"
                newButton
            }
            else -> {
                val newButton = Button(context)
                newButton.background = resources.getDrawable(R.drawable.home_buttons)
                newButton.text = "aboba"
                newButton
            }
        }
    
    private fun addBlockToStack(view: View) {
        val params = ConstraintLayout.LayoutParams(
            bindingStack.stackContainer.width - 40,
            bindingStack.stackContainer.height - 40
        )
        bindingStack.stackContainer.addView(view, params)
        //bindingWorkspace.scrollBox.addView(view, params)
        view.x += 20
        view.y += 20
        view.setOnTouchListener(choiceTouchListener())
        view.translationZ = 30f
        //view.setOnDragListener(choiceDragListener())
    }
    
    // drag blocks
    @SuppressLint("ClickableViewAccessibility")
    private fun choiceTouchListener() = View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = DragShadowBuilder(view)
            view.startDrag(data, shadowBuilder, view, 0)
            draggingView = view as Button
            true
        } else {
            false
        }
    }
    
    //private var point = Point(0f, 0f)
    private var location = Point(0f, 0f)
    private lateinit var currentParent: FrameLayout
    private lateinit var draggingView: Button
    
    private fun choiceDragListener() = OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                //draggingView.visibility = INVISIBLE
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                if (view == bindingWorkspace.scrollBox) {
                    location.x = event.x
                    location.y = event.y
                }
            }
            DragEvent.ACTION_DROP -> {
                when (view) {
                    draggingView.parent -> {
                        draggingView.x = location.x - draggingView.width / 2
                        draggingView.y = location.y - draggingView.height / 2
                        draggingView.translationZ = 30f
                    }
                    bindingStack.stackContainer -> {
                        bindingWorkspace.scrollBox.removeView(draggingView)
                        bindingStack.stackContainer.addView(draggingView)
                        draggingView.x = 20f
                        draggingView.y = 20f
                    }
                    bindingWorkspace.scrollBox -> {
                        bindingStack.stackContainer.removeView(draggingView)
                        bindingWorkspace.scrollBox.addView(draggingView)
                        draggingView.translationZ = 30f
                        draggingView.x = location.x - draggingView.width / 2
                        draggingView.y = location.y - draggingView.height / 2
                    }
                }
                //draggingView.visibility = VISIBLE
            }
        }
        true
    }
    
    // animation for ui: console and down panel
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun consoleMoving() = View.OnTouchListener { view, event ->
        if (isBlocksPanelHidden && !isPanelMoving) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchPoint.x = event.x
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (console.x + event.x - touchPoint.x > 0.1 * metrics.bounds.width()) {
                        console.x += event.x - touchPoint.x
                    }
                    false
                }
                else -> {
                    isPanelMoving = true
                    isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
                        hideConsole(view, 200)
                        true
                    } else {
                        takeConsole(view, 200)
                        false
                    }
                    isPanelMoving = false
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
                val newPoint = Point(
                    container.x + (to.x - from.x) / (time * 60f / 1000f),
                    container.y + (to.y - from.y) / (time * 60f / 1000f)
                )
                val direction = Point(to.x - from.x, to.y - from.y)
                if (newPoint.x * direction.x <= to.x * direction.x) {
                    container.x += (to.x - from.x) / (time * 60f / 1000f)
                }
                if (newPoint.y * direction.y <= to.y * direction.y) {
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