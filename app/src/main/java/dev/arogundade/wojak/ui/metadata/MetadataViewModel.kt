package dev.arogundade.wojak.ui.metadata

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.repository.NomicsRepository
import javax.inject.Inject

@HiltViewModel
class MetadataViewModel
@Inject
constructor(
    private val nomicsRepository: NomicsRepository
) : ViewModel() {
}