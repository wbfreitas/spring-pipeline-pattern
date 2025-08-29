package dev.wfreitas.cardsbff.orchestration

import dev.wfreitas.cardsbff.dto.CardSummaryResponse
import dev.wfreitas.cardsbff.security.SaltedCrypto
import org.springframework.stereotype.Component

/**
 * O SummaryBuilder é responsável por transformar um [CardCtx] enriquecido
 * no DTO exposto pelo BFF. Ele aplica mascaramento e criptografia
 * às informações confidenciais e valida se todos os valores necessários estão
 * presentes.
 */
@Component
class SummaryBuilder(private val crypto: SaltedCrypto) {

    /**
     * Cria uma [CardSummaryResponse] a partir do contexto fornecido.
     *
     * @lança IllegalStateException se alguma propriedade obrigatória estiver faltando no contexto
     */
    fun build(ctx: CardCtx): CardSummaryResponse {
        val cnpj = requireNotNull(ctx.cnpj) { "CNPJ não encontrado no contexto" }
        val total = requireNotNull(ctx.points) { "Total de pontos não encontrado no contexto" }
        val holders = requireNotNull(ctx.holders) { "Lista de portadores não encontrada no contexto" }

        val outHolders = holders.map { h ->
            val digits = onlyDigits(h.cpf)
            // Mask the last three digits of the CPF.  If you need to mask
            // the first three digits instead, replace `takeLast` with `take`.
            val last3 = digits.takeLast(3).padStart(3, '0')
            CardSummaryResponse.HolderOut(
                nome = h.name.lowercase(),
                ultimos_digito_cpf = last3,
                cpf_cryp = crypto.hmacSha256(digits)
            )
        }

        return CardSummaryResponse(
            cnpj_crypto = crypto.hmacSha256(onlyDigits(cnpj)),
            total_pontos = total,
            portadores = outHolders
        )
    }

    private fun onlyDigits(s: String) = s.filter { it.isDigit() }
}