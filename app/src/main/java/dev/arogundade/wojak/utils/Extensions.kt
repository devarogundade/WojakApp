package dev.arogundade.wojak.utils

import android.app.Dialog
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.ChartData
import dev.arogundade.wojak.models.FilterResult
import dev.arogundade.wojak.models.Transaction
import java.text.DecimalFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object Extensions {

    fun Alert.isWatchingPump(): Boolean {
        return watchType == 1
    }

    fun String.isSvg(): Boolean {
        return this.lowercase(Locale.ENGLISH).endsWith(".svg")
    }

    fun List<Alert>.ids(): String {
        val sb = StringBuilder()

        if (isEmpty()) return ""

        for (alert in this) {
            sb.append("${alert.currencyId}, ")
        }
        return sb.toString()
    }

    fun Double.format(digits: Int?, alwaysPositive: Boolean = false): String {
        val count: Int = digits
            ?: when (this) {
                in Double.MIN_VALUE..0.000000001 -> 9
                in Double.MIN_VALUE..0.000001 -> 8
                in Double.MIN_VALUE..0.0001 -> 7
                in Double.MIN_VALUE..0.001 -> 6
                in Double.MIN_VALUE..0.01 -> 5
                in Double.MIN_VALUE..0.1 -> 4
                in Double.MIN_VALUE..2.0 -> 3
                else -> 2
            }

        val decimalFormat = DecimalFormat().apply {
            minimumFractionDigits = count
            maximumFractionDigits = count
        }

        if (alwaysPositive) {
            return decimalFormat.format(this.toPositive())
        }

        return decimalFormat.format(this)
    }

    fun smartRound(value: Double, alwaysPositive: Boolean = false): String {
        if (alwaysPositive) {
            return value.toPositive().prettyCount()
        }
        return value.prettyCount()
    }

    fun Double.toPositive(): Double {
        return if (this < 0.0) this * -1
        else this
    }

    fun expand(view: View, dialog: Dialog, activity: FragmentActivity, lock: Boolean = false) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // cast to bottom sheet type
                dialog as BottomSheetDialog
                val bottomSheet =
                    dialog.findViewById<View>(
                        com.google.android.material.R.id.design_bottom_sheet
                    ) as FrameLayout

                // full height
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    isDraggable = !lock
                }

                val viewGroupLayoutParams = bottomSheet.layoutParams

                // screen height
                viewGroupLayoutParams.height = activity.window?.decorView?.measuredHeight ?: 0
                bottomSheet.layoutParams = viewGroupLayoutParams
            }
        })
    }

    private fun Double.prettyCount(): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val value = floor(log10(this)).toInt()
        val base = value / 3
        return if (value >= 6 && base < suffix.size) {
            (this / 10.0.pow((base * 3).toDouble())).format(null) + suffix[base]
        } else {
            format(null)
        }
    }

    fun Transaction.isBought(): Boolean {
        return type == "bought"
    }

    fun Transaction.isSold(): Boolean {
        return type == "sold"
    }

    fun Transaction.isReceived(): Boolean {
        return type == "received"
    }

    fun Transaction.isSent(): Boolean {
        return type == "sent"
    }

    fun Transaction.isAirdrop(): Boolean {
        return type == "airdrop"
    }

    fun List<List<Transaction>>.toChartDataCollection(): List<ChartData> {
        val result = ArrayList<ChartData>()
        var prevChartData: ChartData? = null

        this.forEach { transactions ->
            val chartData = transactions.getChartData()
            val currentChartData = chartData.copy(
                holding = chartData.holding + (prevChartData?.holding ?: 0.0),
                price = chartData.price + (prevChartData?.price ?: 0.0)
            )
            result.add(currentChartData)
            prevChartData = currentChartData
        }

        return result
    }

    private fun List<Transaction>.getChartData(): ChartData {
        var price = 0.0
        var holding = 0.0

        this.forEach { transaction ->
            price += (transaction.price * transaction.amount)
            holding += transaction.amount
        }

        return ChartData(
            price = price,
            holding = holding
        )
    }

    fun List<Transaction>.holdings(): Double {
        var holding = 0.0

        this.forEach { transaction ->
            holding += transaction.amount
        }

        return holding
    }

    fun List<Transaction>.spent(): Double {
        var price = 0.0

        this.forEach { transaction ->
            price += (transaction.price * transaction.amount)
        }

        return price
    }

    fun FilterResult.isAll(): Boolean = sort == 0
    fun FilterResult.isProfitOnly(): Boolean = sort == 1
    fun FilterResult.isLossOnly(): Boolean = sort == 2
    fun FilterResult.isFiltering(): Boolean {
        if (hasGoal) return true
        return !isAll()
    }

    fun FilterResult.count(): Int {
        if (!isFiltering()) return 0

        var count = 0
        if (!isAll()) count++
        if (hasGoal) count++
        return count
    }
}