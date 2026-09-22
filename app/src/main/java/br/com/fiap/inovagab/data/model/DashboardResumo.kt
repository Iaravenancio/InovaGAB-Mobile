package br.com.fiap.inovagab.data.model

data class DashboardResumo(
    val totalProjetos: Long,
    val totalInvestimento: Double,
    val totalRetornoFinanceiro: Double,
    val lucro: Double,
    val roi: Double,
    val progressoMedio: Double,
    val ganhoProdutividade: Double,
    val reducaoCustos: Double
)