package io.github.luishenriqueaguiar.ui.activities.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityLoginBinding
import io.github.luishenriqueaguiar.ui.activities.initial.InitialActivity

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
            startActivity(Intent(this, InitialActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}