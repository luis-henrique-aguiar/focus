package io.github.luishenriqueaguiar.ui.activities.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.databinding.ActivityMainBinding
import io.github.luishenriqueaguiar.ui.dialogs.newsession.NewSessionBottomSheet

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.buttonStartSession.setOnClickListener {
            NewSessionBottomSheet().show(supportFragmentManager, NewSessionBottomSheet.TAG)
        }
    }
}