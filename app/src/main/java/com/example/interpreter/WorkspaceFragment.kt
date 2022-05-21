package com.example.interpreter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.DrawView
import com.example.interpreter.customView.Line
import com.example.interpreter.customView.blockView.BlockView
import com.example.interpreter.customView.blocks.*
import com.example.interpreter.databinding.*
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.VM
import com.example.interpreter.vm.instruction.If
import kotlin.math.pow
import kotlin.math.sqrt


var isConsoleHidden = true
var isBlocksPanelHidden = true

data class Point(var x:Float, var y:Float)
data class Wire(var isVisible: Boolean,
                var startBlock: BlockView, var endBlock: BlockView,
                var outputPoint: Point, var inputPoint: Point)

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private var touchPoint = Point(0f, 0f)
    
    private var scaleInStack = 0.5f
    
    private var isPanelMoving = false
    private var isMovingScreenOn = true
    
    private lateinit var console: ConstraintLayout
    private lateinit var blocksPanel: ConstraintLayout
    
    private lateinit var consoleBody: LinearLayout
    var listOfReading = mutableListOf<String>()
    private lateinit var canvas: DrawView
    
    private lateinit var bindingWorkspace: FragmentWorkspaceBinding
    private lateinit var bindingConsole: FragmentRightPanelBinding
    private lateinit var bindingStack: StackForWorkspaceBinding
    private lateinit var bindingBlocksPanel: FragmentBlocksBinding
    private lateinit var bindingListOfBlocks: ListOfBlocksBinding
    private lateinit var bindingScrollBox: ScrollBoxBinding
    
    private lateinit var draggingView: View
    private lateinit var dragShadow: DragShadowBuilder
    private var translationForBlocks = 10f
    
    private val listOfBlocks = mutableListOf<BlockView>()
    private val listOfWires = mutableListOf<Wire>()
    
    @SuppressLint("ClickableViewAccessibility", "UseRequireInsteadOfGet")
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
                movePanelWithBlocks(200)
            }
            else {
                movePanelWithBlocks(200L)
                moveConsole(200L)
            }
        }
        
        // put panel with blocks down screen
        blocksPanel = bindingBlocksPanel.blocksContainer
        blocksPanel.y = metrics.bounds.height().toFloat()
        // set touch listener for button that pull console
        consoleBody = bindingConsole.consoleBody
        consoleBody.addView(EditText(context))
        getListOfTextViewFromConsole()[getListOfTextViewFromConsole().size - 1].setOnKeyListener(keyListener())
        
        console = bindingConsole.console
        //bindingConsole.buttonPanel.setOnTouchListener(consoleMoving())
        
        // show or hide button that call or close console
        //if (isButtonForConsoleVisibility) {
        bindingConsole.consoleButton.setOnClickListener {
            if (isBlocksPanelHidden) {
                moveConsole(200L)
            }
            else {
                movePanelWithBlocks(200L)
                moveConsole(200L)
            }
        }
        //} else {
        //    bindingConsole.consoleButton.visibility = View.INVISIBLE
        //}
        
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(8, 8, 8, 8)
        addBlockToGroup(StartBlock(context!!), params, context!!)
        addBlockToGroup(InitializationBlock(context!!), params, context!!)
        addBlockToGroup(InputBlock(context!!), params, context!!)
        addBlockToGroup(PrintBlock(context!!), params, context!!)
        addBlockToGroup(SetObjectBlock(context!!), params, context!!)
        addBlockToGroup(WhileBlock(context!!), params, context!!)
        addBlockToGroup(IfBlock(context!!), params, context!!)
        addBlockToGroup(GetObjectBlock(context!!), params, context!!)
        addBlockToGroup(CompareBlock(context!!), params, context!!)
        addBlockToGroup(BoolBlock(context!!), params, context!!)
        addBlockToGroup(AssignBlock(context!!), params, context!!)
        
        
        // set on click listener for button that add block from panel to stack
//        for (i in 0 until bindingListOfBlocks.listOfBlocks.childCount) {
//            bindingListOfBlocks.listOfBlocks.getChildAt(i).scaleX = 0.95f
//            bindingListOfBlocks.listOfBlocks.getChildAt(i).setOnClickListener { block ->
////                addBlockToStack(createBlockByClickedButton(block))
//            }
//        }
        
        // turn on drag-n-drop
        bindingScrollBox.scrollBox.setOnDragListener(dropListener())
        bindingStack.stack.setOnDragListener(dropListener())
        bindingStack.basketContainer.setOnDragListener(dropListener())
        bindingBlocksPanel.panel.setOnDragListener(dropListener())
        
        
        canvas = DrawView(activity)
        addCanvas(canvas)
        
        bindingStack.basketContainer.visibility = INVISIBLE

        console.x = metrics.bounds.width().toFloat()
        bindingConsole.flush.setOnClickListener{ flushConsole() }
        //bindingScrollBox.scrollBox.setOnTouchListener{ view, event -> vibrate(1000L); true }
        bindingConsole.run.setOnClickListener {
            for(i in listOfBlocks) {
                if(i is StartBlock) {
                    Toast.makeText(context, getString(R.string.comile_is_start), Toast.LENGTH_SHORT)
                    //TODO: change for all start blocks
                    try {
                        VM(Compiler(i, this).compile()).start()
                    } catch (e: Error) {
                        printlnToConsole(e.toString(), "#FF3F00")
                        Log.i("TAG", e.stackTraceToString())
                    } catch (e: Exception) {
                        printlnToConsole("look at logcat", "#FF3F00")
                        Log.i("TAG", e.stackTraceToString())
                    }
                    break
                }
            }
        }
    }
    
    private fun addBlockToGroup(block: BlockView, params: LinearLayout.LayoutParams, context: Context) {
        setListenersForBlock(block)
        when (block) {
            is AssignBlock -> {
                val newBlock = AssignBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 10, params)
                setListenersForBlock(newBlock)
            }
            is BoolBlock -> {
                val newBlock = BoolBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 9, params)
                setListenersForBlock(newBlock)
            }
            is CompareBlock -> {
                val newBlock = CompareBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 8, params)
                setListenersForBlock(newBlock)
            }
            is GetObjectBlock -> {
                val newBlock = GetObjectBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 7, params)
                setListenersForBlock(newBlock)
            }
            is IfBlock -> {
                val newBlock = IfBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 6, params)
                setListenersForBlock(newBlock)
            }
            is InitializationBlock -> {
                val newBlock = InitializationBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 1, params)
                setListenersForBlock(newBlock)
            }
            is InputBlock -> {
                val newBlock = InputBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 2, params)
                setListenersForBlock(newBlock)
            }
            is PrintBlock -> {
                val newBlock = PrintBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 3, params)
                setListenersForBlock(newBlock)
            }
            is SetObjectBlock -> {
                val newBlock = SetObjectBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 4, params)
                setListenersForBlock(newBlock)
            }
            is StartBlock -> {
                val newBlock = StartBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 0, params)
                setListenersForBlock(newBlock)
            }
            is WhileBlock -> {
                val newBlock = WhileBlock(context)
                bindingListOfBlocks.listOfBlocks.addView(newBlock, 5, params)
                setListenersForBlock(newBlock)
            }
        }
    }
    
    private fun setListenersForBlock(block: BlockView) {
        for (i in block.getListOfOutputView()) {
            i.setOnTouchListener(onTouchIO())
        }
        for (i in block.getListOfInputView()) {
            i.setOnTouchListener(onTouchIO())
        }
        block.translationZ = translationForBlocks
        translationForBlocks++
        
        block.setOnClickListener {
            val delta = Point(block.x, block.y)
            bindingListOfBlocks.listOfBlocks.removeView(block)
            moveToStack(block, Point(400f, 300f), delta)
            listOfBlocks.add(block)
            //correctNearBorder(block)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 8, 8, 8)
            addBlockToGroup(block, params, context!!)
            block.setOnClickListener{ }
        }
        block.setOnLongClickListener(dragListener())
    }
    
     //generate and put in stack blocks
   // @SuppressLint("UseRequireInsteadOfGet")
//    private fun createBlockByClickedButton(view: View): BlockView =
//        when (view) {
//            bindingListOfBlocks.ASSIGN -> { AssignBlock(context!!) }
//            bindingListOfBlocks.WHILE -> { WhileBlock(context!!) }
//            bindingListOfBlocks.COMPARE -> { CompareBlock(context!!) }
//            bindingListOfBlocks.IF -> { IfBlock(context!!) }
//            bindingListOfBlocks.INIT -> { InitializationBlock(context!!) }
//            bindingListOfBlocks.BOOL -> { BoolBlock(context!!) }
//            else -> { InitializationBlock(context!!) }
//        }
    
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
    
    private fun moveConsole(time: Long) {
        isPanelMoving = true
        isConsoleHidden = if (console.x > 0.6 * metrics.bounds.width()) {
            takeConsole(200)
            false
        } else {
            hideConsole(200)
            true
        }
        isPanelMoving = false
    }
    @Suppress("DEPRECATION")
    private fun vibrate(time: Long) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(time,VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(time)
        }
    }
    private fun getListOfTextViewFromConsole(): MutableList<TextView> {
        val list = mutableListOf<TextView>()
        consoleBody.forEach { list.add(it as TextView) }
        return list
    }
    
    fun flushConsole() {
        consoleBody.removeAllViews()
        consoleBody.addView(EditText(context!!))
        listOfReading.removeAll(listOfReading)
    }
    
    fun printToConsole(text: String) {
        val list = getListOfTextViewFromConsole()
        if(isConsoleHidden) {
            takeConsole(200L)
            isConsoleHidden = false
        }
        if(list.size > 1) {
            val newText = list[list.size - 2].text.toString() + text
            list[list.size - 2].text = newText
        }else printlnToConsole(text)
    }
    @SuppressLint("SetTextI18n")
    fun printlnToConsole(text: String) {
        val list = getListOfTextViewFromConsole()
        if(list.size > 1) {
            val newText = list[list.size - 2].text.toString() + text
            list[list.size - 2].text = newText
        }
        val newLine = TextView(context)
        consoleBody.addView(newLine, consoleBody.childCount - 1)
    }
    
//    fun printToConsole(text: String, color: String) {
//        val list = getListOfTextViewFromConsole()
//        if(isConsoleHidden) {
//            takeConsole(200L)
//            isConsoleHidden = false
//        }
//        if(list.size > 1) {
//            val newText = list[list.size - 1].text.toString() + text
//            list[list.size - 2].text = newText
//        }else printlnToConsole(text, color)
//    }
    
    @SuppressLint("SetTextI18n")
    fun printlnToConsole(text: String, color: String) {
        val newLine = TextView(context)
        consoleBody.addView(newLine, consoleBody.childCount - 1)
        newLine.setTextColor(Color.parseColor(color))
        newLine.text = ">> $text"
    }
    
    fun readlnFromConsole(): String {
        if(isConsoleHidden) {
            takeConsole(200L)
            isConsoleHidden = false
        }
        return if(listOfReading.isNotEmpty()) {
            val line = listOfReading[0]
            listOfReading.removeAt(0)
            line
        } else {
            ""
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun keyListener() = OnKeyListener { view, key, event ->
        if (event.action == KeyEvent.ACTION_DOWN &&
            key == KeyEvent.KEYCODE_ENTER &&
            (view as EditText).text.isNotEmpty()
        ) {
            val newLine = TextView(context)
            consoleBody.addView(newLine, consoleBody.childCount - 1)
            newLine.text = "<< ${view.text}"
            listOfReading.add(view.text.toString())
        }
        if(key == KeyEvent.KEYCODE_ENTER) {
            (view as EditText).text.clear()
        }
        false
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
                vibrate(100L)
                touchPoint = getCoordinatesOfIOPoint(fromView, bindingScrollBox.scrollBox)
            }
            MotionEvent.ACTION_MOVE -> {
                    val currentPoint = Point(event.x + touchPoint.x, event.y + touchPoint.y)
                if (calculateDistanceBetweenPoints(
                        Point(touchPoint.x + fromView.width/2, touchPoint.y + fromView.height/2),
                        currentPoint) >= 1.5*fromView.height) {
                    drawWireInMove(fromView, touchPoint, currentPoint)
                    
                }
            }
            MotionEvent.ACTION_UP -> {
                val toView = findIOWhereMovingEnded(
                    Point(event.x + touchPoint.x, event.y + touchPoint.y))
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
                        fromView, toView)) {
                    canvas.draw(listOfWires)
                    return@OnTouchListener true
                }
                
                val currentPoint = Point(event.x + touchPoint.x, event.y + touchPoint.y)
                drawWireInMove(fromView, touchPoint, currentPoint)
                makeConnect(fromView, toView)
                
                recalculateWiresPoint()
                
                canvas.draw(listOfWires)
            }
            else -> canvas.draw(listOfWires)
        }
        true
    }
    
//    private fun updateSizeOfBlocks() {
//        for(i in listOfBlocks) {
//            i.invalidate()
//        }
//    }
    
    
    
    private fun recalculateWiresPoint() {
        for(i in 0 until listOfWires.size) {
            val outputX = getCoordinatesOfIOPoint(listOfWires[i].startBlock.getListOfOutputView()[0], listOfWires[i].startBlock).x
            listOfWires[i].outputPoint.x = outputX
        }
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
    
        when {
            !listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
                    !listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output) -> {
                addNewWire(input, output)
            }
            listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
                    !listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output) -> {
                disconnectWireByIO(input)
                removeWireByIO(input)
                addNewWire(input, output)
            }
            !listOfBlocks[findBlockIndByIOView(input)].isInputComplete(input) &&
                    listOfBlocks[findBlockIndByIOView(output)].isOutputComplete(output) -> {
                disconnectWireByIO(output)
                removeWireByIO(output)
                addNewWire(input, output)
            }
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
                    first.y + from.height / 2)))
        else
            canvas.draw(
                listOfWires, Wire(
                    true,
                    listOfBlocks[0], listOfBlocks[0], Point(
                        first.x + from.width / 2,
                        first.y + from.height / 2),
                    second))
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
    
    
    
    
    // drag-n-drop for blocks
    @SuppressLint("ClickableViewAccessibility", "UseRequireInsteadOfGet")
    private fun dragListener() = OnLongClickListener { view ->
        val data = ClipData.newPlainText("", "")
        vibrate(100L)
    
        //dragShadow = DragShadowBuilder(view)
        if(view.parent == bindingListOfBlocks.listOfBlocks) {
            
            movePanelWithBlocks(200L)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 8, 8, 8)
            addBlockToGroup(view as BlockView, params, context!!)
        }
        draggingView = view as View
        dragShadow = DragShadowBuilder(draggingView)
        bindingStack.basketContainer.translationZ = 100f
        bindingStack.basketContainer.visibility = VISIBLE
        if(view is BlockView) view.translationZ = translationForBlocks
        translationForBlocks++
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            view.startDrag(data, dragShadow, view, 0)
        } else {
            view.startDragAndDrop(data, dragShadow, view, 0)
        }
        
        true
    }
    
    private fun dropListener() = OnDragListener { view, event ->
        if(!isBlocksPanelHidden) movePanelWithBlocks(200L)
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                if(view == bindingStack.basketContainer) vibrate(100L)
            }
            DragEvent.ACTION_DROP -> {
                var delta = Point(0f, 0f)
                if(draggingView.parent == bindingListOfBlocks.listOfBlocks) {
                    bindingListOfBlocks.listOfBlocks.removeView(draggingView)
                    delta = Point(draggingView.x, draggingView.y)
                }
                when (view) {
                    bindingStack.basketContainer -> {
                        removeAllWiresToBlock(draggingView as BlockView)
                        removeBlockFromScreen(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                    bindingStack.stack -> {
                        moveToStack(draggingView as BlockView, Point(event.x, event.y), delta)
                        correctNearBorder(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                    bindingScrollBox.scrollBox -> {
                        moveToScrollBox(draggingView as BlockView, Point(event.x, event.y), delta)
                        correctNearBorder(draggingView as BlockView)
                        canvas.draw(listOfWires)
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                if(draggingView.parent == bindingListOfBlocks.listOfBlocks)
                    bindingListOfBlocks.listOfBlocks.removeView(draggingView)
                bindingStack.basketContainer.visibility = INVISIBLE
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
                block.x + block.width * (1 - scale) / 2 < 0 -> block.x = 0f - block.width * (1 - scale) / 2
                block.x + block.width * (1 + scale) / 2 > parent.width -> block.x = parent.width - block.width * (1 + scale) / 2
            }
            when {
                block.y + block.height * (1 - scale) / 2 < 0 -> block.y = 0f - block.height * (1 - scale) / 2
                block.y + block.height * (1 + scale) / 2 > parent.height -> block.y = parent.height - block.height * (1 + scale) / 2
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
    
    private fun moveToScrollBox(block: BlockView, location: Point, delta: Point) {
        if (block.parent != bindingScrollBox.scrollBox) {
            if(block.parent == bindingStack.stack) bindingStack.stack.removeView(block)
            if(block.parent == bindingListOfBlocks.listOfBlocks) {
                listOfBlocks.add(block)
                block.setOnClickListener {  }
            }
            else {
                block.scaleX /= scaleInStack
                block.scaleY /= scaleInStack
            }
            bindingScrollBox.scrollBox.addView(block)
            changeVisibilityWiresForBlock(block)

        }
        block.x = location.x + delta.x - block.width / 2
        block.y = location.y + delta.y - block.height / 2
    }
    
    private fun moveToStack(block: BlockView, location: Point, delta: Point) {
        if (block.parent != bindingStack.stack) {
            if(block.parent == bindingScrollBox.scrollBox) bindingScrollBox.scrollBox.removeView(block)
            if(block.parent == bindingListOfBlocks.listOfBlocks) {
                listOfBlocks.add(block)
                block.setOnClickListener {  }
            }
            bindingStack.stack.addView(block)
            changeVisibilityWiresForBlock(block)
            block.scaleX *= scaleInStack
            block.scaleY *= scaleInStack
        }
        block.x = location.x + delta.x - block.width / 2
        block.y = location.y + delta.y - block.height / 2
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
    
    private fun hideConsole(time: Long) {
        val from = Point(console.x, console.y)
        val to = Point(metrics.bounds.width().toFloat(), console.y)
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
