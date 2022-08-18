package dev.arogundade.wojak.ui.add

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
import dev.arogundade.wojak.databinding.FragmentAddToPortfolioBinding
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.Portfolio
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddToPortfolioFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var currency: Currency
    private lateinit var binding: FragmentAddToPortfolioBinding
    private val viewModel: AddToPortfolioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currency = it.getSerializable("currency") as Currency
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddToPortfolioBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            name.text = currency.name
            goalDesc.text = "Do you have a goal for ${currency.name}?"
            amountLabel.text = "Enter goal amount in ${currency.symbol}"
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

            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.contains(R.id.yes)) {
                    binding.goalLayout.visibility = View.VISIBLE
                    binding.amountLabel.visibility = View.VISIBLE
                    binding.amount.visibility = View.VISIBLE
                } else {
                    binding.goalLayout.visibility = View.INVISIBLE
                    binding.amountLabel.visibility = View.INVISIBLE
                    binding.amount.visibility = View.INVISIBLE
                }
            }

            addToPortfolio.setOnClickListener {
                val portfolio: Portfolio

                if (chipGroup.checkedChipIds.contains(R.id.yes)) {
                    val goalTitle = this.goalTitle.text.toString().trim()
                    val amount = this.amount.text.toString().trim()

                    if (goalTitle.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Goal title is required",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    if (amount.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Goal amount is required",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    portfolio = Portfolio(
                        id = currency.id,
                        goalTitle = goalTitle,
                        goalAmount = amount.toDouble(),
                        date = Calendar.getInstance().time
                    )
                } else {
                    portfolio = Portfolio(
                        id = currency.id,
                        goalTitle = null,
                        goalAmount = null,
                        date = Calendar.getInstance().time
                    )
                }

                viewModel.addToPortFolio(portfolio)

                findNavController().popBackStack()
            }
        }

    }

}