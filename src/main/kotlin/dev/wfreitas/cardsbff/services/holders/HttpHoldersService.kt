package dev.wfreitas.cardsbff.services.holders

import dev.wfreitas.cardsbff.dto.Cardholder
import dev.wfreitas.cardsbff.dto.CardholdersResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class HttpHoldersService(
    @Value("\${endpoints.holders}") private val baseUrl: String,
    builder: RestClient.Builder
) : HoldersService {

    private val client = builder.baseUrl(baseUrl).build()

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "holders")
    override fun getHolders(cardId: String): List<Cardholder> {
        // The holders API nests the list of portadores under a data.portadores object.
        val body = client.get()
            .uri("/cards/{cardId}/holders", cardId)
            .retrieve()
            .body(CardholdersResponse::class.java)
            ?: throw IllegalStateException("Resposta inválida de portadores")
        return body.data.portadores
    }
}
