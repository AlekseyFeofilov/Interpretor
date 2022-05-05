package com.example.interpreter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.interpreter.customView.DrawView
import com.example.interpreter.customView.blockWhile.BlockWhile
import com.example.interpreter.customView.blockWhile.RowCondition
import com.example.interpreter.databinding.FragmentWorkspaceBinding

class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private var isPanelOnScreen = false
    private val panelWithBlocks = BlocksFragment()
    
    private lateinit var binding: FragmentWorkspaceBinding
    
    private var position = object {
        var curX = 0f
        var curY = 0f
        var prevX = 0f
        var prevY = 0f
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkspaceBinding.bind(view)
        
        val canvas = DrawView(activity)
        addCanvas(canvas)
        
        binding.blocksButton.setOnClickListener {
            if (!isPanelOnScreen) {
                startPanelWithBlocks()
                isPanelOnScreen = true
            }
        }
        
        binding.scrollView.scrollable = false
        binding.horizontalScrollView.scrollable = false
        //binding.scrollView.setOnTouchListener(twoDimensionScrolling)
    }
    
    private fun addCanvas(canvas: DrawView) {
        val density = this.resources.displayMetrics.density
        val params =
            ConstraintLayout.LayoutParams((5000 * density).toInt(), (5000 * density).toInt())
        binding.constraintLayout.addView(canvas, params)
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private val twoDimensionScrolling = View.OnTouchListener { _, action ->
        position.curX = action.x
        position.curY = action.y
        
        val dx = (position.prevX - position.curX).toInt()
        val dy = (position.prevY - position.curY).toInt() / 6
        
        when (action.action) {
            MotionEvent.ACTION_MOVE -> {
                position.prevX = position.curX
                position.prevY = position.curY
                
                binding.scrollView.scrollBy(0, dy)
                binding.horizontalScrollView.scrollBy(dx, 0)
            }
            MotionEvent.ACTION_UP -> {
                binding.scrollView.scrollBy(0, dy)
                binding.horizontalScrollView.scrollBy(dx, 0)
            }
        }
        false
    }
    
    private fun startPanelWithBlocks() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_bottom, 0)
        panelWithBlocks.onCreateAnimation(0, true, 1)
        transaction.add(R.id.container, panelWithBlocks)
        transaction.commit()
    }
}