package dev.wfreitas.cardsbff.orchestration.steps.impl

import dev.wfreitas.cardsbff.orchestration.*
import dev.wfreitas.cardsbff.services.customer.CustomerCoreService
import org.springframework.stereotype.Component

@Component
class FetchCnpjStepImpl(
    private val customerCore: CustomerCoreService
) : dev.wfreitas.cardsbff.orchestration.steps.FetchCnpjStep {
    override fun name() = "FetchCnpj"

    override fun execute(ctx: CardCtx): Result<CardCtx> = runCatching {
        // Use o userId como chave de pesquisa para a API principal do cliente. A API retorna o
        // documento da empresa na propriedade data.cnpj. O valor retornado pode incluir
        // caracteres de formatação, portanto, filtramos apenas para dígitos.
        val cnpj = onlyDigits(customerCore.getCnpjByCustomerId(ctx.userId))
        ctx.copy(cnpj = cnpj)
    }

    private fun onlyDigits(s: String) = s.filter { it.isDigit() }
}
