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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.customView.DrawView
import com.example.interpreter.customView.blocks.*
import com.example.interpreter.customView.blocks.WhileBlock
import com.example.interpreter.databinding.*
import com.example.interpreter.vm.VM
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import com.example.interpreter.vm.Compiler


var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)
data class Wire(var isVisible: Boolean,
                var startBlock: BlockView, var endBlock: BlockView,
                var outputPoint: Point, var inputPoint: Point)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private var touchPoint = Point(0f, 0f)
    
    private val scaleInStack = 0.5f
    
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
    private var translationForBlocks = 10f
    
    private val listOfBlocks = mutableListOf<BlockView>()
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
                addBlockToStack(createBlockByClickedButton(button))
            }
        }
        
        // turn on drag-n-drop
        bindingScrollBox.scrollBox.setOnDragListener(dropListener())
        bindingStack.stack.setOnDragListener(dropListener())
        bindingStack.basketContainer.setOnDragListener(dropListener())
        bindingBlocksPanel.panel.setOnDragListener(dropListener())
        
        bindingWorkspace.compile.setOnClickListener{
            VM(Compiler(listOfBlocks[0]).compile()).start()
        }
        
        canvas = DrawView(activity)
        addCanvas(canvas)
    }
    
    private fun addCanvas(canvas: DrawView) {
        val density = this.resources.displayMetrics.density
        val params =
            ConstraintLayout.LayoutParams((11000 * density).toInt(), (11000 * density).toInt())
        bindingScrollBox.scrollBox.addView(canvas, params)
        //canvas.translationZ = 10000f
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchIO() = OnTouchListener { fromView, event ->
        if(!isInScrollBox(fromView)) return@OnTouchListener true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint = getCoordinatesOfIOPoint(fromView, bindingScrollBox.scrollBox)
            }
            MotionEvent.ACTION_MOVE -> {
                val currentPoint = Point(event.x + touchPoint.x, event.y + touchPoint.y)
                if (calculateDistanceBetweenPoints(
                        touchPoint,
                        currentPoint
                    ) >= 1.5*fromView.height
                ) {
                    drawWireInMove(fromView, touchPoint, currentPoint)
                }
            }
            MotionEvent.ACTION_UP -> {
                val toView = findIOWhereMovingEnded(
                    Point(event.x + touchPoint.x, event.y + touchPoint.y)
                )
                if (toView == null) {
                    canvas.draw(listOfWires)
                    return@OnTouchListener true
                }
                if (toView == fromView) {
                    disconnectWireByIO(fromView)
                    removeWireByIO(fromView)
                    canvas.draw(listOfWires)
                    return@OnTouchListener true
                }
                if (isTwoIOPointsInOneBlock(fromView, toView) || !isInputAndOutput(
                        fromView,
                        toView
                    )
                ) {
                    canvas.draw(listOfWires)
                    return@OnTouchListener true
                }
                
                val currentPoint = Point(event.x + touchPoint.x, event.y + touchPoint.y)
                drawWireInMove(fromView, touchPoint, currentPoint)
                makeConnect(fromView, toView)
                
                canvas.draw(listOfWires)
            }
            else -> canvas.draw(listOfWires)
        }
        true
    }
    
    private fun isInScrollBox(view: View): Boolean {
        var buffView = view
        while(buffView != bindingWorkspace.root) {
            if(buffView == bindingScrollBox.scrollBox) return true
            buffView = buffView.parent as View
        }
        return false
    }
    
    private fun makeConnect(first: View, second: View) {
        var input = second
        var output = first
        if (isInputInBlock(first)) {
            input = first
            output = second
        }
        
        if(!listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
            !listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output)) {
            addNewWire(input, output)
        }
        else if(listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
            !listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output)) {
            disconnectWireByIO(input)
            removeWireByIO(input)
            addNewWire(input, output)
        }
        else if(!listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
            listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output)) {
            disconnectWireByIO(output)
            removeWireByIO(output)
            addNewWire(input, output)
        }
        
    }
    
    private fun isInputAndOutput(first: View, second: View): Boolean =
        ((isInputInBlock(first) && !isInputInBlock(second)) ||
                (!isInputInBlock(first) && isInputInBlock(second)))
    
    private fun addNewWire(first: View, second: View) {
        var input = second
        var output = first
        if (isInputInBlock(first)) {
            input = first
            output = second
        }
        val inputCoordinate =
            getCoordinatesOfIOPoint(input, listOfBlocks[findBlockIndByIOView(input)])
        inputCoordinate.x += input.width / 2
        inputCoordinate.y += input.height / 2
        val outputCoordinate =
            getCoordinatesOfIOPoint(output, listOfBlocks[findBlockIndByIOView(output)])
        outputCoordinate.x += output.width / 2
        outputCoordinate.y += output.height / 2
        
        val newWire = Wire(
            true,
            listOfBlocks[findBlockIndByIOView(output)],
            listOfBlocks[findBlockIndByIOView(input)],
            outputCoordinate,
            inputCoordinate
        )
        listOfWires.add(newWire)
        newWire.startBlock.connectOutput(
            newWire.startBlock.findOutputByOutputRadioButton(output)!!,
            newWire.endBlock.findInputByInputRadioButton(input)!!
        )
    }
    
    private fun drawWireInMove(from: View, first: Point, second: Point) {
        if (isInputInBlock(from))
            canvas.draw(
                listOfWires, Wire(
                    true,
                    listOfBlocks[0], listOfBlocks[0], second,
                    Point(
                    first.x + from.width / 2,
                    first.y + from.height / 2
                ))
            )
        else
            canvas.draw(
                listOfWires, Wire(
                    true,
                    listOfBlocks[0], listOfBlocks[0], Point(
                        first.x + from.width / 2,
                        first.y + from.height / 2
                    ), second
                )
            )
    }
    
    private fun isInputInBlock(view: View): Boolean {
        for (block in listOfBlocks) {
            for (input in block.getListOfInputView()) {
                if (input == view) return true
            }
        }
        return false
    }
    private fun isInputInBlock(view: View, block: BlockView): Boolean {
        for (input in block.getListOfInputView()) {
            if (input == view) return true
        }
        return false
    }
    
    private fun isTwoIOPointsInOneBlock(first: View, second: View): Boolean {
        for (block in listOfBlocks) {
            if (isIOPointInBlock(first, block) && isIOPointInBlock(second, block)) return true
        }
        return false
    }
    
    private fun isIOPointInBlock(IO: View, block: BlockView): Boolean {
        for (input in block.getListOfInputView()) {
            if (input == IO) return true
        }
        for (output in block.getListOfOutputView()) {
            if (output == IO) return true
        }
        return false
    }
    
    private fun calculateDistanceBetweenPoints(first: Point, second: Point) =
        sqrt((first.x - second.x).pow(2f) + (first.y - second.y).pow(2f))
    
    private fun findBlockIndByIOView(IO: View): Int {
        for (ind in 0 until listOfBlocks.size) {
            for (input in listOfBlocks[ind].getListOfInputView()) {
                if (IO == input) {
                    return ind
                }
            }
            for (output in listOfBlocks[ind].getListOfOutputView()) {
                if (IO == output) {
                    return ind
                }
            }
        }
        return 0
    }
    
    private fun findIOWhereMovingEnded(point: Point): View? {
        for (block in listOfBlocks) {
            for (input in block.getListOfInputView()) {
                if (isPointInView(point, input)) {
                    return input
                }
            }
            for (output in block.getListOfOutputView()) {
                if (isPointInView(point, output)) {
                    return output
                }
            }
        }
        return null
    }
    
    private fun isPointInView(point: Point, view: View): Boolean {
        val block = getCoordinatesOfIOPoint(view, bindingScrollBox.scrollBox)
        return (block.x <= point.x && point.x <= block.x + view.width &&
                block.y <= point.y && point.y <= block.y + view.height)
    }
    
    private fun getCoordinatesOfIOPoint(view: View, endParent: View): Point {
        val point = Point(0f, 0f)
        var buffView = view
        while (buffView != bindingScrollBox.scrollBox) {
            point.x += buffView.x
            point.y += buffView.y
            if(buffView.parent == bindingWorkspace.root) break
            buffView = (buffView.parent as View)
            if (buffView == endParent) break
        }
        return point
    }
    
    // generate and put in stack blocks
    @SuppressLint("UseRequireInsteadOfGet")
    private fun createBlockByClickedButton(view: View): BlockView =
        when (view) {
            bindingListOfBlocks.ASSIGN -> {
                val newButton = AssignBlock(context!!)
                newButton
            }
            bindingListOfBlocks.WHILE -> {
                val newBlock = WhileBlock(context!!)
                newBlock
            }
            bindingListOfBlocks.COMPARE -> {
                val newBlock = CompareBlock(context!!)
                newBlock
            }
            bindingListOfBlocks.IF -> {
                val newBlock = IfBlock(context!!)
                newBlock
            }
            bindingListOfBlocks.INIT -> {
                val newBlock = InitializationBlock(context!!)
                newBlock
            }
            else -> {
                val newBlock = InitializationBlock(context!!)
                newBlock
            }
        }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun addBlockToStack(block: BlockView) {
        bindingStack.stack.addView(block)
        for (i in block.getListOfOutputView()) {
            i.setOnTouchListener(onTouchIO())
        }
        for (i in block.getListOfInputView()) {
            i.setOnTouchListener(onTouchIO())
        }
        block.setOnLongClickListener(dragListener())
        block.translationZ = translationForBlocks
        translationForBlocks++
        block.scaleX *= scaleInStack
        block.scaleY *= scaleInStack
        listOfBlocks.add(block)
    }
    
    
    // drag-n-drop for blocks
    @SuppressLint("ClickableViewAccessibility")
    private fun dragListener() = OnLongClickListener { view ->
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
    
    private fun dropListener() = OnDragListener { view, event ->
        if(view is BlockView) view.translationZ = translationForBlocks
        translationForBlocks++
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                when (view) {
                    bindingBlocksPanel.panel -> { return@OnDragListener true }
                    bindingStack.basketContainer -> {
                        removeAllWiresToBlock(draggingView as BlockView)
                        removeBlockFromScreen(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                    bindingStack.stack -> {
                        moveToStack(draggingView as BlockView, Point(event.x, event.y))
                        correctNearBorder(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                    bindingScrollBox.scrollBox -> {
                        moveToScrollBox(draggingView as BlockView, Point(event.x, event.y))
                        //correctNearBorder(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                }
                
                
            }
        }
        true
    }
    
    private fun correctNearBorder(block: BlockView) {
        val parent = block.parent
        var scale = scaleInStack
        if(parent == bindingScrollBox.scrollBox) scale = 1f
        if(parent is ConstraintLayout) {
            when {
                block.x < 0 -> block.x = 0f
                block.x + block.width * scale > parent.width -> block.x = parent.width - block.width * scale
            }
            when {
                block.y < 0 -> block.y = 0f
                block.y + block.height * scale > parent.height -> block.y = parent.height - block.height * scale
            }
        }
    }
    
    private fun removeBlockFromScreen(block: BlockView) {
        when (block.parent) {
            bindingScrollBox.scrollBox -> {
                bindingScrollBox.scrollBox.removeView(block)
            }
            bindingStack.stack -> {
                bindingStack.stack.removeView(block)
            }
        }
        disconnectAllWires(block)
        listOfBlocks.remove(block)
    }
    
    private fun disconnectAllWires(block: BlockView) {
        for(i in block.getListOfInputView()) {
            disconnectWireByIO(i, block)
        }
        for(i in block.getListOfOutputView()) {
            disconnectWireByIO(i, block)
        }
    }
    
    private fun disconnectWireByIO(IO: View) {
        val block = listOfBlocks[findBlockIndByIOView(IO)]
        disconnectWireByIO(IO, block)
    }
    private fun disconnectWireByIO(IO: View, block: BlockView) {
        if(isInputInBlock(IO, block)) block.disconnectInput(block.findInputByInputRadioButton(IO)!!)
        else block.disconnectOutputAll(block.findOutputByOutputRadioButton(IO)!!)
    }
    
    private fun removeWireByIO(IO: View) {
        val block = listOfBlocks[findBlockIndByIOView(IO)]
        removeWireByIO(IO, block)
    }
    private fun removeWireByIO(IO: View, block: BlockView) {
        val point = getCoordinatesOfIOPoint(IO, block)
        point.x += IO.width/2
        point.y += IO.height/2
        for(i in listOfWires.size - 1 downTo 0) {

            if((listOfWires[i].inputPoint == point && listOfWires[i].endBlock == block) ||
                (listOfWires[i].outputPoint == point && listOfWires[i].startBlock == block)) {
                listOfWires.removeAt(i)
            }
        }
    }
    
    private fun moveToScrollBox(block: BlockView, location: Point) {
        if (block.parent != bindingScrollBox.scrollBox) {
            bindingStack.stack.removeView(block)
            bindingScrollBox.scrollBox.addView(block)
            changeVisibilityWiresForBlock(block)
            for(i in listOfBlocks) {
                if(i == block) {
                    i.scaleX /= scaleInStack
                    i.scaleY /= scaleInStack
                }
            }
        }
        block.x = location.x - block.width / 2
        block.y = location.y - block.height / 2
    }
    
    private fun moveToStack(block: BlockView, location: Point) {
        if (block.parent != bindingStack.stack) {
            bindingScrollBox.scrollBox.removeView(block)
            bindingStack.stack.addView(block)
            changeVisibilityWiresForBlock(block)
            for(i in listOfBlocks) {
                if(i == block) {
                    i.scaleX *= scaleInStack
                    i.scaleY *= scaleInStack
                }
            }
        }
        block.x = location.x - block.width / 2
        block.y = location.y - block.height / 2
    }
    
    private fun changeVisibilityWiresForBlock(block: BlockView) {
        for(i in listOfWires) {
            if(i.startBlock == block || i.endBlock == block) {
                i.isVisible = !i.isVisible
            }
        }
    }
    
    private fun removeAllWiresToBlock(block: BlockView) {
        for (i in listOfWires.size - 1 downTo 0) {
            if (listOfWires[i].startBlock == block || listOfWires[i].endBlock == block) {
                listOfWires.removeAt(i)
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
            val to = Point(blocksPanel.x, 0.02f * metrics.bounds.height())
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
