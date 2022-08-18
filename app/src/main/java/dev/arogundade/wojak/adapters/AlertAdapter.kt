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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.AlertItemBinding
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.CurrencyAlerts
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import dev.arogundade.wojak.utils.WojakItemDecoration
import org.ocpsoft.prettytime.PrettyTime

class AlertAdapter(
    private val prettyTime: PrettyTime,
    private val requestManager: RequestManager,
    private val onSwitch: (Alert, Currency, Boolean) -> Unit,
    private val onHold: (Alert) -> Unit,
) :
    RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        return AlertViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.alert_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(diffResult.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CurrencyAlerts>() {
        override fun areItemsTheSame(
            oldItem: CurrencyAlerts,
            newItem: CurrencyAlerts
        ): Boolean {
            return oldItem.currency.id == newItem.currency.id
        }

        override fun areContentsTheSame(
            oldItem: CurrencyAlerts,
            newItem: CurrencyAlerts
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setCurrencyAlerts(currencyAlerts: List<CurrencyAlerts>) {
        diffResult.submitList(currencyAlerts)
    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = AlertItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(currencyAlert: CurrencyAlerts) {
            val alertsAdapter = SubAlertAdapter(prettyTime, { alert, b ->
                onSwitch(alert, currencyAlert.currency, b)
            }, { alert -> onHold(alert) })

            binding.apply {
                name.text = currencyAlert.currency.name

                if (currencyAlert.currency.logo_url != null && currencyAlert.currency.logo_url.isSvg()) {
                    val uri = Uri.parse(currencyAlert.currency.logo_url)
                    requestManager
                        .`as`(PictureDrawable::class.java)
                        .listener(SvgSoftwareLayerSetter())
                        .load(uri)
                        .into(image)
                } else {
                    requestManager
                        .load(currencyAlert.currency.logo_url)
                        .into(image)
                }

                alerts.apply {
                    adapter = alertsAdapter
                    layoutManager = LinearLayoutManager(itemView.context)
                    addItemDecoration(
                        WojakItemDecoration(
                            height = 1,
                            color = ContextCompat.getColor(
                                itemView.context,
                                R.color.md_theme_light_outline
                            )
                        )
                    )
                }
            }

            alertsAdapter.setAlerts(currencyAlert.alerts)
        }

    }

}

