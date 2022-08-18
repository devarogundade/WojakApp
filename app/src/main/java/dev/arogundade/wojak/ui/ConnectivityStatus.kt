package dev.arogundade.wojak.ui

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.ConnectivityStatusBinding
import dev.arogundade.wojak.networking.observer.ConnectivityObserver

class ConnectivityStatus(
    private val binding: ConnectivityStatusBinding
) {

    fun setStatus(status: ConnectivityObserver.Status) {
        val text: String
        val textBackground: Int
        val textColor: Int
        val context = binding.root.context
        val duration: StatusDuration

        when (status) {
            ConnectivityObserver.Status.Available -> {
                text = "Available"
                textBackground = ContextCompat.getColor(context, R.color.md_theme_light_pump)
                textColor = ContextCompat.getColor(context, R.color.white)
                duration = StatusDuration.TEN_SECOND
            }
            ConnectivityObserver.Status.Losing -> {
                text = "Losing connection"
                textBackground = ContextCompat.getColor(context, R.color.md_theme_light_warning)
                textColor = ContextCompat.getColor(context, R.color.white)
                duration = StatusDuration.UNTIL_CHANGE
            }
            ConnectivityObserver.Status.Lost -> {
                text = "Connection lost"
                textBackground = ContextCompat.getColor(context, R.color.md_theme_light_error)
                textColor = ContextCompat.getColor(context, R.color.white)
                duration = StatusDuration.TEN_SECOND
            }
            ConnectivityObserver.Status.Unavailable -> {
                text = "Unavailable"
                textBackground = ContextCompat.getColor(context, R.color.md_theme_light_outline)
                textColor =
                    ContextCompat.getColor(context, R.color.md_theme_dark_onSecondaryContainer)
                duration = StatusDuration.UNTIL_CHANGE
            }
        }

        binding.root.apply {
            this.text = text
            setTextColor(textColor)
            setBackgroundColor(textBackground)
            visibility = View.VISIBLE
        }

        if (duration == StatusDuration.TEN_SECOND) {
            Handler().postDelayed({
                binding.root.visibility = View.GONE
            }, 10000)
        }
    }

    enum class StatusDuration {
        TEN_SECOND,
        UNTIL_CHANGE
    }

}