package dev.wfreitas.cardsbff.services.proposals

interface CardProposalsService {
    fun getCardIdByCnpj(cnpj: String): String
}
