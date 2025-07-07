package io.github.luishenriqueaguiar.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityMainBinding
import io.github.luishenriqueaguiar.ui.activities.focus.FocusSessionActivity
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
        setupFragmentResultListener()
    }

    private fun setupOnClickListeners() {
        binding.buttonStartSession.setOnClickListener {
            NewSessionBottomSheet().show(supportFragmentManager, NewSessionBottomSheet.TAG)
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener("session_created_key", this) { requestKey, bundle ->
            val sessionId = bundle.getString("sessionId")
            if (sessionId != null) {
                val intent = Intent(this, FocusSessionActivity::class.java).apply {
                    putExtra("SESSION_ID_EXTRA", sessionId)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }
}