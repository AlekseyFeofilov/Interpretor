package com.example.interpreter.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.TableRow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import com.example.interpreter.databinding.BlockViewBinding

@SuppressLint("ClickableViewAccessibility")
open class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = BlockViewBinding.inflate(LayoutInflater.from(context), this)
    
    companion object{
        enum class InputType { DOUBLE, STRING, BOOLEAN, FUNCTION }
        
    }
    
    open var name = "Standard Block"
    open var headerColor = "#470505"
    open var rows = listOf(binding.bodyView.getChildAt(1) as BlockRowView)
    
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

/*    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }*/
    
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
    
    private fun setRows(){
        for (row in rows){
            binding.bodyView.addView(row)
        }
    }
    
    private fun setAppearance(){
        binding.headerTextView.text = name
        binding.headerTextView.setBackgroundColor(Color.parseColor(headerColor))
    }
    
    open fun overrideData(){

    }
    
    init {
        overrideData()
        binding.bodyView.removeView(binding.bodyView.getChildAt(1))
        setRows()
        setAppearance()
        
        this.setOnTouchListener(touchListener)
        this.setOnDragListener(dragListener)
        //binding.output1RadioButton.setOnTouchListener(connecting)
    }
}