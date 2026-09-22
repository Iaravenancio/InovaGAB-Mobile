package br.com.fiap.inovagab.data.api

import br.com.fiap.inovagab.data.model.AnaliseIdeiaResponse
import br.com.fiap.inovagab.data.model.DashboardResumo
import br.com.fiap.inovagab.data.model.Ideia
import br.com.fiap.inovagab.data.model.LoginRequest
import br.com.fiap.inovagab.data.model.LoginResponse
import br.com.fiap.inovagab.data.model.OrientacaoEstrategica
import br.com.fiap.inovagab.data.model.Projeto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // =========================
    // AUTENTICAÇÃO
    // =========================

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    // =========================
    // ESTRATÉGIAS
    // =========================

    @GET("api/estrategias")
    suspend fun listarEstrategias(): List<OrientacaoEstrategica>

    @GET("api/estrategias/{id}")
    suspend fun buscarEstrategia(
        @Path("id") id: String
    ): OrientacaoEstrategica


    // =========================
    // IDEIAS
    // =========================

    @GET("api/ideias")
    suspend fun listarIdeias(): List<Ideia>

    @GET("api/ideias/{id}")
    suspend fun buscarIdeia(
        @Path("id") id: String
    ): Ideia

    @GET("api/ideias/operador/{operadorId}")
    suspend fun listarIdeiasPorOperador(
        @Path("operadorId") operadorId: String
    ): List<Ideia>

    @POST("api/ideias")
    suspend fun criarIdeia(
        @Body ideia: Ideia
    ): Ideia

    @PATCH("api/ideias/{id}/prioridade")
    suspend fun atualizarPrioridade(
        @Path("id") id: String,
        @Body prioridade: Map<String, String>
    ): Ideia

    @PATCH("api/ideias/{id}/aprovar")
    suspend fun aprovarIdeia(
        @Path("id") id: String
    ): Ideia

    @PATCH("api/ideias/{id}/arquivar")
    suspend fun arquivarIdeia(
        @Path("id") id: String
    ): Ideia

    @DELETE("api/ideias/{id}")
    suspend fun excluirIdeia(
        @Path("id") id: String
    )


    // =========================
    // PROJETOS
    // =========================

    @GET("api/projetos")
    suspend fun listarProjetos(): List<Projeto>

    @GET("api/projetos/{id}")
    suspend fun buscarProjeto(
        @Path("id") id: String
    ): Projeto

    @GET("api/projetos/estrategia/{estrategiaId}")
    suspend fun listarProjetosPorEstrategia(
        @Path("estrategiaId") estrategiaId: String
    ): List<Projeto>

    @GET("api/projetos/ideia/{ideiaId}")
    suspend fun listarProjetosPorIdeia(
        @Path("ideiaId") ideiaId: String
    ): List<Projeto>

    @POST("api/projetos")
    suspend fun criarProjeto(
        @Body projeto: Projeto
    ): Projeto

    @PUT("api/projetos/{id}")
    suspend fun atualizarProjeto(
        @Path("id") id: String,
        @Body projeto: Projeto
    ): Projeto

    @DELETE("api/projetos/{id}")
    suspend fun excluirProjeto(
        @Path("id") id: String
    )


    // =========================
    // DASHBOARD
    // =========================

    @GET("api/dashboard/resumo")
    suspend fun obterDashboard(): DashboardResumo

    @GET("api/dashboard/estrategia/{estrategiaId}")
    suspend fun obterDashboardPorEstrategia(
        @Path("estrategiaId") estrategiaId: String
    ): DashboardResumo


    // =========================
    // INTELIGÊNCIA ARTIFICIAL
    // =========================

    @POST("api/ia/analisar/{ideiaId}")
    suspend fun analisarIdeiaComIA(
        @Path("ideiaId") ideiaId: String
    ): AnaliseIdeiaResponse
}