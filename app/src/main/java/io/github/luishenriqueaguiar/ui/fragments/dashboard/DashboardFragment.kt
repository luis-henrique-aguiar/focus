package io.github.luishenriqueaguiar.ui.fragments.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.FragmentDashboardBinding
import io.github.luishenriqueaguiar.ui.activities.focus.FocusSessionActivity
import io.github.luishenriqueaguiar.ui.dialogs.newsession.NewSessionBottomSheet

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupFragmentResultListener()
        setupDashboardObservers()
    }

    private fun setupClickListeners() {
        binding.buttonStartSession.setOnClickListener {
            NewSessionBottomSheet().show(childFragmentManager, NewSessionBottomSheet.TAG)
        }
    }

    private fun setupDashboardObservers() {
        viewModel.greetingText.observe(viewLifecycleOwner) { greeting ->
            binding.textGreeting.text = greeting
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            Glide.with(this)
                .load(user?.profilePhoto)
                .placeholder(R.drawable.user_empty)
                .error(R.drawable.user_empty)
                .into(binding.profileImage)
        }

        viewModel.todayStatsText.observe(viewLifecycleOwner) { stats ->
            binding.textStatsToday.text = stats
        }

        viewModel.weeklyStreakText.observe(viewLifecycleOwner) { stats ->
            binding.textStatsStreak.text = stats
        }

        viewModel.weeklyAverageText.observe(viewLifecycleOwner) { stats ->
            binding.textStatsAverage.text = stats
        }
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener("session_created_key", this) { _, bundle ->
            val sessionId = bundle.getString("sessionId")
            if (sessionId != null) {
                val intent = Intent(requireActivity(), FocusSessionActivity::class.java).apply {
                    putExtra("SESSION_ID_EXTRA", sessionId)
                }
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}