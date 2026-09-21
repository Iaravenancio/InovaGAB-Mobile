package br.com.fiap.inovagab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.model.*
import br.com.fiap.inovagab.viewmodel.InovaGABViewModel
import java.text.NumberFormat
import java.util.Locale

val AzulAguia = Color(0xFF1A3B8B)
val VerdeInovacao = Color(0xFF00C853)

val formatter =
    NumberFormat.getCurrencyInstance(
        Locale("pt", "BR")
    )
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavContainer(viewModel: InovaGABViewModel) {

    val usuarioLogado by viewModel.usuarioLogado.collectAsState()
    val ideias by viewModel.ideias.collectAsState()
    val projetos by viewModel.projetos.collectAsState()
    val orientacoes by viewModel.orientacoes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "InovaGAB - Águia Branca",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulAguia
                ),
                actions = {
                    if (usuarioLogado != null) {
                        IconButton(onClick = {
                            viewModel.fazerLogout()
                        }) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Sair",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            val user = usuarioLogado

            if (user == null) {

                TelaLogin(
                    onLogin = { email, senha ->
                        viewModel.fazerLoginReal(email, senha)
                    }
                )

            } else {

                when (user.role) {

                    UserRole.OPERADOR -> {
                        TelaOperador(
                            user = user,
                            diretrizes = orientacoes,
                            minhasIdeias = ideias,
                            onSubmeterIdeia = { t, d ->
                                viewModel.cadastrarIdeia(t, d)
                            }
                        )
                    }

                    UserRole.GESTOR -> {
                        TelaGestor(
                            ideias = ideias,
                            projetos = projetos,

                            onAprovar = {
                                    ideia,
                                    investimento,
                                    prazo,
                                    retorno,
                                    progresso,
                                    prioridade ->

                                viewModel.atualizarStatusIdeia(
                                    ideia.id,
                                    "APROVADA",
                                    prioridade
                                )

                                viewModel.transformarIdeiaEmProjeto(
                                    ideia,
                                    investimento,
                                    prazo,
                                    retorno,
                                    progresso,
                                    prioridade
                                )
                            },

                            onRejeitar = { id ->
                                viewModel.atualizarStatusIdeia(
                                    id,
                                    "REJEITADA"
                                )
                            },

                            onAtualizarProjeto = { projeto ->
                                viewModel.atualizarProjeto(projeto)
                            },

                            onAtualizarPrioridade = { ideia ->
                                viewModel.atualizarIdeia(ideia)
                            }

                        )
                    }

                    UserRole.LIDER -> {
                        TelaLider(
                            diretrizes = orientacoes,
                            projetos = projetos,

                            onCreateDiretriz = { t, d ->
                                viewModel.criarOrientacaoEstrategica(t, d)
                            },

                            onDeleteDiretriz = { id ->
                                viewModel.deletarOrientacaoEstrategica(id)
                            },

                            onUpdateDiretriz = { orientacao ->
                                viewModel.atualizarOrientacao(orientacao)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelaLogin(onLogin: (String, String) -> Unit) {

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            Icons.Default.Lightbulb,
            contentDescription = null,
            tint = AzulAguia,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Plataforma de Inovação",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AzulAguia
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail corporativo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && senha.isNotBlank()) {
                    onLogin(email.trim(), senha.trim())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = AzulAguia
            )
        ) {
            Text("Entrar")
        }
    }
}

@Composable
fun TelaOperador(
    user: Usuario,
    diretrizes: List<OrientacaoEstrategica>,
    minhasIdeias: List<Ideia>,
    onSubmeterIdeia: (String, String) -> Unit
) {

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {

            Text(
                "Olá, ${user.nome}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulAguia
            )

            Spacer(modifier = Modifier.height(4.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AzulAguia
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            "Seu desempenho",
                            color = Color.White
                        )

                        Text(
                            "${user.xp} XP",
                            color = VerdeInovacao,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "📌 Estratégias da Empresa",
                fontWeight = FontWeight.Bold,
                color = AzulAguia,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            diretrizes.take(3).forEach { diretriz ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(6.dp)
                    ) {

                        Text(
                            diretriz.titulo,
                            fontWeight = FontWeight.Bold
                        )

                        Text(diretriz.descricao)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(

                "💡 Nova Ideia",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AzulAguia
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    if (titulo.isNotBlank() && descricao.isNotBlank()) {

                        onSubmeterIdeia(titulo, descricao)

                        titulo = ""
                        descricao = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeInovacao
                )
            ) {
                Text("Submeter")
            }

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(

                "📋 Minhas Ideias",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AzulAguia
            )
        }

        items(

            minhasIdeias.filter {
                it.idOperador == user.uid
            }.reversed()
        ) { ideia ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {

                Column(
                    modifier = Modifier.padding(6.dp)
                ) {

                    Text(
                        ideia.titulo,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(ideia.descricao)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Status: ${ideia.status}",
                        color = AzulAguia,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TelaGestor(
    ideias: List<Ideia>,
    projetos: List<Projeto>,

    onAprovar: (
        Ideia,
        Double,
        String,
        Double,
        Int,
        String
    ) -> Unit,

    onRejeitar: (String) -> Unit,

    onAtualizarProjeto: (Projeto) -> Unit,

    onAtualizarPrioridade: (Ideia) -> Unit,

) {

    var abaSelecionada by remember {
        mutableStateOf(0)
    }

    var abrirModalProjeto by remember {
        mutableStateOf(false)
    }

    var ideiaSelecionada by remember {
        mutableStateOf<Ideia?>(null)
    }

    var investimento by remember {
        mutableStateOf("")
    }

    var prazo by remember {
        mutableStateOf("")
    }

    var retorno by remember {
        mutableStateOf("")
    }


    var prioridade by remember {
        mutableStateOf("")
    }
    var projetoEditando by remember {
        mutableStateOf<Projeto?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TabRow(selectedTabIndex = abaSelecionada) {

            Tab(
                selected = abaSelecionada == 0,
                onClick = { abaSelecionada = 0 },
                text = { Text("Ideias") }
            )

            Tab(
                selected = abaSelecionada == 1,
                onClick = { abaSelecionada = 1 },
                text = { Text("Projetos") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (abaSelecionada == 0) {

            LazyColumn {

                items(
                    ideias.filter {
                        it.status == "ENVIADA" ||
                                it.status == "EM_ANALISE"
                    }
                ) { ideia ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                ideia.titulo,
                                fontWeight = FontWeight.Bold,
                                color = AzulAguia
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(ideia.descricao)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "IA: ${ideia.resumoIA}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Prioridade: ${ideia.prioridade}",
                                color =
                                    when (ideia.prioridade) {

                                        "ALTA" -> Color.Red

                                        "MÉDIA" -> Color(0xFFFF9800)

                                        else -> Color(0xFF4CAF50)
                                    },

                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                listOf("ALTA", "MÉDIA", "BAIXA").forEach { nivel ->

                                    FilterChip(
                                        selected = ideia.prioridade == nivel,

                                        onClick = {

                                            val ideiaAtualizada =
                                                ideia.copy(
                                                    prioridade = nivel,
                                                    status = "EM_ANALISE"
                                                )

                                            onAtualizarPrioridade(
                                                ideiaAtualizada
                                            )
                                        },

                                        label = {
                                            Text(nivel)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {

                                TextButton(
                                    onClick = {
                                        onRejeitar(ideia.id)
                                    }
                                ) {
                                    Text(
                                        "Arquivar",
                                        color = Color.Red
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {

                                        ideiaSelecionada = ideia
                                        abrirModalProjeto = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VerdeInovacao
                                    )
                                ) {
                                    Text("Criar Projeto")
                                }
                            }
                        }
                    }
                }
            }

        } else {

            LazyColumn {

                items(projetos) { projeto ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                projeto.titulo,
                                fontWeight = FontWeight.Bold,
                                color = AzulAguia
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Status: ${projeto.status}")
                            Text("Prazo: ${projeto.prazo}")

                            Text(
                                "Investimento: R$ ${projeto.investimento}"
                            )

                            Text(
                                "Retorno: R$ ${projeto.retornoFinanceiroEst}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = {
                                    projeto.percentualConclusao / 100f
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                "${projeto.percentualConclusao}% concluído",
                                fontWeight = FontWeight.Bold,
                                color = AzulAguia
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    projetoEditando = projeto
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Atualizar Projeto")
                            }
                        }
                    }
                }
            }
        }
    }

    if (abrirModalProjeto && ideiaSelecionada != null) {

        AlertDialog(

            onDismissRequest = {
                abrirModalProjeto = false
            },

            title = {
                Text("Criar Projeto")
            },

            text = {

                Column {

                    Text(
                        "📁 Cadastro do Projeto",
                        fontWeight = FontWeight.Bold,
                        color = AzulAguia
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = investimento,
                        onValueChange = { investimento = it },
                        label = {
                            Text("Investimento")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = prazo,
                        onValueChange = { prazo = it },
                        label = {
                            Text("Prazo")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = retorno,
                        onValueChange = { retorno = it },
                        label = {
                            Text("Retorno Financeiro")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        ideiaSelecionada?.let {

                            onAprovar(
                                it,
                                investimento.toDoubleOrNull() ?: 0.0,
                                prazo,
                                retorno.toDoubleOrNull() ?: 0.0,
                                0,
                                prioridade
                            )
                        }

                        abrirModalProjeto = false
                    }
                ) {
                    Text("Criar Projeto")
                }
            }
        )
    }

    projetoEditando?.let { projeto ->

        var novoStatus by remember {
            mutableStateOf(projeto.status)
        }

        var novoPrazo by remember {
            mutableStateOf(projeto.prazo)
        }

        var novoInvestimento by remember {
            mutableStateOf(projeto.investimento.toString())
        }

        var novoRetorno by remember {
            mutableStateOf(projeto.retornoFinanceiroEst.toString())
        }

        var novoProgresso by remember {
            mutableStateOf(
                projeto.percentualConclusao.toString()
            )
        }

        var novaProdutividade by remember {

            mutableStateOf(
                projeto.ganhoProdutividadeEst.toString()
            )
        }

        var novaReducaoCustos by remember {

            mutableStateOf(
                projeto.reducaoCustosEst.toString()
            )
        }

        var prioridade by remember {
            mutableStateOf("")
        }

        AlertDialog(

            onDismissRequest = {
                projetoEditando = null
            },

            title = {
                Text("Atualizar Projeto")
            },

            text = {

                Column {

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoStatus,
                        onValueChange = {
                            novoStatus = it
                        },
                        label = {
                            Text("Status")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoPrazo,
                        onValueChange = {
                            novoPrazo = it
                        },
                        label = {
                            Text("Prazo")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoInvestimento,
                        onValueChange = {
                            novoInvestimento = it
                        },
                        label = {
                            Text("Investimento")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoRetorno,
                        onValueChange = {
                            novoRetorno = it
                        },
                        label = {
                            Text("Retorno")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoProgresso,

                        onValueChange = {
                            novoProgresso = it
                        },

                        label = {
                            Text("Progresso (%)")
                        }

                    )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = novaProdutividade,

                    onValueChange = {
                        novaProdutividade = it
                    },

                    label = {
                        Text("Produtividade (%)")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = novaReducaoCustos,

                    onValueChange = {
                        novaReducaoCustos = it
                    },

                    label = {
                        Text("Redução de Custos (%)")
                    }
                )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        val atualizado = projeto.copy(
                            status = novoStatus,
                            prazo = novoPrazo,
                            investimento = novoInvestimento.toDoubleOrNull() ?: 0.0,
                            retornoFinanceiroEst = novoRetorno.toDoubleOrNull() ?: 0.0,
                            percentualConclusao = novoProgresso.toIntOrNull() ?: 0,
                            ganhoProdutividadeEst = novaProdutividade.toIntOrNull() ?: 0,
                            reducaoCustosEst =  novaReducaoCustos.toIntOrNull() ?: 0
                        )

                        onAtualizarProjeto(atualizado)

                        projetoEditando = null
                    }
                ) {
                    Text("Salvar")
                }
            }
        )
    }
}

@Composable
fun TelaLider(
    diretrizes: List<OrientacaoEstrategica>,
    projetos: List<Projeto>,
    onCreateDiretriz: (String, String) -> Unit,
    onDeleteDiretriz: (String) -> Unit,
    onUpdateDiretriz: (OrientacaoEstrategica) -> Unit
) {

    var verDashboard by remember {
        mutableStateOf(true)
    }

    var tituloD by remember {
        mutableStateOf("")
    }

    var descD by remember {
        mutableStateOf("")
    }

    var diretrizEditando by remember {
        mutableStateOf<OrientacaoEstrategica?>(null)
    }

    val investimentoTotal = projetos.sumOf {
        it.investimento
    }

    val retornoTotal = projetos.sumOf {
        it.retornoFinanceiroEst
    }

    val roiGeral = if (investimentoTotal > 0) {
        ((retornoTotal - investimentoTotal) / investimentoTotal) * 100
    } else {
        0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                if (verDashboard)
                    "📊 Dashboard Executivo"
                else
                    "⚙️ Estratégias Corporativas",

                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AzulAguia
            )

            IconButton(
                onClick = {
                    verDashboard = !verDashboard
                }
            ) {

                Icon(
                    imageVector =
                        if (verDashboard)
                            Icons.Default.Settings
                        else
                            Icons.Default.BarChart,

                    contentDescription = "Alternar Tela"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (verDashboard) {

            LazyColumn {

                item {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = AzulAguia
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                            horizontalAlignment = Alignment.CenterHorizontally,

                            verticalArrangement = Arrangement.Center
                        ){

                            Text(
                                "ROI Geral",
                                color = Color.White
                            )

                            Text(
                                String.format("%.1f%%", roiGeral),
                                fontSize = 32.sp,
                                color = VerdeInovacao,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Card(
                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    "💰 Investimento",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    formatter.format(investimentoTotal)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    "📈 Retorno",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    formatter.format(retornoTotal)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Card(
                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    "⚡ Produtividade",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "${projetos.sumOf {
                                        it.ganhoProdutividadeEst
                                    }}%"
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    "📉 Custos",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "${projetos.sumOf {
                                        it.reducaoCustosEst
                                    }}%"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "📁 Projetos",
                        fontWeight = FontWeight.Bold,
                        color = AzulAguia
                    )
                }

                items(projetos) { projeto ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {

                            Text(
                                projeto.titulo,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Status: ${projeto.status}"
                            )

                            Text(
                                "Prazo: ${projeto.prazo}"
                            )

                            Text(
                                "Retorno: ${formatter.format(projeto.retornoFinanceiroEst)}"
                            )
                        }
                    }
                }
            }

        } else {

            Column {

                OutlinedTextField(
                    value = tituloD,
                    onValueChange = {
                        tituloD = it
                    },
                    label = {
                        Text("Título Estratégico")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descD,
                    onValueChange = {
                        descD = it
                    },
                    label = {
                        Text("Descrição")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {

                        if (
                            tituloD.isNotBlank() &&
                            descD.isNotBlank()
                        ) {

                            if (diretrizEditando == null) {

                                onCreateDiretriz(
                                    tituloD,
                                    descD
                                )

                            } else {

                                onUpdateDiretriz(
                                    diretrizEditando!!.copy(
                                        titulo = tituloD,
                                        descricao = descD
                                    )
                                )

                                diretrizEditando = null
                            }

                            tituloD = ""
                            descD = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulAguia
                    )
                ) {

                    Text(
                        if (diretrizEditando == null)
                            "Publicar Estratégia"
                        else
                            "Salvar Alterações"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {

                    items(diretrizes) { diretriz ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween,

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        diretriz.titulo,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        diretriz.descricao
                                    )
                                }

                                IconButton(
                                    onClick = {

                                        diretrizEditando = diretriz

                                        tituloD = diretriz.titulo
                                        descD = diretriz.descricao
                                    }
                                ) {

                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = AzulAguia
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onDeleteDiretriz(
                                            diretriz.id
                                        )
                                    }
                                ) {

                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
