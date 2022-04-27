package com.example.interpreter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class BlocksFragment : Fragment(R.layout.fragment_blocks) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener { destroy() }
    }

    private fun destroy() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(0, R.anim.slide_out_bottom)
        this.onCreateAnimation(0, true, 1)
        transaction.remove(this)
        transaction.commit()
    }
}