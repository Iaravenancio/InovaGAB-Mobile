package br.com.fiap.inovagab.model

enum class UserRole {
    OPERADOR, GESTOR, LIDER
}

data class Usuario(
    val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val role: UserRole = UserRole.OPERADOR,
    val xp: Int = 0,
    val badges: List<String> = emptyList()
)

data class Ideia(
    val id: String = "",
    val idOperador: String = "",
    val nomeOperador: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val status: String = "ENVIADA", // ENVIADA, EM_ANALISE, APROVADA, REJEITADA
    val categoriaIA: String = "",
    val resumoIA: String = "",
    val dataCriacao: Long = System.currentTimeMillis(),
    val prioridade: String = ""
)

data class Projeto(

    val id: String = "",
    val idIdeiaOrigem: String = "",
    val titulo: String = "",
    val status: String = "",
    val investimento: Double = 0.0,
    val retornoFinanceiroEst: Double = 0.0,
    val prazo: String = "",
    val ganhoProdutividadeEst: Int = 0,
    val reducaoCustosEst: Int = 0,
    val percentualConclusao: Int = 0
)

data class OrientacaoEstrategica(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val dataPublicacao: Long = System.currentTimeMillis()
)