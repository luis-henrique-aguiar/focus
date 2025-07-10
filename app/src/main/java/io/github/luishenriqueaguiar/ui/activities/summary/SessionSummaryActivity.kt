package io.github.luishenriqueaguiar.ui.activities.summary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.databinding.ActivitySessionSummaryBinding

@AndroidEntryPoint
class SessionSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionSummaryBinding
    private val viewModel: SessionSummaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionId = intent.getStringExtra("SESSION_ID_EXTRA")
        if (sessionId == null) {
            finish()
            return
        }
        viewModel.loadSummary(sessionId)
        setupToolbar()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        binding.buttonDone.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.sessionName.observe(this) { binding.textSessionName.text = it }
        viewModel.focusTimeText.observe(this) { binding.textFocusTime.text = it }
        viewModel.interruptionsText.observe(this) { binding.textInterruptions.text = it }
        viewModel.insightMessage.observe(this) { binding.textInsightMessage.text = it }
        viewModel.pauseTimeText.observe(this) { binding.textPauseTime.text = it }
    }
}