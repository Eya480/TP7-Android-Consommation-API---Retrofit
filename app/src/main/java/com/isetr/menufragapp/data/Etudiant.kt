package com.isetr.menufragapp.data

import com.google.gson.annotations.SerializedName

/**
 * Modèle de données pour un étudiant.
 * Utilise 'data class' pour obtenir automatiquement des méthodes utiles comme equals() et copy().
 */
data class Etudiant(
    @SerializedName("cin") val cin: String,
    @SerializedName("nom") val nom: String,
    @SerializedName("prenom") val prenom: String,
    @SerializedName("mail") val mail: String,
    @SerializedName("classe") val classe: String
)