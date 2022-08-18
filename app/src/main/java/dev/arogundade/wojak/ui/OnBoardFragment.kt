package dev.arogundade.wojak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.FragmentOnBoardBinding
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var binding: FragmentOnBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            createPortfolio.setOnClickListener {
                findNavController().navigate(R.id.action_onBoardFragment_to_homeFragment)
            }

            requestManager.load(R.drawable.wojak_gif)
                .into(wojak)

        }

    }


}