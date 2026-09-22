package br.com.fiap.inovagab.data.model

data class LoginResponse(
    val token: String,
    val id: String,
    val nome: String,
    val email: String,
    val perfil: String
)