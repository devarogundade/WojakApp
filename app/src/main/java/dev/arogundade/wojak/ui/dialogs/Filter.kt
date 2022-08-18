package dev.arogundade.wojak.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.FilterBinding
import dev.arogundade.wojak.models.FilterResult
import dev.arogundade.wojak.utils.Extensions.isLossOnly
import dev.arogundade.wojak.utils.Extensions.isProfitOnly

class Filter(
    private val filterResult: FilterResult,
    private val onFilter: (FilterResult) -> Unit
) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            when {
                filterResult.isProfitOnly() -> radioGroup.check(R.id.profit)
                filterResult.isLossOnly() -> radioGroup.check(R.id.loss)
                else -> radioGroup.check(R.id.all)
            }

            hasGoal.isChecked = filterResult.hasGoal

            cancel.setOnClickListener {
                dismiss()
            }

            done.setOnClickListener {
                val result = FilterResult(
                    sort = when (radioGroup.checkedRadioButtonId) {
                        R.id.all -> 0
                        R.id.profit -> 1
                        else -> 2
                    },
                    hasGoal.isChecked
                )

                onFilter(result)

                dismiss()
            }
        }

    }

}