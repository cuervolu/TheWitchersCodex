package com.cuervolu.witcherscodex.ui.bestiary

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.adapters.BestiaryAdapter
import com.cuervolu.witcherscodex.databinding.ActivityBestiaryBinding
import com.github.ybq.android.spinkit.SpinKitView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestiaryActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context): Intent =
            Intent(context, BestiaryActivity::class.java)
    }

    private lateinit var binding: ActivityBestiaryBinding
    private val bestiaryViewModel: BestiaryViewModel by viewModels()
    private lateinit var bestiaryAdapter: BestiaryAdapter
    private lateinit var spinKitView: SpinKitView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBestiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spinKitView = findViewById(R.id.spin_kit)
        spinKitView.visibility = View.VISIBLE // Muestra el indicador de carga al inicio


        // Configura el RecyclerView y su adaptador
        bestiaryAdapter = BestiaryAdapter(emptyList()) { /* Lógica de clic en el elemento */ }
        binding.rwBeastiary.layoutManager = LinearLayoutManager(this)
        binding.rwBeastiary.adapter = bestiaryAdapter

        // Observa los cambios en la lista de bestias y actualiza el adaptador
        bestiaryViewModel.bestiaryList.observe(this, Observer { bestiaryList ->
            bestiaryAdapter.monsters = bestiaryList
            bestiaryAdapter.notifyDataSetChanged()
        })

        bestiaryViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                spinKitView.visibility = View.VISIBLE // Muestra el indicador de carga
            } else {
                spinKitView.visibility = View.GONE // Oculta el indicador de carga
            }
        })

        bestiaryViewModel.loadBestiaryEntries()
    }
}