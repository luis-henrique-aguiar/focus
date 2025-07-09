package io.github.luishenriqueaguiar.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.luishenriqueaguiar.R
import io.github.luishenriqueaguiar.databinding.ItemSessionHistoryBinding
import io.github.luishenriqueaguiar.domain.model.Session
import io.github.luishenriqueaguiar.domain.model.SessionStatus
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<Session, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSessionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val session = getItem(position)
        holder.bind(session)
    }

    inner class HistoryViewHolder(private val binding: ItemSessionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {
            binding.textSessionName.text = session.name

            val durationInMinutes = session.actualStudyDurationInSeconds / 60
            binding.textSessionDuration.text = "$durationInMinutes min"

            session.startTime?.let {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.textSessionDate.text = formatter.format(it)
            }

            when (session.status) {
                SessionStatus.COMPLETED -> {
                    binding.iconStatus.setImageResource(R.drawable.ic_check_circle)
                    binding.iconStatus.imageTintList = binding.root.context.getColorStateList(R.color.green)
                }
                SessionStatus.ABANDONED -> {
                    binding.iconStatus.setImageResource(R.drawable.ic_cancel)
                    binding.iconStatus.imageTintList = binding.root.context.getColorStateList(R.color.red)
                }
                else -> {

                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Session, newItem: Session) = oldItem == newItem
    }
}