package dev.wfreitas.cardsbff.orchestration.steps.impl

import dev.wfreitas.cardsbff.orchestration.*
import dev.wfreitas.cardsbff.services.points.PointsService
import org.springframework.stereotype.Component

@Component
class FetchPointsStepImpl(
    private val points: PointsService
) : dev.wfreitas.cardsbff.orchestration.steps.FetchPointsStep {
    override fun name() = "FetchPoints"

    override fun execute(ctx: CardCtx): Result<CardCtx> = runCatching {
        val cardId = requireNotNull(ctx.cardId) { "Pré-requisito ausente: cardId" }
        val total = points.getPoints(cardId)
        ctx.copy(points = total)
    }
}
