package dev.arogundade.wojak.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.GoalItemBinding
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.utils.Extensions.holdings


class GoalAdapter(
    private val onGoalClick: (PortfolioCurrencyTransactions) -> Unit
) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.goal_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(diffResult.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<PortfolioCurrencyTransactions>() {
        override fun areItemsTheSame(
            oldItem: PortfolioCurrencyTransactions,
            newItem: PortfolioCurrencyTransactions
        ): Boolean {
            return oldItem.portfolio.id == newItem.portfolio.id
        }

        override fun areContentsTheSame(
            oldItem: PortfolioCurrencyTransactions,
            newItem: PortfolioCurrencyTransactions
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setGoals(portfolioCurrencyTransactions: List<PortfolioCurrencyTransactions>) {
        diffResult.submitList(portfolioCurrencyTransactions)
    }

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = GoalItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(portfolioCurrencyTransactions: PortfolioCurrencyTransactions) {

            val holdings = portfolioCurrencyTransactions.transactions.holdings()

            val progress =
                (holdings / (portfolioCurrencyTransactions.portfolio.goalAmount ?: 0.0)) * 100

            binding.apply {
                title.text = portfolioCurrencyTransactions.portfolio.goalTitle
                from.text = "$holdings ${portfolioCurrencyTransactions.portfolio.id}"
                to.text =
                    "${portfolioCurrencyTransactions.portfolio.goalAmount} ${portfolioCurrencyTransactions.portfolio.id}"
                this.progress.progress = progress.toInt()
                edit.setOnClickListener {
                    onGoalClick(portfolioCurrencyTransactions)
                }
            }.root.setOnClickListener {

            }
        }

    }

}

