package dev.wfreitas.cardsbff.api

import dev.wfreitas.cardsbff.orchestration.Outcome
import dev.wfreitas.cardsbff.orchestration.CardSummaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/bff/v1/card")
class CardSummaryController(
    private val service: CardSummaryService
) {

    @PostMapping("/summary")
    fun summary(
        @RequestHeader("X-User-Id") userId: String
    ): ResponseEntity<Any> {
        val reqId = UUID.randomUUID().toString()
        return when (val out = service.summary(reqId, userId)) {
            is Outcome.Ok -> ResponseEntity.ok(out.value)
            is Outcome.Err -> ResponseEntity.status(out.status).body(
                mapOf(
                    "error" to out.message,
                    "requestId" to reqId
                )
            )
        }
    }
}
