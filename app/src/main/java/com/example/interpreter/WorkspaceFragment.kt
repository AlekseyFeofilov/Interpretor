package com.example.interpreter

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment


class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    private var isPanelOnScreen = false
    private val panelWithBlocks = BlocksFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.blocksButton)
        button.setOnClickListener {
            if(!isPanelOnScreen) {
                startPanelWithBlocks()
                isPanelOnScreen = true
            }
        }
    }

    private fun startPanelWithBlocks() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_bottom, 0)
        panelWithBlocks.onCreateAnimation(0, true, 1)
        transaction.add(R.id.container, panelWithBlocks)
        transaction.commit()
    }
}