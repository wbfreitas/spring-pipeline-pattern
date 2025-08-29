package dev.wfreitas.cardsbff.orchestration.steps.impl

import dev.wfreitas.cardsbff.orchestration.*
import dev.wfreitas.cardsbff.services.proposals.CardProposalsService
import org.springframework.stereotype.Component

@Component
class FetchCardIdStepImpl(
    private val proposals: CardProposalsService
) : dev.wfreitas.cardsbff.orchestration.steps.FetchCardIdStep {
    override fun name() = "FetchCardId"

    override fun execute(ctx: CardCtx): Result<CardCtx> = runCatching {
        val cnpj = requireNotNull(ctx.cnpj) { "Pré-requisito ausente: CNPJ" }
        val cardId = proposals.getCardIdByCnpj(cnpj)
        ctx.copy(cardId = cardId)
    }
}
