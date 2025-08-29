package dev.wfreitas.cardsbff.dto

//
// O BFF não aceita mais um payload JSON. A única entrada agora vem de
// um cabeçalho HTTP (X-User-Id). Portanto, a classe de dados CardSummaryRequest
// foi removida. Se você precisar transportar campos adicionais
// pela camada de orquestração no futuro, adicione-os diretamente a
// CardCtx no pacote de orquestração.

// CustomerCoreResponse modela o formato retornado pela API do núcleo do cliente. A
// API aninha o número do documento em um campo "data". Por exemplo:
// {
// "data": {
// "cnpj": "12345678000199"
// }
// }
//
// Se sua API retornar um campo chamado "cpf" em vez de "cnpj", ajuste
// CustomerCoreData adequadamente.
data class CustomerCoreResponse(val data: CustomerCoreData) {
    data class CustomerCoreData(val cnpj: String)
}

data class CardProposal(val id: String, val product: String)
data class CardProposalsResponse(val proposals: List<CardProposal> = emptyList())

// PointsBalanceResponse modela a resposta aninhada retornada pela API de pontos. O
// número total de pontos é retornado em um objeto "beneficios". Exemplo:
// {
// "beneficios": {
// "totaldepontos": 150
// }
// }
data class PointsBalanceResponse(val beneficios: Beneficios) {
    data class Beneficios(val totaldepontos: Long)
}

data class Cardholder(val name: String, val cpf: String)

// CardholdersResponse modela a resposta aninhada retornada pela API de titulares. A
// lista de titulares de cartão é retornada sob um objeto "data". Exemplo:
// {
// "data": {
// "portadores": [ { "name": "Maria", "cpf": "123" }, ... ]
// }
// }
data class CardholdersResponse(val data: HoldersData) {
    data class HoldersData(val portadores: List<Cardholder> = emptyList())
}

data class CardSummaryResponse(
    val cnpj_crypto: String,
    val total_pontos: Long,
    val portadores: List<HolderOut>
) {
    data class HolderOut(
        val nome: String,
        val ultimos_digito_cpf: String,
        val cpf_cryp: String
    )
}
