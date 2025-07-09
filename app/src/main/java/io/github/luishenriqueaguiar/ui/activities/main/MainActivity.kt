package io.github.luishenriqueaguiar.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityMainBinding
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.ui.activities.focus.FocusSessionActivity
import io.github.luishenriqueaguiar.ui.fragments.dashboard.DashboardFragment
import io.github.luishenriqueaguiar.ui.fragments.history.HistoryFragment
import io.github.luishenriqueaguiar.ui.fragments.profile.ProfileFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        setupBottomNavListener()
        setupObservers()
    }

    private fun setupBottomNavListener() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(DashboardFragment())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun setupObservers() {
        viewModel.inProgressSession.observe(this) { session ->
            session?.let { showResumeSessionDialog(it) }
        }
    }

    private fun showResumeSessionDialog(session: Session) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sessão em Andamento")
            .setMessage("Você tem uma sessão de '${session.name}' que não foi finalizada. O que deseja fazer?")
            .setCancelable(false)
            .setPositiveButton("Continuar") { dialog, _ ->
                val intent = Intent(this, FocusSessionActivity::class.java).apply {
                    putExtra("SESSION_ID_EXTRA", session.id)
                }
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Abandonar") { dialog, _ ->
                viewModel.onAbandonSessionClicked(session)
                dialog.dismiss()
            }
            .show()
    }
}