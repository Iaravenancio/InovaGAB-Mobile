package br.com.fiap.inovagab.data.api

import android.content.Context
import br.com.fiap.inovagab.data.local.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "https://SUA-URL-DO-CODESPACE-DO-BACKEND/"

    private lateinit var tokenManager: TokenManager

    private lateinit var retrofit: Retrofit

    fun inicializar(context: Context) {

        tokenManager = TokenManager(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenManager)
            )
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val apiService: ApiService
        get() = retrofit.create(ApiService::class.java)
}