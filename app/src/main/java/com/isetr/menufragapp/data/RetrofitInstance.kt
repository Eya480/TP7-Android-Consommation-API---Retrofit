package com.isetr.menufragapp.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private const val BASE_URL = "https://gestetudiants.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val retryInterceptor = Interceptor { chain ->
        var attempt = 0
        val maxAttempts = 3
        var lastException: IOException? = null
        while (attempt < maxAttempts) {
            try {
                return@Interceptor chain.proceed(chain.request())
            } catch (e: IOException) {
                lastException = e
                attempt++
                try {
                    Thread.sleep(250L * attempt)
                } catch (ignored: InterruptedException) {
                }
            }
        }
        throw lastException ?: IOException("Unknown network error")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(retryInterceptor)
        .addInterceptor(loggingInterceptor) // Pour debug
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: EtudiantApi by lazy {
        retrofit.create(EtudiantApi::class.java)
    }
}