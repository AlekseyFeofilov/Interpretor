package com.example.interpreter.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.example.interpreter.databinding.BlockViewBinding
import com.example.interpreter.mainBlock.Input
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Output
import com.example.interpreter.mainBlock.ioTypes.InputDouble

@SuppressLint("ClickableViewAccessibility")
open class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), IOContainer {
    private val binding = BlockViewBinding.inflate(LayoutInflater.from(context), this)
    
    override val view = this
    override var inputs = mutableListOf<Pair<Input, Output?>>()
    override var outputs = mutableListOf<Pair<Output, List<Input>>>()
    
    //todo: add spinner for InputBoolean
    override fun addInput(input: Input, before: Input?) {
        super.addInput(input, before)
        
        val row = InputView(context)
        val currentInput = inputs[findIndexByInput(input)].first
        
        row.initComponents(
            input, true, !currentInput.description.isNullOrEmpty(), currentInput.isDefault
        )
        
        row.setDescription(input.description)
        binding.listOfInputLinearLayout.addView(row, findIndexByInput(input))
    }
    
    override fun addOutput(output: Output, before: Output?) {
        super.addOutput(output, before)
    
        val row = OutputView(context)
        val currentInput = outputs[findIndexByOutput(output)].first
    
        row.initComponents(
            true, !currentInput.description.isNullOrEmpty()
        )
    
        row.setDescription(output.description)
        binding.listOfOutputLinearLayout.addView(row, findIndexByOutput(output))
    }
    
    override fun removeInput(input: Input, disconnectInput: Boolean) {
        binding.listOfInputLinearLayout.removeViewAt(findIndexByInput(input))
        super.removeInput(input, disconnectInput)
    }
    
    override fun removeOutput(output: Output) {
        binding.listOfOutputLinearLayout.removeViewAt(findIndexByOutput(output))
        
        super.removeOutput(output)
    }
    
    override fun connectInput(input: Input, output: Output, connectOutput: Boolean) {
        super.connectInput(input, output, connectOutput)
    
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
        val currentInput = inputs[findIndexByInput(input)].first
        
        if(currentInput.isDefault){
            row.hideDefaultValue()
        }
    }
    
    override fun disconnectInput(input: Input, disconnectOutput: Boolean) {
        super.disconnectInput(input, disconnectOutput)
        if(findIndexByInput(input) == -1) return
        
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
        val currentInput = inputs[findIndexByInput(input)].first
    
        if(currentInput.isDefault){
            row.showDefaultValue()
        }
    }
    
    override fun setHeader(name: String, colorHEX: String) {
        binding.headerTextView.text = name
        binding.headerTextView.setBackgroundColor(Color.parseColor(colorHEX))
    }
    
    private val touchListener = OnTouchListener { it, _ ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            it.startDrag(null, DragShadowBuilder(it), it, 0)
        } else {
            it.startDragAndDrop(null, DragShadowBuilder(it), it, 0)
        }
        //it.visibility = View.INVISIBLE
        true
    }
    
    private val dragListener = OnDragListener { view, dragEvent ->
        val draggableItem = dragEvent.localState as View
        
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED,
            DragEvent.ACTION_DRAG_LOCATION,
            DragEvent.ACTION_DRAG_EXITED,
            DragEvent.ACTION_DRAG_ENTERED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.x = dragEvent.x - draggableItem.width / 2
                draggableItem.y = dragEvent.y - draggableItem.height / 2
                draggableItem.visibility = View.VISIBLE
                
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                draggableItem.x = dragEvent.x - draggableItem.width / 2
                draggableItem.y = dragEvent.y - draggableItem.height / 2
                draggableItem.visibility = View.VISIBLE
                
                view.invalidate()
                true
            }
            else -> false
        }
    }

/*    private val connecting = OnTouchListener { it, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                canvas.startDraw(it.x + it.width / 2, it.y + it.height / 2)
                //it.isEnabled = false
            }
        }
        false
    }*/
    
    /*val connect = OnClickListener {
        connectInput(inputs[0].first, outputs[0].first)
       
    }*/
    
    init {
        this.setOnTouchListener(touchListener)
        this.setOnDragListener(dragListener)
        
        //binding.listOfInputLinearLayout.setOnClickListener(connect)
        //binding.output1RadioButton.setOnTouchListener(connecting)
    }
}