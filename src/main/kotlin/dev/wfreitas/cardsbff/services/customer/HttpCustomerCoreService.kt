package dev.wfreitas.cardsbff.services.customer

import dev.wfreitas.cardsbff.dto.CustomerCoreResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class HttpCustomerCoreService(
    @Value("\${endpoints.customerCore}") private val baseUrl: String,
    builder: RestClient.Builder
) : CustomerCoreService {

    private val client = builder.baseUrl(baseUrl).build()

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "customerCore")
    override fun getCnpjByCustomerId(customerId: String): String {
        // A API do núcleo do cliente retorna o documento dentro de um objeto de dados. Por exemplo:
        // {
        // "data": { "cnpj": "12345678000199" }
        // }
        val body = client.get()
            .uri("/customers/{id}", customerId)
            .retrieve()
            .body(CustomerCoreResponse::class.java)
            ?: throw IllegalStateException("Cliente não encontrado")
        return body.data.cnpj
    }
}
