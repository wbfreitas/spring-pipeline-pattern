package dev.wfreitas.cardsbff.services.points

import dev.wfreitas.cardsbff.dto.PointsBalanceResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class HttpPointsService(
    @Value("\${endpoints.points}") private val baseUrl: String,
    builder: RestClient.Builder
) : PointsService {

    private val client = builder.baseUrl(baseUrl).build()

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "points")
    override fun getPoints(cardId: String): Long {
        // The points API nests the total points under a "beneficios.totaldepontos" object.
        val body = client.get()
            .uri("/cards/{cardId}/points", cardId)
            .retrieve()
            .body(PointsBalanceResponse::class.java)
            ?: throw IllegalStateException("Resposta inválida de pontos")
        return body.beneficios.totaldepontos
    }
}
