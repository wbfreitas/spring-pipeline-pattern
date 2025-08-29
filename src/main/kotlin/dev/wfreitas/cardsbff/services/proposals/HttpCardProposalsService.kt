package dev.wfreitas.cardsbff.services.proposals

import dev.wfreitas.cardsbff.dto.CardProposalsResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class HttpCardProposalsService(
    @Value("\${endpoints.cards}") private val baseUrl: String,
    builder: RestClient.Builder
) : CardProposalsService {

    private val client = builder.baseUrl(baseUrl).build()

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "cardProposals")
    override fun getCardIdByCnpj(cnpj: String): String {
        val body = client.get()
            .uri { it.path("/proposals").queryParam("cnpj", cnpj).build() }
            .retrieve()
            .body(CardProposalsResponse::class.java)
            ?: throw IllegalStateException("Resposta inválida de propostas")

        return body.proposals.firstOrNull()?.id
            ?: throw IllegalStateException("Nenhuma proposta encontrada para o CNPJ")
    }
}
