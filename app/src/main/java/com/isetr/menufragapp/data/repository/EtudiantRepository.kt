package com.isetr.menufragapp.data.repository

import com.isetr.menufragapp.data.Etudiant
import com.isetr.menufragapp.data.RetrofitInstance

import retrofit2.Response

class EtudiantRepository {

    suspend fun getAllEtudiants(): Response<List<Etudiant>> {
        return RetrofitInstance.api.getEtudiants()
    }

    suspend fun getEtudiantByCin(cin: String): Response<Etudiant> {
        return RetrofitInstance.api.getEtudiantByCin(cin)
    }

    suspend fun addEtudiant(etudiant: Etudiant): Response<Etudiant> {
        return RetrofitInstance.api.addEtudiant(etudiant)
    }

    suspend fun updateEtudiant(etudiant: Etudiant): Response<Etudiant> {
        return RetrofitInstance.api.updateEtudiant(etudiant)
    }

    suspend fun deleteEtudiant(cin: String): Response<Void> {
        return RetrofitInstance.api.deleteEtudiant(cin)
    }
}