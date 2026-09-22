package br.com.fiap.inovagab.data.model

data class Ideia(
    val id: String? = null,
    val titulo: String,
    val descricao: String,
    val status: String? = null,
    val prioridade: String? = null,
    val operadorId: String? = null,
    val estrategiaId: String? = null,
    val dataCriacao: String? = null,
    val xp: Int? = null
)