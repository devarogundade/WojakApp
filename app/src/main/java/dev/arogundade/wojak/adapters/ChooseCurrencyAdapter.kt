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
import dev.arogundade.wojak.databinding.ChooseCoinItemBinding
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.utils.Constants.HUNDRED
import dev.arogundade.wojak.utils.Extensions.format
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.Extensions.smartRound
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter

class ChooseCurrencyAdapter(
    private val requestManager: RequestManager,
    private val onChooseListener: OnChooseListener,
) :
    RecyclerView.Adapter<ChooseCurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.choose_coin_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(diffResult.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.price == newItem.price
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setCurrencies(currencies: List<Currency>) {
        diffResult.submitList(currencies)
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ChooseCoinItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(currency: Currency) {
            binding.apply {
                name.text = currency.name
                price.text = "$${currency.price?.let { smartRound(it.toDouble()) }}"
                symbol.text = "${currency.symbol}"
                rank.text = "${currency.rank}"

                val priceChangePercent24h =
                    (currency.oneDay?.price_change_pct?.toDouble() ?: 0.0) * HUNDRED

                when {
                    priceChangePercent24h > 0.0 -> {
                        binding.percentChange.apply {
                            text = "+%${priceChangePercent24h.format(2)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.md_theme_light_pump
                                )
                            )
                        }
                    }
                    priceChangePercent24h < 0.0 -> {
                        binding.percentChange.apply {
                            text = "-%${priceChangePercent24h.format(2, true)}"
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
                            text = "%${priceChangePercent24h.format(2)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.md_theme_light_onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                if (currency.logo_url != null && currency.logo_url.isSvg()) {
                    val uri = Uri.parse(currency.logo_url)
                    requestManager
                        .`as`(PictureDrawable::class.java)
                        .listener(SvgSoftwareLayerSetter())
                        .load(uri)
                        .into(image)
                } else {
                    requestManager
                        .load(currency.logo_url)
                        .into(image)
                }

                root.setOnClickListener {
                    onChooseListener.onClick(currency)
                }

                root.setOnLongClickListener {
                    onChooseListener.onHold(currency)
                    false
                }
            }
        }
    }

    interface OnChooseListener {
        fun onClick(currency: Currency)
        fun onHold(currency: Currency)
    }

}

