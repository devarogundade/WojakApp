package dev.arogundade.wojak.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arogundade.wojak.databinding.TransactionInfoBinding
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.utils.Extensions.smartRound

class TransactionInfo(
    private val transaction: Transaction,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: TransactionInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TransactionInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            amount.text = smartRound(transaction.amount)
            price.text = smartRound(transaction.price)
            type.text = transaction.type
            timestamp.text = transaction.date.toString()

            cancel.setOnClickListener { dismiss() }

            delete.setOnClickListener {
                onDelete(transaction)
                dismiss()
            }

            edit.setOnClickListener {
                onEdit(transaction)
                dismiss()
            }
        }

    }

}