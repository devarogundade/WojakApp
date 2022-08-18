package dev.arogundade.wojak.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.TransactionItemBinding
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.utils.Extensions.isAirdrop
import dev.arogundade.wojak.utils.Extensions.isBought
import dev.arogundade.wojak.utils.Extensions.isReceived
import dev.arogundade.wojak.utils.Extensions.isSent
import dev.arogundade.wojak.utils.Extensions.isSold
import dev.arogundade.wojak.utils.Extensions.smartRound
import org.ocpsoft.prettytime.PrettyTime

class TransactionAdapter(
    private val prettyTime: PrettyTime,
    val onTransactionClick: (Transaction) -> Unit
) :
    RecyclerView.Adapter<TransactionAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(diffResult.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setTransactions(transactions: List<Transaction>) {
        diffResult.submitList(transactions)
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = TransactionItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(transaction: Transaction) {
            binding.apply {
                price.text = "$${smartRound(transaction.price)}"
                holdings.text = "${transaction.amount} ${transaction.currencyId}"
                date.text = prettyTime.format(transaction.date)

                when {
                    transaction.isBought() or transaction.isReceived() -> {
                        cardView.backgroundTintList = ContextCompat.getColorStateList(
                            itemView.context,
                            R.color.md_theme_light_pump
                        )
                        image.rotation = 135f
                    }
                    transaction.isSold() or transaction.isSent() -> {
                        cardView.backgroundTintList = ContextCompat.getColorStateList(
                            itemView.context,
                            R.color.md_theme_light_error
                        )
                        image.rotation = -45f
                    }
                    transaction.isAirdrop() -> {
                        cardView.backgroundTintList = ContextCompat.getColorStateList(
                            itemView.context,
                            R.color.md_theme_light_surfaceTint
                        )
                        image.rotation = 135f
                    }
                }

            }.root.setOnClickListener {
                onTransactionClick(transaction)
            }
        }


    }

}

