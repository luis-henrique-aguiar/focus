package io.github.luishenriqueaguiar.ui.activities.focus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
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
        setupOnClickListeners()
        setupObservers()
        setupBackButtonHandler()
    }

    private fun setupObservers() {
        viewModel.session.observe(this) { session ->
            binding.textSessionTitle.text = session.name
        }

        viewModel.timeDisplay.observe(this) { time ->
            binding.textTimer.text = time
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            binding.buttonPlayPause.setIconResource(iconRes)
        }

        viewModel.progress.observe(this) { progress ->
            binding.progressCircular.progress = progress
        }

        viewModel.sessionFinishedEvent.observe(this) { event ->
            event?.let {
                Toast.makeText(this, "Sessão finalizada.", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.buttonPlayPause.setOnClickListener {
            viewModel.onPlayPauseClicked()
        }

        binding.buttonReset.setOnClickListener {
            viewModel.onResetClicked()
        }

        binding.buttonStop.setOnClickListener {
            viewModel.onStopClicked()
        }

        binding.arrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onStopClicked()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }
}