package io.github.luishenriqueaguiar.ui.activities.focus

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.databinding.ActivityFocusSessionBinding

@AndroidEntryPoint
class FocusSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFocusSessionBinding
    private val viewModel: FocusSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionId = intent.getStringExtra("SESSION_ID_EXTRA")
        if (sessionId == null) {
            Snackbar.make(binding.root, "Erro: ID da sessão não encontrado", Snackbar.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadSession(sessionId)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.session.observe(this) { session ->
            binding.textSessionTitle.text = session.name
        }

        viewModel.timeDisplay.observe(this) { time ->
            binding.textTimer.text = time
        }
    }
}