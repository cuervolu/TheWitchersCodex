package com.cuervolu.witcherscodex.ui.bestiary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.databinding.FragmentCreateMonsterBinding
import com.cuervolu.witcherscodex.databinding.FragmentFactsBinding
import com.cuervolu.witcherscodex.ui.dashboard.FactsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMonsterFragment : Fragment() {

    companion object {
        fun newInstance() = CreateMonsterFragment()
    }

    private val viewModel: CreateMonsterViewModel by viewModels()
    private lateinit var binding: FragmentCreateMonsterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMonsterBinding.inflate(inflater, container, false)
        return binding.root
    }



}