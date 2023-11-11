package com.cuervolu.witcherscodex.ui.bestiary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.viewModels
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.databinding.FragmentCreateMonsterBinding
import com.cuervolu.witcherscodex.databinding.FragmentFactsBinding
import com.cuervolu.witcherscodex.ui.dashboard.FactsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMonsterFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = CreateMonsterFragment()
    }

    private val viewModel: CreateMonsterViewModel by viewModels()
    private lateinit var binding: FragmentCreateMonsterBinding
    private lateinit var spinner: Spinner
    private lateinit var type: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateMonsterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = binding.typesSpinner
        context?.let { ArrayAdapter.createFromResource(it, R.array.types_array, android.R.layout.simple_spinner_item) }.also {
            it?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        type = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }


}