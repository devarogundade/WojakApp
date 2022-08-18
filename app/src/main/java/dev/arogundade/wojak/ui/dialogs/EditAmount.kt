package dev.arogundade.wojak.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arogundade.wojak.databinding.EditAmountBinding
import dev.arogundade.wojak.models.Portfolio

class EditAmount(
    private val onEdit: (Portfolio, Double) -> Unit,
    private val onDelete: (Portfolio) -> Unit,
    private val portfolio: Portfolio
) : BottomSheetDialogFragment() {

    private lateinit var binding: EditAmountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditAmountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val target = portfolio.goalAmount ?: 0.0
            text.hint = target.toString()
            text.setText(target.toString())

            cancel.setOnClickListener {
                dismiss()
            }
            edit.setOnClickListener {
                val text = this.text.text.toString().trim()
                if (text.isNotEmpty()) {
                    if (text.toDouble() > 0.0) {
                        onEdit(portfolio, text.toDouble())
                        dismiss()
                    }
                }
            }
            delete.setOnClickListener {
                onDelete(portfolio)
                dismiss()
            }
        }

    }

}