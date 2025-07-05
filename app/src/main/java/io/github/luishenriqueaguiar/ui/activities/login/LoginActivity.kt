package io.github.luishenriqueaguiar.ui.activities.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityLoginBinding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListener()
        setupTextWatchers()
        setupObservers()
    }

    private fun setupTextWatcher(input: TextInputEditText, updateFunc: (String) -> Unit) {
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateFunc(s.toString())
            }
        })
    }

    private fun setupTextWatchers() {
        setupTextWatcher(binding.inputEmail) { viewModel.updateEmail(it) }
        setupTextWatcher(binding.inputPassword) { viewModel.updatePassword(it) }
    }

    private fun setupObservers() {

    }

    private fun setupOnClickListener() {
        binding.arrowBack.setOnClickListener {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        binding.loginButton.setOnClickListener {
            viewModel.onLoginButtonClicked()
        }
    }
}