package dev.arogundade.wojak.ui.transaction

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.FragmentNewTransactionBinding
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.ui.dialogs.EditPrice
import dev.arogundade.wojak.utils.Extensions
import dev.arogundade.wojak.utils.Extensions.isAirdrop
import dev.arogundade.wojak.utils.Extensions.isBought
import dev.arogundade.wojak.utils.Extensions.isReceived
import dev.arogundade.wojak.utils.Extensions.isSent
import dev.arogundade.wojak.utils.Extensions.isSold
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.Extensions.toPositive
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewTransactionFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var currency: Currency
    private var transaction: Transaction? = null

    private lateinit var binding: FragmentNewTransactionBinding
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currency = it.getSerializable("currency") as Currency
            transaction = it.getSerializable("transaction") as Transaction?
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHoldings(currency)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewTransactionBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setTransaction(transaction: Transaction?) {
        this.transaction = transaction
        val price = currency.price?.toDouble() ?: 0.0
        binding.price.text = "$${Extensions.smartRound(price)}"
        if (transaction == null) return
        binding.apply {
            when {
                transaction.isBought() -> chipGroup.check(R.id.bought)
                transaction.isAirdrop() -> chipGroup.check(R.id.airdrop)
                transaction.isSent() -> chipGroup.check(R.id.sent)
                transaction.isSold() -> chipGroup.check(R.id.sold)
                transaction.isReceived() -> chipGroup.check(R.id.received)
            }

            amount.setText(transaction.amount.toPositive().toString())
            this.price.text = transaction.price.toString()
            addTransaction.text = getString(R.string.edit_transaction)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var transactionPrice: Double

        setTransaction(transaction)

        binding.apply {
            name.text = currency.name
            val price = currency.price?.toDouble() ?: 0.0
            transactionPrice = price
            this.price.setOnClickListener {
                EditPrice(
                    { price ->
                        binding.price.text = "$${Extensions.smartRound(price.toDouble())}"
                        transactionPrice = price.toDouble()
                    },
                    if (transaction != null) transaction!!.price.toString()
                    else price.toString()
                ).show(childFragmentManager, "childFragmentManager")
            }

            if (currency.logo_url != null && currency.logo_url!!.isSvg()) {
                val uri = Uri.parse(currency.logo_url)
                requestManager
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                    .load(uri)
                    .into(image)
            } else {
                requestManager
                    .load(currency.logo_url)
                    .into(image)
            }

            addTransaction.setOnClickListener {
                if (amount.text.isEmpty()) {
                    return@setOnClickListener
                }

                val amount = amount.text.toString().toDouble()

                val amountDeterminate: Int

                val type = when (chipGroup.checkedChipId) {
                    R.id.sent -> {
                        amountDeterminate = -1
                        "sent"
                    }
                    R.id.received -> {
                        amountDeterminate = 1
                        "received"
                    }
                    R.id.bought -> {
                        amountDeterminate = 1
                        "bought"
                    }
                    R.id.sold -> {
                        amountDeterminate = -1
                        "sold"
                    }
                    else -> {
                        amountDeterminate = 1
                        "airdrop"
                    }
                }

                if (amountDeterminate < 0) {
                    if (viewModel.holdings < amount) {
                        Toast.makeText(requireContext(), "Insufficient funds", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                }

                if (this@NewTransactionFragment.transaction != null) {
                    viewModel.updateTransaction(
                        transaction!!.copy(
                            price = transactionPrice,
                            amount = amount * amountDeterminate,
                            type = type,
                            message = null
                        )
                    )
                } else {
                    val transaction = Transaction(
                        id = 0,
                        currencyId = currency.id,
                        price = transactionPrice,
                        amount = amount * amountDeterminate,
                        type = type,
                        date = Calendar.getInstance().time,
                        message = null
                    )

                    viewModel.addTransaction(transaction)
                }

                findNavController().popBackStack()
            }
        }

    }

}