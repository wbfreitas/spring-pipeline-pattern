package dev.wfreitas.cardsbff.orchestration

import dev.wfreitas.cardsbff.dto.CardSummaryResponse
import org.springframework.stereotype.Service

/**
 * CardSummaryService fica à frente de [CardSummaryPipeline] e
 * [SummaryBuilder]. Ele coordena a execução do pipeline e, em seguida,
 * delega ao construtor a tarefa de produzir a resposta final. Quaisquer exceções
 * geradas pelo pipeline ou construtor são convertidas em um [Outcome.Err]
 * com um código de status HTTP significativo.
 */
@Service
class CardSummaryService(
    private val pipeline: CardSummaryPipeline,
    private val builder: SummaryBuilder
) {
    /**
     * Executa o pipeline e cria a resposta final. É necessário apenas um único identificador
     * (userId), que é usado como chave de consulta para a API principal do cliente.
     */
    fun summary(requestId: String, userId: String): Outcome<CardSummaryResponse> {
        return try {
            pipeline.run(requestId, userId).fold(
                onSuccess = { ctx -> Outcome.Ok(builder.build(ctx)) },
                onFailure = { ex -> Outcome.Err(httpStatusOf(ex), ex.message ?: "erro") }
            )
        } catch (e: Exception) {
            Outcome.Err(httpStatusOf(e), e.message ?: "erro")
        }
    }

    private fun httpStatusOf(e: Throwable): Int = when (e) {
        is IllegalArgumentException -> 400
        is IllegalStateException -> 404
        else -> 502
    }
}