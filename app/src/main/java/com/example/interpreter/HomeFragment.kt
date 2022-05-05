package com.example.interpreter

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val settingsPanel = SettingsFragment()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonStart = view.findViewById<Button>(R.id.start_Button)
        val buttonSettings = view.findViewById<Button>(R.id.settings_Button)
        val animationFromLeft = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_out_rigth
                popExit = R.anim.slide_in_left
            }
        }
        buttonStart.setOnClickListener {
            findNavController().navigate(R.id.workspaceFragment, null, animationFromLeft)
        }
        if(!settingsPanel.isAdded) {
            createSettingsPanel()
        }
        buttonSettings.setOnClickListener {
            if(settingsPanel.isHidden) {
                takeSettingsPanel()
            }
        }
    }
    
    private fun createSettingsPanel() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.container, settingsPanel).hide(settingsPanel).commit()
    }
    
    private fun takeSettingsPanel() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom)
        settingsPanel.onCreateAnimation(0, true, 1)
        transaction.addToBackStack(null)
        transaction.show(settingsPanel).commit()
    }
}