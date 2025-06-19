package io.github.luishenriqueaguiar.ui.activities.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityRegisterBinding
import io.github.luishenriqueaguiar.ui.activities.initial.InitialActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.arrowBack.setOnClickListener {
            startActivity(Intent(this, InitialActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}