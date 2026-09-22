package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.data.model.AnaliseIdeiaResponse

class IaRepository {

    private val api
        get() = RetrofitClient.apiService

    suspend fun analisarIdeia(
        ideiaId: String
    ): AnaliseIdeiaResponse {
        return api.analisarIdeiaComIA(ideiaId)
    }
}