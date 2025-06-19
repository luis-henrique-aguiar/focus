package io.github.luishenriqueaguiar.ui.activities.register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.arrowBack.setOnClickListener {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}