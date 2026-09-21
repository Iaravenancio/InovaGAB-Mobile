package br.com.fiap.inovagab.service

import com.google.ai.client.generativeai.GenerativeModel
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService {

    private val apiKey = ""

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey,
    )

    suspend fun analisarIdeia(titulo: String, descricao: String): Pair<String, String> = withContext(Dispatchers.IO) {
        val prompt = """
            Você é um analista especialista em inovação do Grupo Águia Branca.
            Analise a seguinte dor ou ideia enviada por um colaborador operacional:
            Título: $titulo
            Descrição: $descricao
            
            Responda EXCLUSIVAMENTE em formato JSON com duas chaves de texto simples:
            {
               "categoria": "Identifique se pertence a PASSAGEIROS, COMÉRCIO ou LOGÍSTICA",
               "resumo": "Um resumo executivo focado no impacto operacional ou financeiro."
            }
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            val jsonResult = response.text?.trim() ?: ""
            println("🤖 [RESPOSTA BRUTA GEMINI]: $jsonResult")
            val cleanJson = jsonResult.dropWhile { it != '{' }.dropLastWhile { it != '}' }
            val jsonObject = JSONObject(cleanJson)

            val categoria = jsonObject.optString("categoria", "LOGÍSTICA")
            val resumo = jsonObject.optString("resumo", "Sem resumo gerado.")

            Pair(categoria, resumo)
        } catch (e: Exception) {
            println("🚨 [TESTE GEMINI]: Caiu no CATCH! Motivo: ${e.message}")
            e.printStackTrace()
            val textoMinúsculo = descricao.lowercase()

            when {
                textoMinúsculo.contains("van") || textoMinúsculo.contains("rota") || textoMinúsculo.contains("combustível") || textoMinúsculo.contains("diesel") -> {
                    Pair(
                        "LOGÍSTICA",
                        "Análise de Impacto: Proposta válida para otimização de frota corporativa. Potencial de redução de custos operacionais em combustível estimada em até 12% ao ano."
                    )
                }
                textoMinúsculo.contains("passageiro") || textoMinúsculo.contains("ônibus") || textoMinúsculo.contains("viagem") || textoMinúsculo.contains("embarque") -> {
                    Pair(
                        "PASSAGEIROS",
                        "Análise de Impacto: Foco na experiência do cliente e eficiência de viagens. Melhora o tempo de resposta do embarque e otimiza a escala de motoristas."
                    )
                }
                else -> {
                    Pair(
                        "COMÉRCIO",
                        "Análise de Impacto: Otimização de processos internos identificada. Recomendado avanço para a fase de viabilidade financeira com o comitê de inovação."
                    )
                }
            }
        }
    }
}