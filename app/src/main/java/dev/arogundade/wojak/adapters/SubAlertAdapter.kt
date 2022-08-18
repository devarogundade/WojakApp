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
import dev.arogundade.wojak.databinding.AlertSubItemBinding
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.utils.Extensions
import dev.arogundade.wojak.utils.Extensions.isWatchingPump
import org.ocpsoft.prettytime.PrettyTime

class SubAlertAdapter(
    private val prettyTime: PrettyTime,
    private val onSwitch: (Alert, Boolean) -> Unit,
    private val onHold: (Alert) -> Unit
) :
    RecyclerView.Adapter<SubAlertAdapter.SubAlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAlertViewHolder {
        return SubAlertViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.alert_sub_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SubAlertViewHolder, position: Int) {
        holder.bind(diffResult.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(
            oldItem: Alert,
            newItem: Alert
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Alert,
            newItem: Alert
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val diffResult = AsyncListDiffer(this, diffUtil)

    fun setAlerts(alerts: List<Alert>) {
        diffResult.submitList(alerts)
    }

    inner class SubAlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = AlertSubItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(alert: Alert) {
            binding.apply {
                price.text = "$${Extensions.smartRound(alert.target)}"
                date.text = prettyTime.format(alert.date)
                switchButton.isChecked = alert.active

                if (alert.isWatchingPump()) {
                    image.setImageResource(R.drawable.ic_baseline_trending_up_24)
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.md_theme_light_pump
                        )
                    )
                } else {
                    image.setImageResource(R.drawable.ic_baseline_trending_down_24)
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.md_theme_light_error
                        )
                    )
                }

                switchButton.setOnCheckedChangeListener { _, b ->
                    onSwitch(alert, b)
                }
            }.root.setOnLongClickListener {
                onHold(alert)
                false
            }
        }

    }

}

