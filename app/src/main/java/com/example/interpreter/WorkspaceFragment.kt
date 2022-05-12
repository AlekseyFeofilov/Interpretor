package com.example.interpreter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.BlockView
import com.example.interpreter.customView.DrawView
import com.example.interpreter.customView.InputView
import com.example.interpreter.customView.OutputView
import com.example.interpreter.customView.blocks.BlockWhile
import com.example.interpreter.databinding.*
import com.example.interpreter.vm.instruction.Bool


var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)
data class Wire(var startBlockId: Int, var outputPoint: Point, var inputPoint: Point)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private var touchPoint = Point(0f, 0f)
    
    private var isPanelMoving = false
    private var isMovingScreenOn = true
    
    private lateinit var console: ConstraintLayout
    private lateinit var blocksPanel: ConstraintLayout
    
    private lateinit var canvas: DrawView
    
    private lateinit var bindingWorkspace: FragmentWorkspaceBinding
    private lateinit var bindingConsole: FragmentRightPanelBinding
    private lateinit var bindingStack: StackForWorkspaceBinding
    private lateinit var bindingBlocksPanel: FragmentBlocksBinding
    private lateinit var bindingListOfBlocks: ListOfBlocksBinding
    private lateinit var bindingScrollBox: ScrollBoxBinding
    
    private val listOfBlocks = mutableListOf<View>()
    private val listOfWires  = mutableListOf<Wire>()
    
    private var location = Point(0f, 0f)
    private lateinit var draggingView: View
    
    private lateinit var myContext: Context
    
    @SuppressLint("ClickableViewAccessibility", "UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        myContext = context!!
        
        bindingWorkspace = FragmentWorkspaceBinding.bind(view)
        bindingConsole = FragmentRightPanelBinding.bind(view)
        bindingBlocksPanel = FragmentBlocksBinding.bind(view)
        bindingStack = StackForWorkspaceBinding.bind(view)
        bindingListOfBlocks = ListOfBlocksBinding.bind(view)
        bindingScrollBox = ScrollBoxBinding.bind(view)
        
        isConsoleHidden = true
        isBlocksPanelHidden = true
        
        // set on click listener for button that call panel with blocks
        bindingBlocksPanel.closeButton.setOnClickListener { moveBlocksFragment(300) }
        bindingBlocksPanel.blocksButton.setOnClickListener {
            if (isConsoleHidden) {
                moveBlocksFragment(400)
            }
        }
        
        // put panel with blocks down screen
        blocksPanel = bindingBlocksPanel.blocksContainer
        blocksPanel.y = metrics.bounds.height().toFloat()
        // set touch listener for button that pull console
        console = bindingConsole.console
        //bindingConsole.buttonPanel.setOnTouchListener(consoleMoving())
        
        // show or hide button that call or close console
        //if (isButtonForConsoleVisibility) {
            bindingConsole.consoleButton.setOnClickListener {
                if (isBlocksPanelHidden) {
                    isPanelMoving = true
                    isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
                        takeConsole( 200)
                        false
                    } else {
                        hideConsole(bindingConsole.buttonPanel, 200)
                        true
                    }
                    isPanelMoving = false
                }
            }
        //} else {
        //    bindingConsole.consoleButton.visibility = View.INVISIBLE
        //}
        
        // set on click listener for button that add block from panel to stack
        for (i in 0 until bindingListOfBlocks.listOfBlocks.childCount) {
            bindingListOfBlocks.listOfBlocks.getChildAt(i).setOnClickListener { button ->
                addBlockToStack(createBlockByClickedButton(button as Button))
            }
        }
        
        
        // turn on drag-n-drop
        bindingScrollBox.scrollBox.setOnDragListener(choiceDragListener())
        bindingStack.stackContainer.setOnDragListener(choiceDragListener())
        bindingStack.basketContainer.setOnDragListener(choiceDragListener())
    
        //canvas = DrawView(activity)
        //addCanvas(canvas)
        
        var newBlock = BlockWhile(myContext)
        newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
        newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
        bindingScrollBox.scrollBox.addView(newBlock)
        newBlock.setOnLongClickListener(choiceLongClickListener())
        listOfBlocks.add(newBlock)
    
        newBlock = BlockWhile(myContext)
        newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
        newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
        bindingScrollBox.scrollBox.addView(newBlock)
        newBlock.setOnLongClickListener(choiceLongClickListener())
        listOfBlocks.add(newBlock)
    }
    
    // generate and put in stack blocks
    @SuppressLint("UseRequireInsteadOfGet", "ClickableViewAccessibility")
    private fun createBlockByClickedButton(button: Button): BlockView =
        //TODO: change body of "when" that it creates blocks not stubs
        when (button) {
            bindingListOfBlocks.VARIABLE -> {
                //TODO: add new function whose will creates "views"
                val newBlock = BlockWhile(myContext)
                newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                bindingStack.stackContainer.addView(newBlock)
                newBlock.setOnLongClickListener(choiceLongClickListener())
                listOfBlocks.add(newBlock)
                newBlock
            }
            bindingListOfBlocks.IF -> {
                val newBlock = BlockWhile(myContext)
                newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                bindingStack.stackContainer.addView(newBlock)
                newBlock.setOnLongClickListener(choiceLongClickListener())
                listOfBlocks.add(newBlock)
                newBlock
            }
            bindingListOfBlocks.WHILE -> {
                val newBlock = BlockWhile(myContext)
                newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                bindingStack.stackContainer.addView(newBlock)
                newBlock.setOnLongClickListener(choiceLongClickListener())
                listOfBlocks.add(newBlock)
                newBlock
            }
            bindingListOfBlocks.MATH -> {
                val newBlock = BlockWhile(myContext)
                newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                bindingStack.stackContainer.addView(newBlock)
                newBlock.setOnLongClickListener(choiceLongClickListener())
                listOfBlocks.add(newBlock)
                newBlock
            }
            else -> {
                val newBlock = BlockWhile(myContext)
                newBlock.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                newBlock.findViewById<RadioButton>(R.id.inputRadioButton).setOnTouchListener(onTouchBlocksPoint())
                bindingStack.stackContainer.addView(newBlock)
                newBlock.setOnLongClickListener(choiceLongClickListener())
                listOfBlocks.add(newBlock)
                newBlock
            }
        }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun addBlockToStack(view: BlockView) {
//        (view as BlockView).binding.listOfInputLinearLayout.children.forEach {
//            it.setOnTouchListener(onTouchBlocksPoint())
//        }
        //view.findViewById<RadioButton>(R.id.outputRadioButton)
        //    .setOnTouchListener(onTouchBlocksPoint())
        //view.findViewById<RadioButton>(R.id.inputRadioButton)
        //    .setOnTouchListener(onTouchBlocksPoint())
//        bindingStack.stackContainer.addView(view)
//        view.x += 20
//        view.y += 20
//        view.setOnLongClickListener(choiceLongClickListener())
        //listOfBlocks.add(view)
        //view.translationZ = 30f
    }
    
    
    // add canvas for draw wires
//    private fun addCanvas(canvas: DrawView) {
//        val density = this.resources.displayMetrics.density
//        val params =
//            ConstraintLayout.LayoutParams((5000 * density).toInt(), (5000 * density).toInt())
//        bindingScrollBox.scrollBox.addView(canvas, params)
//        canvas.translationZ = 10000f
//    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchBlocksPoint() = OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(view is RadioButton) { // TODO: change for IO view
                    touchPoint = getCoordinatesOfIOPoint(view)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                canvas.draw(listOfWires, Wire(
                    0,
                    Point(touchPoint.x + view.width/2, touchPoint.y + view.height/2),
                    Point(event.x + touchPoint.x, event.y + touchPoint.y))
                )
            }
            MotionEvent.ACTION_UP -> {
                if(view is RadioButton) {
                    val dropView = findIOViewWhereMovingEnded(
                        Point(
                        event.x + touchPoint.x,
                        event.y + touchPoint.y
                        ))
                    if(dropView != null) {
                        listOfWires.add(Wire(
                            0,
                            Point(touchPoint.x + view.width/2, touchPoint.y + view.height/2),
                            Point(
                                getCoordinatesOfIOPoint(dropView).x + dropView.width/2,
                                getCoordinatesOfIOPoint(dropView).y + dropView.height/2
                            )))
                    }
                    canvas.draw(listOfWires)
                }
            }
        }
        true
    }
    
    private fun findIOViewWhereMovingEnded(point: Point): View? {
        for(block in listOfBlocks) {
            if(isInView(point, block.findViewById(R.id.inputRadioButton))) {
                return block.findViewById(R.id.inputRadioButton)
            }
        }
        return null
    }
    
    private fun isInView(point: Point, view: View): Boolean {
        val block = getCoordinatesOfIOPoint(view)
        return (block.x <= point.x && point.x <= block.x + view.width &&
                block.y <= point.y && point.y <= block.y + view.height)
    }
    
    private fun getCoordinatesOfIOPoint(view: View): Point {
        var x = view.x
        x+=(view.parent as View).x
        x += ((view.parent).parent as View).x
        x += (((view.parent).parent).parent as View).x
    
        var y = view.y
        y+=(view.parent as View).y
        y += ((view.parent).parent as View).y
        y += (((view.parent).parent).parent as View).y
        return Point(x , y)
    }
    
    // drag-n-drop for blocks
    @SuppressLint("ClickableViewAccessibility")
    private fun choiceLongClickListener() = OnLongClickListener { view ->
        val data = ClipData.newPlainText("", "")
        val shadowBuilder = DragShadowBuilder(view)
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            view.startDrag(data, shadowBuilder, view, 0)
        } else {
            view.startDragAndDrop(data, shadowBuilder, view, 0)
        }
        draggingView = view as View
        Log.i("hello", "${draggingView}")
        true
    }
    
    //private val deltaLocation = Point(0f, 0f)
    private fun choiceDragListener() = OnDragListener { view, event ->
        when (event.action) {
            //TODO: this fun is need refactoring
            DragEvent.ACTION_DRAG_STARTED -> {
                //deltaLocation.x = draggingView.x
                //deltaLocation.y = draggingView.y
                //draggingView.visibility = INVISIBLE
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                if (view == bindingScrollBox.scrollBox) {
                    location.x = event.x
                    location.y = event.y
                }
            }
            DragEvent.ACTION_DROP -> {
                when (view) {
                    draggingView.parent -> {
                        when (view) {
                            bindingStack.stackContainer -> {
                                draggingView.x = 20f
                                draggingView.y = 20f
                            }
                            bindingScrollBox.scrollBox -> {
                                draggingView.translationZ = 30f
                                draggingView.x = location.x - draggingView.width / 2
                                draggingView.y = location.y - draggingView.height / 2
                            }
                        }
                    }
                    bindingStack.basketContainer -> {
                        when(draggingView.parent) {
                            bindingScrollBox.scrollBox -> {
                                bindingScrollBox.scrollBox.removeView(draggingView)
                            }
                            bindingStack.stackContainer-> {
                                bindingStack.stackContainer.removeView(draggingView)
                            }
                        }
                    }
                    bindingStack.stackContainer -> {
                        bindingScrollBox.scrollBox.removeView(draggingView)
                        bindingStack.stackContainer.addView(draggingView)
                        draggingView.x = 20f
                        draggingView.y = 20f
                    }
                    bindingScrollBox.scrollBox -> {
                        bindingStack.stackContainer.removeView(draggingView)
                        bindingScrollBox.scrollBox.addView(draggingView)
                        draggingView.translationZ = 30f
                        draggingView.x = location.x - draggingView.width / 2
                        draggingView.y = location.y - draggingView.height / 2
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                //draggingView.visibility = VISIBLE
            }
        }
        true
    }

    // animation for ui: console and down panel
//    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
//    private fun consoleMoving() = View.OnTouchListener { view, event ->
//        if (isBlocksPanelHidden && !isPanelMoving) {
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    touchPoint.x = event.x
//                    false
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    if (console.x + event.x - touchPoint.x > 0.1 * metrics.bounds.width()) {
//                        console.x += event.x - touchPoint.x
//                    }
//                    false
//                }
//                else -> {
//                    isPanelMoving = true
//                    isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
//                        hideConsole(view, 200)
//                        true
//                    } else {
//                        takeConsole(200)
//                        false
//                    }
//                    isPanelMoving = false
//                    true
//                }
//            }
//        } else true
//    }
    
    private fun moveBlocksFragment(time: Long) {
        isMovingScreenOn = true
        isBlocksPanelHidden = if (isBlocksPanelHidden) {
            val from = Point(blocksPanel.x, blocksPanel.y)
            val to = Point(blocksPanel.x, 0.03f * metrics.bounds.height())
            moveContainer(from, to, time, blocksPanel)
            false
        } else {
            val from = Point(blocksPanel.x, blocksPanel.y)
            val to = Point(blocksPanel.x, metrics.bounds.height().toFloat())
            moveContainer(from, to, time, blocksPanel)
            true
        }
        isMovingScreenOn = false
    }
    
    private fun takeConsole(time: Long) {
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
                //radius vector of moving by one fps
                val deltaVector = Point(
                    container.x + (to.x - from.x) / (time * 60f / 1000f),
                    container.y + (to.y - from.y) / (time * 60f / 1000f)
                )
                //radius vector of moving
                val direction = Point(to.x - from.x, to.y - from.y)
                
                if (deltaVector.x * direction.x <= to.x * direction.x) {
                    container.x += (to.x - from.x) / (time * 60f / 1000f)
                }
                if (deltaVector.y * direction.y <= to.y * direction.y) {
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