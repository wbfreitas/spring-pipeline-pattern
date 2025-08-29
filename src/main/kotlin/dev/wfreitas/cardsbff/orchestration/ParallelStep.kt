package dev.wfreitas.cardsbff.orchestration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class ParallelStep {
    fun execute(base: CardCtx, steps: List<Step>): Result<CardCtx> =
        runBlocking(Dispatchers.IO) {
            val results = steps.map { s -> async { s.execute(base) } }.awaitAll()
            val failure = results.firstOrNull { it.isFailure }?.exceptionOrNull()
            if (failure != null) Result.failure(failure)
            else Result.success(results.map { it.getOrThrow() }.fold(base) { acc, ctx -> acc.merge(ctx) })
        }
}
