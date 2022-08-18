package dev.arogundade.wojak.ui

import android.view.View
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.EmptyItemBinding

class EmptyItem(private val binding: EmptyItemBinding, private val onAction: () -> Unit) {

    fun setText(actionText: String, message: String, image: Int = R.drawable.empty) {
        binding.apply {
            this.image.setImageResource(image)
            this.message.text = message
            this.add.apply {
                text = actionText
                setOnClickListener { onAction() }
            }
        }
    }

    fun show() {
        if (!isShowing()) {
            binding.root.visibility = View.VISIBLE
        }
    }

    fun hide() {
        if (isShowing()) {
            binding.root.visibility = View.GONE
        }
    }

    private fun isShowing(): Boolean =
        binding.root.visibility == View.VISIBLE

}