package dev.arogundade.wojak.ui.newalert

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.databinding.FragmentNewAlertBinding
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewAlertFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var currency: Currency
    private lateinit var binding: FragmentNewAlertBinding

    private val viewModel: NewAlertViewModel by viewModels()

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
        binding = FragmentNewAlertBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            name.text = currency.name

            price.apply {
                hint = currency.price
                setText(currency.price)
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

            val price = currency.price?.toDouble() ?: 0.0

            addAlert.setOnClickListener {
                val targetPrice = this.price.text.toString().trim()

                if (targetPrice.isEmpty()) {
                    return@setOnClickListener
                }

                val alert = Alert(
                    0,
                    currency.id,
                    targetPrice.toDouble(),
                    true,
                    if (targetPrice.toDouble() >= price) 1 else -1,
                    null,
                    1,
                    Calendar.getInstance().time
                )

                viewModel.addAlert(alert)

                findNavController().popBackStack()
            }
        }

    }

}