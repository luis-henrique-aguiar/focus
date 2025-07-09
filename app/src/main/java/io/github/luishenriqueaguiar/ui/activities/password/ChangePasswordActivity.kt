package io.github.luishenriqueaguiar.ui.activities.password

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityChangePasswordBinding

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.buttonSavePassword.setOnClickListener {
            val currentPass = binding.inputCurrentPassword.text.toString()
            val newPass = binding.inputNewPassword.text.toString()
            val confirmPass = binding.inputConfirmPassword.text.toString()
            viewModel.onSaveChangesClicked(currentPass, newPass, confirmPass)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "Falha: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}