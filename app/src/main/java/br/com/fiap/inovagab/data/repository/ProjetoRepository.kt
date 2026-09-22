package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.data.model.Projeto

class ProjetoRepository {

    private val api
        get() = RetrofitClient.apiService

    suspend fun listarProjetos(): List<Projeto> {
        return api.listarProjetos()
    }

    suspend fun buscarProjeto(id: String): Projeto {
        return api.buscarProjeto(id)
    }

    suspend fun listarProjetosPorEstrategia(
        estrategiaId: String
    ): List<Projeto> {
        return api.listarProjetosPorEstrategia(estrategiaId)
    }

    suspend fun listarProjetosPorIdeia(
        ideiaId: String
    ): List<Projeto> {
        return api.listarProjetosPorIdeia(ideiaId)
    }

    suspend fun criarProjeto(projeto: Projeto): Projeto {
        return api.criarProjeto(projeto)
    }

    suspend fun atualizarProjeto(
        id: String,
        projeto: Projeto
    ): Projeto {
        return api.atualizarProjeto(id, projeto)
    }

    suspend fun excluirProjeto(id: String) {
        api.excluirProjeto(id)
    }
}