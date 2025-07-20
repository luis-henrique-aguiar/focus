package io.github.luishenriqueaguiar.ui.activities.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ActivityRegisterBinding
import io.github.luishenriqueaguiar.ui.activities.main.MainActivity

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var gallery: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
        setupGalleryPicker()
        setupTextWatchers()
        setupObservers()
    }

    private fun setupTextWatcher(inputField: TextInputEditText, updateFunc: (String) -> Unit) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateFunc(s.toString())
            }
        })
    }

    private fun setupTextWatchers() {
        setupTextWatcher(binding.inputName) { viewModel.updateName(it) }
        setupTextWatcher(binding.inputEmail) { viewModel.updateEmail(it) }
        setupTextWatcher(binding.inputPassword) { viewModel.updatePassword(it) }
        setupTextWatcher(binding.inputConfirmPassword) { viewModel.updateConfirmPassword(it) }
    }

    private fun setupGalleryPicker() {
        gallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.updateProfileImageUri(uri)
            } else {
                Snackbar.make(binding.root, "Nenhuma foto selecionada", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                Snackbar.make(binding.root, "Cadastro realizado com sucesso!", Snackbar.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.profileImageUri.observe(this) { uri ->
            if (uri != null) {
                Glide.with(this).load(uri).into(binding.profileImage)
            }
        }

        viewModel.confirmPasswordError.observe(this) { error ->
            binding.inputConfirmPasswordContainer.error = error
        }

        viewModel.emailError.observe(this) { error ->
            binding.inputEmailContainer.error = error
        }

        viewModel.nameError.observe(this) { error ->
            binding.inputNameContainer.error = error
        }

        viewModel.generalError.observe(this) {  error ->
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }

        viewModel.passwordError.observe(this) { error ->
            binding.inputPasswordContainer.error = error
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

        binding.editIcon.setOnClickListener {
            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}