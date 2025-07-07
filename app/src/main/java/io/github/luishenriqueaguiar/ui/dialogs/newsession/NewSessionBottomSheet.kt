package io.github.luishenriqueaguiar.ui.dialogs.newsession

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.databinding.DialogNewSessionBinding

@AndroidEntryPoint
class NewSessionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogNewSessionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewSessionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogNewSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.inputSessionName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSessionNameChanged(s.toString())
            }
        })

        binding.sliderFocusDuration.addOnChangeListener { _, value, _ ->
            viewModel.onFocusDurationChanged(value)
        }

        binding.sliderBreakDuration.addOnChangeListener { _, value, _ ->
            viewModel.onBreakDurationChanged(value)
        }

        binding.buttonStartSession.setOnClickListener {
            viewModel.onStartSessionClicked()
        }
    }

    private fun setupObservers() {
        viewModel.focusDurationText.observe(viewLifecycleOwner) { text ->
            binding.labelFocusDuration.text = text
        }

        viewModel.breakDurationText.observe(viewLifecycleOwner) { text ->
            binding.labelBreakDuration.text = text
        }

        viewModel.sessionNameError.observe(viewLifecycleOwner) { error ->
            binding.inputSessionNameContainer.error = error
        }

        viewModel.generalError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.navigateToFocusSession.observe(viewLifecycleOwner) { session ->
            if (session != null) {
                val resultBundle = bundleOf("sessionId" to session.id)
                setFragmentResult("session_created_key", resultBundle)
                dismiss()
                viewModel.onNavigationHandled()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NewSessionBottomSheet"
    }
}