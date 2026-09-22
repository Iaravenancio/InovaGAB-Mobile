package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.data.model.OrientacaoEstrategica

class EstrategiaRepository {

    private val api
        get() = RetrofitClient.apiService

    suspend fun listarEstrategias(): List<OrientacaoEstrategica> {
        return api.listarEstrategias()
    }

    suspend fun buscarEstrategia(id: String): OrientacaoEstrategica {
        return api.buscarEstrategia(id)
    }
}