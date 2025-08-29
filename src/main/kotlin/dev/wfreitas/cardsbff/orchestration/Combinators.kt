package dev.wfreitas.cardsbff.orchestration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

/**
 * Um Step representa uma única operação síncrona que transforma um contexto do tipo [Ctx].
 * Cada passo retorna um [Resultado] que pode ser um contexto enriquecido com sucesso ou uma falha
 * envolvendo a exceção encontrada durante a execução.
 */
/**
 * Um PipelineStep representa uma operação síncrona que transforma um contexto do tipo [Ctx].
 * Cada passo retorna um [Resultado] que pode ser um contexto enriquecido com sucesso ou uma falha
 * envolvendo a exceção encontrada durante a execução.
 */
typealias PipelineStep<Ctx> = (Ctx) -> Result<Ctx>

/**
* Compõe esta etapa com [next] em sequência. Se esta etapa for bem-sucedida, a etapa [next] é
* invocada com o contexto enriquecido; caso contrário, a falha é propagada.
*/
infix fun <Ctx> PipelineStep<Ctx>.then(next: PipelineStep<Ctx>): PipelineStep<Ctx> = { ctx ->
    this(ctx).fold(onSuccess = { next(it) }, onFailure = { Result.failure(it) })
}

/**
 * Cria uma única etapa que executa as [etapas] fornecidas sequencialmente. A execução para na
 * primeira falha e a falha é retornada.
 */
fun <Ctx> seq(vararg steps: PipelineStep<Ctx>): PipelineStep<Ctx> =
    steps.reduce { acc, s -> acc then s }

/**
 * Executa as [etapas] fornecidas em paralelo no mesmo contexto base. Cada etapa deve ser
 * independente e não deve modificar o contexto base diretamente. Após a conclusão de todas as etapas, os
 * contextos resultantes são mesclados em um novo contexto usando a função [merge] fornecida.
 */
fun <Ctx> par(
    vararg steps: PipelineStep<Ctx>,
    merge: (base: Ctx, parts: List<Ctx>) -> Ctx
): PipelineStep<Ctx> = { base ->
    runBlocking(Dispatchers.IO) {
        val results = steps.map { async { it(base) } }.awaitAll()
        val failure = results.firstOrNull { it.isFailure }?.exceptionOrNull()
        if (failure != null) Result.failure(failure)
        else Result.success(merge(base, results.map { it.getOrThrow() }))
    }
}