package br.com.fiap.inovagab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.inovagab.model.Usuario
import br.com.fiap.inovagab.model.Ideia
import br.com.fiap.inovagab.model.Projeto
import br.com.fiap.inovagab.model.OrientacaoEstrategica
import br.com.fiap.inovagab.model.StatusIdeia
import br.com.fiap.inovagab.model.UserRole
import br.com.fiap.inovagab.service.GeminiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID



class InovaGABViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val geminiService = GeminiService()

    private val _usuarioLogado = MutableStateFlow<Usuario?>(null)
    val usuarioLogado: StateFlow<Usuario?> = _usuarioLogado.asStateFlow()

    private val _ideias = MutableStateFlow<List<Ideia>>(emptyList())
    val ideias: StateFlow<List<Ideia>> = _ideias.asStateFlow()

    private val _projetos = MutableStateFlow<List<Projeto>>(emptyList())
    val projetos: StateFlow<List<Projeto>> = _projetos.asStateFlow()

    private val _orientacoes = MutableStateFlow<List<OrientacaoEstrategica>>(emptyList())
    val orientacoes: StateFlow<List<OrientacaoEstrategica>> = _orientacoes.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    private val _mensagemSucesso = MutableStateFlow<String?>(null)
    val mensagemSucesso: StateFlow<String?> = _mensagemSucesso.asStateFlow()
    init {
        verificarSessao()
        escutarOrientacoes()
        escutarIdeias()
        escutarProjetos()
    }

    val percentualConclusao: Int = 0

    fun fazerLoginReal(email: String, senha: String) {

        _isLoading.value = true
        _mensagemErro.value = null

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->

                _isLoading.value = false

                if (task.isSuccessful) {

                    val firebaseUser = task.result?.user

                    if (firebaseUser != null) {

                        db.collection("usuarios")
                            .document(firebaseUser.uid)
                            .get()
                            .addOnSuccessListener { document ->

                                if (document.exists()) {

                                    val perfilString =
                                        document.getString("perfil") ?: "OPERADOR"

                                    val cargoValidado = when (perfilString) {
                                        "GESTOR" -> UserRole.GESTOR
                                        "LIDER" -> UserRole.LIDER
                                        else -> UserRole.OPERADOR
                                    }

                                    val usuarioCompleto = Usuario(
                                        uid = document.getString("uid") ?: firebaseUser.uid,
                                        nome = document.getString("nome") ?: "Usuário",
                                        email = email,
                                        role = cargoValidado,
                                        xp = document.getLong("xp")?.toInt() ?: 0
                                    )

                                    _usuarioLogado.value = usuarioCompleto

                                    _mensagemSucesso.value = "Login realizado com sucesso"

                                } else {

                                    _mensagemErro.value =
                                        "Usuário não encontrado no banco"

                                }

                            }
                            .addOnFailureListener {

                                _mensagemErro.value =
                                    "Erro ao carregar usuário"

                            }
                    }

                } else {

                    _mensagemErro.value =
                        "E-mail ou senha inválidos"

                }
            }
    }

    private fun verificarSessao() {

        val usuarioFirebase = auth.currentUser

        if (usuarioFirebase != null) {

            db.collection("usuarios")
                .document(usuarioFirebase.uid)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val perfilString =
                            document.getString("perfil") ?: "OPERADOR"

                        val cargoValidado = when (perfilString) {
                            "GESTOR" -> UserRole.GESTOR
                            "LIDER" -> UserRole.LIDER
                            else -> UserRole.OPERADOR
                        }

                        val usuarioCompleto = Usuario(
                            uid = document.getString("uid") ?: usuarioFirebase.uid,
                            nome = document.getString("nome") ?: "Usuário",
                            email = document.getString("email") ?: "",
                            role = cargoValidado,
                            xp = document.getLong("xp")?.toInt() ?: 0
                        )

                        _usuarioLogado.value = usuarioCompleto
                    }
                }
        }
    }
    fun fazerLogout() {
        FirebaseAuth.getInstance().signOut()
        _usuarioLogado.value = null
    }

    fun cadastrarIdeia(titulo: String, descricao: String) {
        // Pega o usuário que está logado atualmente (Carlos)
        val usuarioAtual = _usuarioLogado.value ?: return

        viewModelScope.launch {
            val resultadoIA = geminiService.analisarIdeia(titulo, descricao)

            val novaIdeia = Ideia(
                id = java.util.UUID.randomUUID().toString(),
                idOperador = usuarioAtual.uid,
                titulo = titulo,
                descricao = descricao,
                status = StatusIdeia.EM_ANALISE.name,
                categoriaIA = resultadoIA.first,
                resumoIA = resultadoIA.second
            )

            db.collection("ideias").document(novaIdeia.id).set(novaIdeia)

            try {
                val novoXp = usuarioAtual.xp + 50

                _usuarioLogado.value = usuarioAtual.copy(xp = novoXp)

                db.collection("usuarios").document(usuarioAtual.uid).update("xp", novoXp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun atualizarStatusIdeia(
        idIdeia: String,
        novoStatus: String,
        prioridade: String = "MÉDIA"
    ) {

        db.collection("ideias")
            .document(idIdeia)
            .update(
                mapOf(
                    "status" to novoStatus,
                    "prioridade" to prioridade
                )
            )
    }

    fun transformarIdeiaEmProjeto(ideia: Ideia, investimento: Double, prazo: String, retornoEst: Double, progresso: Int, prioridade: String) {
        val idProjeto = UUID.randomUUID().toString()

        val novoProjeto = Projeto(
            id = idProjeto,
            idIdeiaOrigem = ideia.id,
            titulo = "Projeto: ${ideia.titulo}",
            status = "EM_ANDAMENTO",
            investimento = investimento,
            prazo = prazo,
            retornoFinanceiroEst = retornoEst
        )

        // 1. Salva o projeto no banco
        db.collection("projetos").document(idProjeto).set(novoProjeto)

        // 2. Atualiza o status da ideia de origem para APROVADA
        db.collection("ideias").document(ideia.id).update("status", "APROVADA")

        // Dá o prêmio de XP para o Operador que deu a ideia!
        try {
            db.collection("usuarios").document(ideia.idOperador).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val xpAtual = document.getLong("xp") ?: 0
                        // Soma +100 pontos pela conversão da ideia em projeto real
                        db.collection("usuarios").document(ideia.idOperador).update("xp", xpAtual + 100)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun atualizarProjeto(projeto: Projeto) {

        fun atualizarIdeia(ideia: Ideia) {

            db.collection("ideias")
                .document(ideia.id)
                .set(ideia)
        }

        db.collection("projetos")
            .document(projeto.id)
            .set(projeto)
            .addOnSuccessListener {

                _mensagemSucesso.value =
                    "Projeto atualizado com sucesso"

            }
            .addOnFailureListener {

                _mensagemErro.value =
                    "Erro ao atualizar projeto"

            }
    }
    fun criarOrientacaoEstrategica(titulo: String, descricao: String) {
        val id = UUID.randomUUID().toString()
        val orientacao = OrientacaoEstrategica(id = id, titulo = titulo, descricao = descricao)
        db.collection("orientacoes_estrategicas").document(id).set(orientacao)
    }

    fun deletarOrientacaoEstrategica(id: String) {
        db.collection("orientacoes_estrategicas").document(id).delete()
    }

    fun atualizarOrientacao(
        orientacao: OrientacaoEstrategica
    ) {

        db.collection("orientacoes_estrategicas")
            .document(orientacao.id)
            .set(orientacao)
    }

    private fun escutarOrientacoes() {
        db.collection("orientacoes_estrategicas")
            .addSnapshotListener { snapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                if (snapshot != null) {
                    _orientacoes.value = snapshot.toObjects(OrientacaoEstrategica::class.java)
                }
            }
    }

    private fun escutarIdeias() {
        db.collection("ideias")
            .addSnapshotListener { snapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                if (snapshot != null) {
                    _ideias.value = snapshot.toObjects(Ideia::class.java)
                }
            }
    }

    private fun escutarProjetos() {
        db.collection("projetos")
            .addSnapshotListener { snapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                if (snapshot != null) {
                    _projetos.value = snapshot.toObjects(Projeto::class.java)
                }
            }
    }

    fun atualizarIdeia(ideia: Ideia) {

        db.collection("ideias")
            .document(ideia.id)
            .set(ideia)
    }
}