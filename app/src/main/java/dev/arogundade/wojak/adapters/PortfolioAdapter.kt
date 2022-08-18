package dev.arogundade.wojak.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.PortfolioTemBinding
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.utils.Constants.HUNDRED
import dev.arogundade.wojak.utils.Extensions.format
import dev.arogundade.wojak.utils.Extensions.holdings
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.Extensions.smartRound
import dev.arogundade.wojak.utils.Extensions.spent
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter


class PortfolioAdapter(
    private val requestManager: RequestManager,
    private val onCurrencyClick: (PortfolioCurrencyTransactions) -> Unit
) :
    RecyclerView.Adapter<PortfolioAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.portfolio_tem, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
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
            return oldItem.currency.id == newItem.currency.id
        }

        override fun areContentsTheSame(
            oldItem: PortfolioCurrencyTransactions,
            newItem: PortfolioCurrencyTransactions
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setCurrencies(currencies: List<PortfolioCurrencyTransactions>) {
        diffResult.submitList(currencies)
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = PortfolioTemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(portfolioCurrencyTransactions: PortfolioCurrencyTransactions) {

            val holdings = portfolioCurrencyTransactions.transactions.holdings()

            val portfolioPrice = portfolioCurrencyTransactions.transactions.spent()

            val marketPrice =
                (portfolioCurrencyTransactions.currency.price?.toDouble() ?: 0.0) * holdings

            val priceDifference = ((marketPrice - portfolioPrice) / marketPrice) * HUNDRED

            binding.apply {
                name.text = portfolioCurrencyTransactions.currency.name
                price.text = "$${smartRound(marketPrice)}"
                this.holdings.text =
                    "${smartRound(holdings)} ${portfolioCurrencyTransactions.currency.symbol}"

                when {
                    priceDifference > 0.0 -> {
                        binding.percentChange.apply {
                            text = "+%${priceDifference.format(2)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.md_theme_light_pump
                                )
                            )
                        }
                    }
                    priceDifference < 0.0 -> {
                        binding.percentChange.apply {
                            text = "-%${priceDifference.format(2, true)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.md_theme_light_error
                                )
                            )
                        }
                    }
                    else -> {
                        binding.percentChange.apply {
                            text = "%${priceDifference.format(null)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.md_theme_light_onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                if (portfolioCurrencyTransactions.currency.logo_url != null && portfolioCurrencyTransactions.currency.logo_url.isSvg()) {
                    val uri = Uri.parse(portfolioCurrencyTransactions.currency.logo_url)
                    requestManager
                        .`as`(PictureDrawable::class.java)
                        .listener(SvgSoftwareLayerSetter())
                        .load(uri)
                        .into(image)
                } else {
                    requestManager
                        .load(portfolioCurrencyTransactions.currency.logo_url)
                        .into(image)
                }

            }.root.setOnClickListener {
                onCurrencyClick(portfolioCurrencyTransactions)
            }
        }


    }

}

