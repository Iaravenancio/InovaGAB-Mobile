package br.com.fiap.inovagab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import br.com.fiap.inovagab.data.api.RetrofitClient
import br.com.fiap.inovagab.ui.MainNavContainer
import br.com.fiap.inovagab.viewmodel.InovaGABViewModel

class MainActivity : ComponentActivity() {

    // Inicialização da nossa ViewModel que conecta o Firebase e o Gemini
    private val viewModel: InovaGABViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.inicializar(this)
        setContent {
            // Executa a interface injetando o motor do projeto
            MainNavContainer(viewModel = viewModel)
        }
    }
}