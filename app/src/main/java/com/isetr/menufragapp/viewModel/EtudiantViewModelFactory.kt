package com.isetr.menufragapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isetr.menufragapp.data.repository.EtudiantRepository

class EtudiantViewModelFactory(private val repository: EtudiantRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EtudiantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EtudiantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}