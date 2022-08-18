package dev.arogundade.wojak.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arogundade.wojak.databinding.EditPriceBinding

class EditPrice(
    private val onEdit: (String) -> Unit,
    private val default: String?
) : BottomSheetDialogFragment() {

    private lateinit var binding: EditPriceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditPriceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (default != null) {
                text.hint = default
                text.setText(default)
            }
            cancel.setOnClickListener {
                dismiss()
            }
            edit.setOnClickListener {
                val text = this.text.text.toString().trim()
                if (text.isNotEmpty()) {
                    onEdit(text)
                    dismiss()
                }
            }
            reset.apply {
                visibility = if (default == null) View.INVISIBLE else View.VISIBLE
                setOnClickListener {
                    if (default != null) {
                        binding.text.setText(default)
                    }
                }
            }
        }

    }

}