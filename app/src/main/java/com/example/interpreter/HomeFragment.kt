package com.example.interpreter

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.interpreter.vm.VM

class HomeFragment : Fragment(R.layout.fragment_home) {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonStart = view.findViewById<Button>(R.id.start_Button)
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
    }
}