package com.happyhappyyay.android.dreamdiary.password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.android.dreamdiary.databinding.PasswordFragmentBinding

class PasswordFrag : Fragment() {

    private lateinit var viewModel: PasswordViewModel
    private var _binding: PasswordFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PasswordFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        if (!viewModel.hasPassword()) {
            binding.passwordEdit.visibility = View.GONE
            navigateToJournal()
        } else {
            binding.lock.setOnClickListener {
                if (viewModel.getPassword() == binding.passwordEdit.text.toString()) {
                    navigateToJournal()
                } else {
                    binding.lock.isChecked = false
                }
            }
        }
        return binding.root
    }

    private fun navigateToJournal(){
        this.findNavController()
            .navigate(PasswordFragDirections.actionPasswordFragToJournalFrag())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
