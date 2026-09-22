package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.data.model.LoginRequest
import br.com.fiap.inovagab.data.model.LoginResponse

class AuthRepository {

    private val api
        get() = RetrofitClient.apiService

    suspend fun login(
        email: String,
        senha: String
    ): LoginResponse {

        return api.login(
            LoginRequest(
                email = email,
                senha = senha
            )
        )
    }
}