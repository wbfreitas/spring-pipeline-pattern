package dev.wfreitas.cardsbff.orchestration.steps.impl

import dev.wfreitas.cardsbff.orchestration.*
import dev.wfreitas.cardsbff.services.holders.HoldersService
import org.springframework.stereotype.Component

@Component
class FetchHoldersStepImpl(
    private val holders: HoldersService
) : dev.wfreitas.cardsbff.orchestration.steps.FetchHoldersStep {
    override fun name() = "FetchHolders"

    override fun execute(ctx: CardCtx): Result<CardCtx> = runCatching {
        val cardId = requireNotNull(ctx.cardId) { "Pré-requisito ausente: cardId" }
        val list = holders.getHolders(cardId)
        ctx.copy(holders = list)
    }
}
