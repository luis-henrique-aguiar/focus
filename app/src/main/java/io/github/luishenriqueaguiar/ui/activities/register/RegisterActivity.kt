package io.github.luishenriqueaguiar.ui.activities.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityRegisterBinding
import io.github.luishenriqueaguiar.ui.activities.main.MainActivity

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
        setupTextWatchers()
        setupObservers()
    }

    private fun setupTextWatcher(inputField: TextInputEditText, updateFunc: (String) -> Unit) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateFunc(s.toString())
                viewModel.clearInputErrors()
            }
        })
    }

    private fun setupTextWatchers() {
        setupTextWatcher(binding.inputName) { viewModel.updateName(it) }
        setupTextWatcher(binding.inputEmail) { viewModel.updateEmail(it) }
        setupTextWatcher(binding.inputPassword) { viewModel.updatePassword(it) }
        setupTextWatcher(binding.inputConfirmPassword) { viewModel.updateConfirmPassword(it) }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.confirmPasswordError.observe(this) { error ->
            if (error != null) binding.inputConfirmPasswordContainer.error = error
        }

        viewModel.emailError.observe(this) { error ->
            if (error != null) binding.inputEmailContainer.error = error
        }

        viewModel.nameError.observe(this) { error ->
            if (error != null) binding.inputNameContainer.error = error
        }

        viewModel.generalError.observe(this) {  error ->
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }

        viewModel.passwordError.observe(this) { error ->
            if (error != null) binding.inputPasswordContainer.error = error
        }
    }

    private fun setupOnClickListeners() {
        binding.arrowBack.setOnClickListener {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        binding.registerButton.setOnClickListener {
            viewModel.onRegisterButtonClicked()
        }
    }
}