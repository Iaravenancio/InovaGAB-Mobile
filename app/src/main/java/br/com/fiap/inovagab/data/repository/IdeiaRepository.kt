package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.data.model.Ideia

class IdeiaRepository {

    private val api
        get() = RetrofitClient.apiService

    suspend fun listarIdeias(): List<Ideia> {
        return api.listarIdeias()
    }

    suspend fun buscarIdeia(id: String): Ideia {
        return api.buscarIdeia(id)
    }

    suspend fun listarIdeiasPorOperador(
        operadorId: String
    ): List<Ideia> {
        return api.listarIdeiasPorOperador(operadorId)
    }

    suspend fun criarIdeia(ideia: Ideia): Ideia {
        return api.criarIdeia(ideia)
    }

    suspend fun atualizarPrioridade(
        id: String,
        prioridade: String
    ): Ideia {
        return api.atualizarPrioridade(
            id,
            mapOf("prioridade" to prioridade)
        )
    }

    suspend fun aprovarIdeia(id: String): Ideia {
        return api.aprovarIdeia(id)
    }

    suspend fun arquivarIdeia(id: String): Ideia {
        return api.arquivarIdeia(id)
    }

    suspend fun excluirIdeia(id: String) {
        api.excluirIdeia(id)
    }
}