package br.com.fiap.inovagab.data.model

data class Projeto(
    val id: String? = null,
    val nome: String,
    val descricao: String,
    val status: String? = null,
    val progresso: Double? = null,
    val investimento: Double? = null,
    val retornoFinanceiro: Double? = null,
    val prazo: String? = null,
    val ganhoProdutividade: Double? = null,
    val reducaoCustos: Double? = null,
    val estrategiaId: String? = null,
    val ideiaId: String? = null,
    val dataCriacao: String? = null
)