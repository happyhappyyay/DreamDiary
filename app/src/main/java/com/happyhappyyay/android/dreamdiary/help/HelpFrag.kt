package com.happyhappyyay.android.dreamdiary.help

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.happyhappyyay.android.dreamdiary.R
import com.happyhappyyay.android.dreamdiary.databinding.FragmentHelpBinding

private var _binding: FragmentHelpBinding? = null
private val binding get() = _binding!!

/**
 * A simple [Fragment] subclass.
 */
class HelpFrag : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater,container,false)
        if(savedInstanceState!=null){
            val box = savedInstanceState.getInt("BOX")
            if(box == 1){
                binding.voiceCommandsSelector.isChecked = true
            }
            else if(box == 2){
                binding.appearanceSelector.isChecked = true
            }
        }
        binding.appearanceSelector.setOnClickListener {
            setBackground()
        }
        binding.voiceCommandsSelector.setOnClickListener {
            setBackground()
        }
        binding.howToUseSelector.setOnClickListener {
            setBackground()
        }
        setBackground()
        return binding.root
    }

    private fun setBackground(){
        Log.d("HelpFrag","${binding.appearanceSelector}")
        if(context!=null) {
            if (binding.appearanceSelector.isChecked) {
                if (binding.appearanceView.visibility != View.VISIBLE) {
                    binding.appearanceView.visibility = View.VISIBLE
                    binding.header.text = context!!.getString(R.string.appearance)
                }
            }
            else {
                if (binding.appearanceView.visibility == View.VISIBLE) {
                    binding.appearanceView.visibility = View.GONE
                }
            }
            if (binding.howToUseSelector.isChecked) {
                if (binding.howToUseView.visibility != View.VISIBLE) {
                    binding.howToUseView.visibility = View.VISIBLE
                    binding.header.text = context!!.getString(R.string.how_to_use_this_app)

                }
            } else {
                if ((binding.howToUseView.visibility == View.VISIBLE)) {
                    binding.howToUseView.visibility = View.GONE
                }
            }
            if (binding.voiceCommandsSelector.isChecked) {
                if (binding.voiceCommandsView.visibility != View.VISIBLE) {
                    binding.voiceCommandsView.visibility = View.VISIBLE
                    binding.header.text = context!!.getString(R.string.voice_commands)

                }
            } else {
                if (binding.voiceCommandsView.visibility == View.VISIBLE) {
                    binding.voiceCommandsView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        var box = 0
        if(binding.appearanceSelector.isChecked){
            box = 2
        }
        else if(binding.voiceCommandsSelector.isChecked){
            box = 1
        }
        outState.putInt("BOX", box)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
