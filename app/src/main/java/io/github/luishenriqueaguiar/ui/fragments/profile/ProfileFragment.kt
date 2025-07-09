package io.github.luishenriqueaguiar.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.FragmentProfileBinding
import io.github.luishenriqueaguiar.ui.activities.initial.InitialActivity

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.onProfileImageSelected(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            viewModel.onLogoutClicked()
        }

        binding.profileImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonEditName.setOnClickListener {
            showEditNameDialog()
        }

        binding.buttonChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), "Tela de Alterar Senha em breve!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textUserName.text = it.name ?: "Usuário"
                binding.textUserEmail.text = it.email

                Glide.with(this)
                    .load(it.profilePhoto)
                    .placeholder(R.drawable.user_empty)
                    .error(R.drawable.user_empty)
                    .into(binding.profileImage)
            }
        }

        viewModel.logoutComplete.observe(viewLifecycleOwner) { event ->
            event?.let {
                val intent = Intent(requireActivity(), InitialActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }

        viewModel.updateMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onUpdateMessageShown()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEditNameDialog() {
        val editText = EditText(requireContext()).apply {
            setText(viewModel.user.value?.name)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Alterar Nome")
            .setMessage("Digite seu novo nome de exibição.")
            .setView(editText)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = editText.text.toString()
                viewModel.onUpdateName(newName)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}