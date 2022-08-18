package dev.arogundade.wojak.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arogundade.wojak.databinding.DeleteConfimationBinding

class ConfirmationDelete(val onDelete: () -> Unit, private val onCancel: () -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: DeleteConfimationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeleteConfimationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cancel.setOnClickListener {
                onCancel()
                dismiss()
            }
            delete.setOnClickListener {
                onDelete()
                dismiss()
            }
        }

    }

}