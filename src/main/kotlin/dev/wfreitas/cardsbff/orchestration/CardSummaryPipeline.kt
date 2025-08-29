package dev.wfreitas.cardsbff.orchestration

import dev.wfreitas.cardsbff.orchestration.steps.FetchCardIdStep
import dev.wfreitas.cardsbff.orchestration.steps.FetchCnpjStep
import dev.wfreitas.cardsbff.orchestration.steps.FetchHoldersStep
import dev.wfreitas.cardsbff.orchestration.steps.FetchPointsStep
import org.springframework.stereotype.Component

/**
 * O CardSummaryPipeline é responsável por orquestrar as etapas individuais
 * necessárias para compor o resumo do cartão. Ele não cria a
 * resposta final; em vez disso, enriquece um [CardCtx] executando cada etapa e
 * delega a renderização a um [SummaryBuilder] separado.
 *
 * O pipeline executa o seguinte em ordem:
 * 1. Busca o CNPJ do cliente usando o identificador fornecido (userId).
 * 2. Busca o cardId associado ao CNPJ.
 * 3. Paralelamente, busca o total de pontos e a lista de titulares do cartão.
 */

@Component
class CardSummaryPipeline(
    fetchCnpj: FetchCnpjStep,
    fetchCardId: FetchCardIdStep,
    fetchPoints: FetchPointsStep,
    fetchHolders: FetchHoldersStep
) {
    /**
     * Cria um pipeline declarativo usando combinadores. O pipeline
     * ​​executa fetchCnpj, depois fetchCardId e, por fim, fetchPoints
     * e fetchHolders em paralelo. Cada etapa individual é encapsulada
     * em um adaptador [Step] para se adequar ao alias do tipo funcional.
     */
    private val pipeline: PipelineStep<CardCtx> = seq(
        { ctx -> fetchCnpj.execute(ctx) },
        { ctx -> fetchCardId.execute(ctx) },
        par(
            { ctx: CardCtx -> fetchPoints.execute(ctx) },
            { ctx: CardCtx -> fetchHolders.execute(ctx) },
            merge = { base: CardCtx, parts: List<CardCtx> -> parts.fold(base) { acc, p -> acc.merge(p) } }
        )
    )

    /**
     * Executa o pipeline com base nos identificadores fornecidos pelo controlador. Em caso de
     * sucesso, um [Result.success] contendo um [CardCtx] enriquecido é
     * retornado. Se alguma etapa falhar, o erro é capturado em um
     * [Result.failure]. O chamador é responsável por traduzir isso em
     * uma resposta HTTP apropriada.
     */
    fun run(requestId: String, userId: String): Result<CardCtx> {
        val ctx0 = CardCtx(requestId = requestId, userId = userId)
        return pipeline(ctx0)
    }
}