package com.example.interpreter

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.BlockView
import com.example.interpreter.customView.DrawView
import com.example.interpreter.customView.Line
import com.example.interpreter.customView.blocks.BlockWhile
import com.example.interpreter.databinding.*


var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)
data class Wire(var startBlockId: Int, var finishBlockInd: Int, var outputPoint: Point, var inputPoint: Point)

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
    
    private lateinit var draggingView: View
    
    private val listOfBlocks = mutableListOf<View>()
    private val listOfWires = mutableListOf<Wire>()
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bindingWorkspace = FragmentWorkspaceBinding.bind(view)
        bindingConsole = FragmentRightPanelBinding.bind(view)
        bindingBlocksPanel = FragmentBlocksBinding.bind(view)
        bindingStack = StackForWorkspaceBinding.bind(view)
        bindingListOfBlocks = ListOfBlocksBinding.bind(view)
        bindingScrollBox = ScrollBoxBinding.bind(view)
        
        isConsoleHidden = true
        isBlocksPanelHidden = true
        
        // set on click listener for button that call panel with blocks
        bindingBlocksPanel.closeButton.setOnClickListener { movePanelWithBlocks(300) }
        bindingBlocksPanel.blocksButton.setOnClickListener {
            if (isConsoleHidden) {
                movePanelWithBlocks(400)
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
                    takeConsole(200)
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
        
        canvas = DrawView(activity)
        addCanvas(canvas)
    }
    
    private fun addCanvas(canvas: DrawView) {
        val density = this.resources.displayMetrics.density
        val params =
            ConstraintLayout.LayoutParams((11000 * density).toInt(), (11000 * density).toInt())
        bindingScrollBox.scrollBox.addView(canvas, params)
        canvas.translationZ = 10000f
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchBlocksPoint() = OnTouchListener { view, event ->
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (view is RadioButton) { // TODO: change for IO view
                    touchPoint = getCoordinatesOfIOPointInScrollBox(view)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                canvas.draw(
                    listOfWires, Wire(
                        0, 0,
                        Point(touchPoint.x + view.width / 2, touchPoint.y + view.height / 2),
                        Point(event.x + touchPoint.x, event.y + touchPoint.y)
                    )
                )
            }
            MotionEvent.ACTION_UP -> {
                if (view is RadioButton) {
                    val dropView = findInputViewWhereMovingEnded(
                        Point(
                            event.x + touchPoint.x,
                            event.y + touchPoint.y
                        )
                    )
                    if (dropView != null) {
                    
                    }
                    canvas.draw(listOfWires)
                }
            }
        }
        true
    }
    
    private fun isCorrectWire(wire: Wire, startView: View, endView: View): Boolean {
        val start = findBlockIndByIOView(startView)
        val end = findBlockIndByIOView(endView)
        var isAdded = false
        var countWireForArrow = 0
        if (start != end) {
            val newWire = Wire(
                start, end,
                Point(
                    touchPoint.x + startView.width / 2,
                    touchPoint.y + startView.height / 2
                ),
                Point(
                    getCoordinatesOfIOPointInScrollBox(endView).x + endView.width / 2,
                    getCoordinatesOfIOPointInScrollBox(endView).y + endView.height / 2
                )
            )
            for (i in listOfWires) {
                if (i == newWire) {
                    isAdded = true
                    break
                }
            }
        }
        
        for (i in (view as BlockView).binding.listOfOutputLinearLayout.children) {
            if((i as BlockView).binding.outputRadioButton == startView) {
            
            }
            if((i as BlockView).binding.inputRadioButton == endView) {
            
            }
        }
        
        //if(!isAdded && isFirstWireForArrow)
        return false
    }
    
    
    private fun isArrowHaveOneWire(arrow: View): Boolean {
        for (i in listOfBlocks) {
            if ((listOfBlocks as BlockView).binding.inputRadioButton == arrow) {
                val coordinate = getCoordinatesOfIOPointInScrollBox(arrow)
                for (j in listOfWires) {
                    if (coordinate == j.inputPoint) {
                        return true
                    }
                }
            }
            if ((listOfBlocks as BlockView).binding.outputRadioButton == arrow) {
                val coordinate = getCoordinatesOfIOPointInScrollBox(arrow)
                for (j in listOfWires) {
                    if (coordinate == j.inputPoint) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    private fun findBlockIndByIOView(view: View): Int {
        for(id in 0 until listOfBlocks.size) {
            if(view == (listOfBlocks[id] as BlockView).binding.inputRadioButton ||
                    view == (listOfBlocks[id] as BlockView).binding.outputRadioButton) {
                return id
            }
            for (i in (listOfBlocks[id] as BlockView).binding.listOfOutputLinearLayout.children) {
                val button = ((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(0)
                if (button == view) {
                    return id
                }
            }
            for (i in (listOfBlocks[id] as BlockView).binding.listOfInputLinearLayout.children) {
                val button = ((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(0)
                if (button == view) {
                    return id
                }
            }
        }
        return 0
    }
    
    private fun findInputViewWhereMovingEnded(point: Point): View? {
        for(id in 0 until listOfBlocks.size) {
            if(isInView(point, (listOfBlocks[id] as BlockView).binding.inputRadioButton)) {
                return (listOfBlocks[id] as BlockView).binding.inputRadioButton
            }
            for (i in (listOfBlocks[id] as BlockView).binding.listOfInputLinearLayout.children) {
                val button = ((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(0)
                if (isInView(point, button)) {
                    return button
                }
            }
//            for (i in (listOfBlocks[id] as BlockView).binding.listOfInputLinearLayout.children) {
//                if (isInView(point, i)) {
//                    Log.i("hello", "${(i as LinearLayout).getChildAt(0)}")
//                    return i
//                }
//            }
        }
        return null
    }
    
    private fun isInView(point: Point, view: View): Boolean {
        val block = getCoordinatesOfIOPointInScrollBox(view)
        return (block.x <= point.x && point.x <= block.x + view.width &&
                block.y <= point.y && point.y <= block.y + view.height)
    }
    
    
    private fun getCoordinatesOfIOPointInScrollBox(view:View): Point {
        val point = Point(0f, 0f)
        var buffView = view
        while(buffView != bindingScrollBox.scrollBox) {
            point.x += buffView.x
            point.y += buffView.y
            buffView = (buffView.parent as View)
        }
        return point
    }
    
//    private fun getCoordinatesOfIOPoint(view: View): Point {
//        var x = view.x
//        x+=(view.parent as View).x
//        x += ((view.parent).parent as View).x
//        x += (((view.parent).parent).parent as View).x
//
//        var y = view.y
//        y+=(view.parent as View).y
//        y += ((view.parent).parent as View).y
//        y += (((view.parent).parent).parent as View).y
//        return Point(x , y)
//    }
    
    // generate and put in stack blocks
    @SuppressLint("UseRequireInsteadOfGet")
    private fun createBlockByClickedButton(button: Button): View =
        //TODO: change body of "when" that it creates blocks not stubs
        when (button) {
            bindingListOfBlocks.VARIABLE -> {
                //TODO: add new function whose will creates "views"
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
                var newBlock = BlockWhile(context!!)
                newBlock
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
    
    @SuppressLint("ClickableViewAccessibility")
    private fun addBlockToStack(view: View) {
        bindingStack.stack.addView(view)
        view.findViewById<RadioButton>(R.id.outputRadioButton).setOnTouchListener(onTouchBlocksPoint())
        //TODO: add listener for IO points
//        (view as BlockView).binding.listOfOutputLinearLayout.children.forEach {
//            it.setOnTouchListener(onTouchBlocksPoint())
//        }
        for (i in (view as BlockView).binding.listOfOutputLinearLayout.children) {
            val button = ((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1)
            button.setOnTouchListener(onTouchBlocksPoint())
        }
        //
        view.setOnLongClickListener(choiceLongClickListener())
        view.translationZ = 30f
        listOfBlocks.add(view)
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
        true
    }
    
    private var location = Point(0f, 0f)
    private var deltaLocation = Point(0f, 0f)
    private fun choiceDragListener() = OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                //draggingView.visibility = INVISIBLE
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                    location.x = event.x
                    location.y = event.y
            }
            DragEvent.ACTION_DROP -> {
                deltaLocation.x = location.x - draggingView.width/2  - draggingView.x
                deltaLocation.y = location.y - draggingView.height/2 - draggingView.y
                when (view) {
                    bindingStack.basketContainer -> {
                        removeWiresToBlock(draggingView)
                        correctIndexes(draggingView)
                        removeBlock(draggingView)
                        canvas.draw(listOfWires)
                    }
                    bindingStack.stackContainer -> {
                        moveToStack(draggingView)
                        removeWiresToBlock(draggingView)
                        canvas.draw(listOfWires)
                    }
                    bindingScrollBox.scrollBox -> {
                        moveToScrollBox(draggingView, location)
                        moveWiresToBlock(draggingView, deltaLocation)
                        canvas.draw(listOfWires)
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                //draggingView.visibility = VISIBLE
            }
        }
        true
    }
    
    private fun correctIndexes(view: View) {
        val ind = findBlockIndByView(view)
        for(i in listOfWires.size - 1 downTo 0) {
            if(listOfWires[i].startBlockId >= ind) {
                listOfWires[i].startBlockId--
            }
            if(listOfWires[i].finishBlockInd >= ind) {
                listOfWires[i].finishBlockInd--
            }
        }
    }
    
    private fun removeBlock(view: View) {
        when(view.parent) {
            bindingScrollBox.scrollBox -> {
                bindingScrollBox.scrollBox.removeView(view)
            }
            bindingStack.stack -> {
                bindingStack.stack.removeView(view)
            }
        }
        listOfBlocks.remove(view)
    }
    
    private fun moveToScrollBox(view: View, location: Point) {
        view.x = 0f
        view.y = 0f
        if(view.parent != bindingScrollBox.scrollBox) {
            bindingStack.stack.removeView(view)
            bindingScrollBox.scrollBox.addView(view)
        }
        view.x = location.x - view.width / 2
        view.y = location.y - view.height / 2
    }
    
    private fun moveToStack(view: View) {
        if(view.parent != bindingStack.stack) {
            bindingScrollBox.scrollBox.removeView(view)
            bindingStack.stack.addView(view)
        }
        view.x = 0f
        view.y = 0f
    }
    
    private fun findBlockIndByView(view: View): Int {
        var ind = 0
        for(i in 0 until listOfBlocks.size) {
            if(view == listOfBlocks[i]) {
                ind = i
                break
            }
        }
        return ind
    }
    
    private fun removeWiresToBlock(view: View) {
        val ind = findBlockIndByView(view)
        for(i in listOfWires.size - 1 downTo 0) {
            if(listOfWires[i].startBlockId == ind || listOfWires[i].finishBlockInd == ind) {
                listOfWires.removeAt(i)
            }
        }
    }
    
    private fun moveWiresToBlock(view: View, delta: Point) {
        val ind = findBlockIndByView(view)
        for(i in listOfWires) {
            if(i.startBlockId == ind) {
                i.outputPoint.x += delta.x
                i.outputPoint.y += delta.y
            } else if(i.finishBlockInd == ind) {
                i.inputPoint.x += delta.x
                i.inputPoint.y += delta.y
            }
        }
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
    
    private fun movePanelWithBlocks(time: Long) {
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
