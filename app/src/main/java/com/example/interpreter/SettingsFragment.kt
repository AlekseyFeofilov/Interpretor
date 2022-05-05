package com.example.interpreter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val button = view.findViewById<Button>(R.id.close)
        button.setOnClickListener { hideSettings() }
        
        val consoleVisibilityButton = view.findViewById<SwitchCompat>(R.id.consoleVisibilityButton)
        consoleVisibilityButton.isChecked = isButtonForConsoleVisibility
        consoleVisibilityButton.setOnCheckedChangeListener { _ , isChecked ->
            isButtonForConsoleVisibility = isChecked
        }
    }
    
    private fun hideSettings() {
        //play animation and hide fragment
        var transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(0, R.anim.slide_out_bottom)
        this.onCreateAnimation(0, true, 1)
        transaction.hide(this).commit()
        //pop the fragment from the stack so that the "back" button closes the parent fragment
        transaction = parentFragmentManager.beginTransaction()
        parentFragmentManager.popBackStack()
        transaction.commit()
    }
}