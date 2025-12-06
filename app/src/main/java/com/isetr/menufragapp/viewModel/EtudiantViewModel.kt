package com.isetr.menufragapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.isetr.menufragapp.data.Etudiant
import com.isetr.menufragapp.data.repository.EtudiantRepository
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EtudiantViewModel(private val repository: EtudiantRepository) : ViewModel() {
    private val _etudiants = MutableLiveData<MutableList<Etudiant>>()
    val etudiants: LiveData<MutableList<Etudiant>> = _etudiants

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _addResult = MutableLiveData<Etudiant?>()
    val addResult: LiveData<Etudiant?> = _addResult

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult

    private val _updateResult = MutableLiveData<Etudiant?>()
    val updateResult: LiveData<Etudiant?> = _updateResult
    private val _selectedEtudiant = MutableLiveData<Etudiant?>()
    val selectedEtudiant: LiveData<Etudiant?> = _selectedEtudiant

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    // Activer un fallback local si l'API distante est injoignable (utile en développement)
    private val USE_LOCAL_FALLBACK = true

    init {
        getAllEtudiants()
    }

    fun getAllEtudiants() {
        _errorMessage.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllEtudiants()
                val errorMsg = "Erreur de serveur: ${response.code()}"
                if (response.isSuccessful && response.body() != null) {
                    val etudiantsList = response.body()!!
                    if (etudiantsList.isNotEmpty()) {
                        Log.d("API_CALL", "liste étudiant trouvée.")
                        _etudiants.postValue(etudiantsList.toMutableList())
                        _errorMessage.postValue(null)
                    } else {
                        Log.d("API_CALL", "Aucun étudiant trouvé.")
                        _etudiants.postValue(mutableListOf())
                        _errorMessage.postValue("Aucun étudiant trouvé.")
                    }
                } else {
                    _errorMessage.postValue(errorMsg)
                    _etudiants.postValue(mutableListOf())
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: UnknownHostException) {
                // Hôte introuvable — souvent problème de DNS ou URL incorrecte
                val errorMsg = "Impossible de joindre le serveur. Vérifiez l'URL de l'API ou votre connexion réseau."
                Log.e("API_CALL", "Erreur réseau/parsing (UnknownHost): ${e.message}", e)
                _errorMessage.postValue(errorMsg)

                if (USE_LOCAL_FALLBACK) {
                    // Fournir des données de démonstration pour que l'UI reste utilisable
                    val fallback = mutableListOf(
                        Etudiant("0001", "Dupont", "Jean", "jean.dupont@example.com", "L1"),
                        Etudiant("0002", "Martin", "Anne", "anne.martin@example.com", "L2")
                    )
                    _etudiants.postValue(fallback)
                } else {
                    _etudiants.postValue(mutableListOf())
                }
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Timeout de connexion. Le serveur met trop de temps à répondre."
                Log.e("API_CALL", "Erreur réseau/parsing (Timeout): ${e.message}", e)
                _errorMessage.postValue(errorMsg)
                _etudiants.postValue(mutableListOf())
            } catch (e: Exception) {
                val errorMsg = "Erreur de connexion. Vérifiez l'URL de l'API."
                _errorMessage.postValue(errorMsg)
                _etudiants.postValue(mutableListOf())
                Log.e("API_CALL", "Erreur réseau/parsing: ${e.message}", e)
            }
        }
    }
    fun getEtudiantByCin(cin: String) {
        _errorMessage.postValue(null)
        _selectedEtudiant.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getEtudiantByCin(cin)
                if (response.isSuccessful && response.body() != null) {
                    val etudiant = response.body()
                    _selectedEtudiant.postValue(etudiant)
                    _errorMessage.postValue(null)
                } else {
                    val errorMsg = "Étudiant non trouvé (Code: ${response.code()})."
                    _errorMessage.postValue(errorMsg)
                    _selectedEtudiant.postValue(null)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de la recherche."
                _errorMessage.postValue(errorMsg)
                _selectedEtudiant.postValue(null)
                Log.e("API_CALL", "Erreur réseau/parsing (GET): ${e.message}", e)
            }
        }
    }

    fun addNewEtudiant(newEtudiant: Etudiant) {
        _errorMessage.postValue(null)
        _addResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.addEtudiant(newEtudiant)
                if (response.isSuccessful && response.body() != null) {
                    val createdEtudiant = response.body()
                    _addResult.postValue(createdEtudiant)
                    _operationSuccess.postValue(true)
                    getAllEtudiants()
                } else {
                    val errorMsg = "Erreur lors de l'ajout (Code: ${response.code()})."
                    _errorMessage.postValue(errorMsg)
                    _addResult.postValue(null)
                    _operationSuccess.postValue(false)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de l'ajout."
                _errorMessage.postValue(errorMsg)
                _addResult.postValue(null)
                _operationSuccess.postValue(false)
                Log.e("API_CALL", "Erreur réseau/parsing (POST): ${e.message}", e)
            }
        }
    }

    fun updateEtudiant(etudiant: Etudiant) {
        _errorMessage.postValue(null)
        _updateResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.updateEtudiant(etudiant)
                if (response.isSuccessful && response.body() != null) {
                    val updatedEtudiant = response.body()
                    _updateResult.postValue(updatedEtudiant)
                    _operationSuccess.postValue(true)
                    getAllEtudiants()
                } else {
                    val errorMsg = "Erreur lors de la mise à jour (Code: ${response.code()})."
                    _errorMessage.postValue(errorMsg)
                    _updateResult.postValue(null)
                    _operationSuccess.postValue(false)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de la mise à jour."
                _errorMessage.postValue(errorMsg)
                _updateResult.postValue(null)
                _operationSuccess.postValue(false)
                Log.e("API_CALL", "Erreur réseau/parsing (PUT): ${e.message}", e)
            }
        }
    }

    fun deleteEtudiant(cin: String) {
        _errorMessage.postValue(null)
        _deleteResult.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.deleteEtudiant(cin)
                if (response.isSuccessful) {
                    _deleteResult.postValue(true)
                    _operationSuccess.postValue(true)
                    getAllEtudiants()
                } else {
                    val errorMsg = "Erreur lors de la suppression (Code: ${response.code()})."
                    _errorMessage.postValue(errorMsg)
                    _deleteResult.postValue(false)
                    _operationSuccess.postValue(false)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de la suppression."
                _errorMessage.postValue(errorMsg)
                _deleteResult.postValue(false)
                _operationSuccess.postValue(false)
                Log.e("API_CALL", "Erreur réseau/parsing (DELETE): ${e.message}", e)
            }
        }
    }
    fun clearSelectedEtudiant() {
        _selectedEtudiant.postValue(null)
    }

    fun clearOperationSuccess() {
        _operationSuccess.postValue(false)
    }

    fun refreshEtudiants() {
        getAllEtudiants()
    }
}