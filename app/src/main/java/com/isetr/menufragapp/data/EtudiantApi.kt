package com.isetr.menufragapp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EtudiantApi {
    @GET("api/etudiants")
    suspend fun getEtudiants(): Response<List<Etudiant>>

    @POST("api/addEtudiant")
    suspend fun addEtudiant(@Body etudiant: Etudiant): Response<Etudiant>

    @PUT("api/update")
    suspend fun updateEtudiant(@Body etudiant: Etudiant): Response<Etudiant>

    @GET("api/etudiant/{cin}")
    suspend fun getEtudiantByCin(@Path("cin") cin: String): Response<Etudiant>

    @DELETE("api/delete/{cin}")
    suspend fun deleteEtudiant(@Path("cin") cin: String): Response<Void>

}