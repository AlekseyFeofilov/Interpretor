package com.example.interpreter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment

class BlocksFragment : Fragment(R.layout.fragment_blocks) {
    private val stackOfBlocks = mutableListOf<View>()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener { hide() }
        
        //val listOfBlocks = view.findViewById<ConstraintLayout>(R.id.listOfBlocks)
        //for(i in 0..9) {
            //listOfBlocks.getChildAt(i).setO
        //}
    }
    
    private fun addBlockToStack(view: View) {
    
    }
    
    private fun hide() {
        var transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(0, R.anim.slide_out_bottom)
        this.onCreateAnimation(0, true, 1)
        transaction.hide(this).commit()
        transaction = parentFragmentManager.beginTransaction()
        parentFragmentManager.popBackStack()
        transaction.commit()
    }
}