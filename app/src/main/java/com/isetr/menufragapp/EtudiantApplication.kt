package com.isetr.menufragapp

import android.app.Application
import com.isetr.menufragapp.data.repository.EtudiantRepository
import com.isetr.menufragapp.viewModel.EtudiantViewModelFactory
import kotlin.getValue

class EtudiantApplication : Application() {

    val etudiantRepository by lazy { EtudiantRepository() }
    val viewModelFactory by lazy { EtudiantViewModelFactory(etudiantRepository) }
}