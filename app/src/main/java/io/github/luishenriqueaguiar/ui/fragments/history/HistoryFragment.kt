package io.github.luishenriqueaguiar.ui.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.luishenriqueaguiar.databinding.FragmentHistoryBinding
import io.github.luishenriqueaguiar.domain.model.HistoryFilter
import io.github.luishenriqueaguiar.ui.adapters.HistoryAdapter
import io.github.luishenriqueaguiar.R

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupFilterListeners()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupFilterListeners() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedFilter = when (checkedIds.firstOrNull()) {
                R.id.chip_today -> HistoryFilter.TODAY
                R.id.chip_month -> HistoryFilter.MONTH
                else -> HistoryFilter.WEEK
            }
            viewModel.onFilterSelected(selectedFilter)
        }
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            binding.textEmptyState.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
            historyAdapter.submitList(sessions)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}