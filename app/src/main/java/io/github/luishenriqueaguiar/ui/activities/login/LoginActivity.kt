package io.github.luishenriqueaguiar.ui.activities.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding.arrowBack.setOnClickListener {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}