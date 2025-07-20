package io.github.luishenriqueaguiar.ui.activities.focus

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityFocusSessionBinding
import io.github.luishenriqueaguiar.services.FocusTimerService
import io.github.luishenriqueaguiar.ui.utils.OneTimeEvent
import io.github.luishenriqueaguiar.ui.utils.SessionEndEvent

@AndroidEntryPoint
class FocusSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFocusSessionBinding
    private val viewModel: FocusSessionViewModel by viewModels()
    private var sessionId: String? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as FocusTimerService.FocusTimerBinder
            viewModel.setService(binder.getService())
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.setService(null)
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, FocusTimerService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        viewModel.setService(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionId = intent.getStringExtra(FocusTimerService.EXTRA_SESSION_ID)
        if (sessionId == null) {
            Toast.makeText(this, "Erro: ID da sessão não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadAndObserveSession(sessionId!!)

        startFocusService()
        setupListeners()
        setupBackButtonHandler()
        setupObservers()

        Snackbar.make(binding.root, "Vire o celular para baixo para iniciar a sessão.", Snackbar.LENGTH_LONG).show()
    }

    private fun setupObservers() {
        viewModel.timerState.observe(this) { state ->
            binding.textTimer.text = state.timeDisplay
            binding.progressCircular.progress = state.progress
            binding.buttonPlayPause.isEnabled = true
            val iconRes = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            binding.buttonPlayPause.setIconResource(iconRes)
        }

        viewModel.sessionName.observe(this) { name ->
            binding.textSessionTitle.text = name
        }

        viewModel.oneTimeEvent.observe(this) { event ->
            event?.let {
                when(it) {
                    is OneTimeEvent.VibrateShort -> vibrateShort()
                    is OneTimeEvent.VibrateLong -> vibrateLong()
                }
                viewModel.onEventHandled()
            }
        }

        viewModel.sessionEndEvent.observe(this) { event ->
            event?.let {
                when (it) {
                    is SessionEndEvent.CloseScreen -> {
                        Snackbar.make(binding.root, "Sessão finalizada.", Snackbar.LENGTH_SHORT).show()
                        finish()
                    }
                }
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupListeners() {
        binding.buttonPlayPause.setOnClickListener {
            viewModel.onPlayPauseClicked()
        }

        binding.buttonStop.setOnClickListener {
            viewModel.onStopClicked()
            finish()
        }

        binding.arrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onStopClicked()
                finish()
            }
        })
    }

    private fun startFocusService() {
        val serviceIntent = Intent(this, FocusTimerService::class.java).apply {
            putExtra(FocusTimerService.EXTRA_SESSION_ID, sessionId)
        }
        startService(serviceIntent)
    }

    private fun vibrateShort() {
        val vibrator = getVibratorService()
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun vibrateLong() {
        val vibrator = getVibratorService()
        val pattern = longArrayOf(0, 400, 200, 400)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun getVibratorService(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
}