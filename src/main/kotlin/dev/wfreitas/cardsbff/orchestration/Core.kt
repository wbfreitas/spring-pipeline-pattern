package dev.wfreitas.cardsbff.orchestration

// No longer imports CardSummaryRequest because the pipeline receives only header values.
import dev.wfreitas.cardsbff.dto.Cardholder

data class CardCtx(
    val requestId: String,
    /**
     * Identificador propagado do controlador. Neste exemplo simplificado, o mesmo
     * identificador é usado para procurar o cliente na API principal do cliente. Se o seu
     * domínio distingue entre um ID de usuário e um ID de cliente, você pode adicionar
     * campos adicionais aqui, conforme necessário. Para este BFF, carregamos apenas um único cabeçalho e o usamos
     * consistentemente em todo o pipeline.
     */
    val userId: String,
    val cnpj: String? = null,
    val cardId: String? = null,
    val points: Long? = null,
    val holders: List<Cardholder>? = null
) {

    /**
     * Cria um novo contexto mesclando os campos de enriquecimento não nulos do outro contexto.
     * Isso permite que etapas executadas em paralelo atualizem apenas as partes pelas quais são responsáveis
     * sem perder informações já presentes no contexto base.
     */
    fun merge(other: CardCtx) = copy(
        cnpj = other.cnpj ?: cnpj,
        cardId = other.cardId ?: cardId,
        points = other.points ?: points,
        holders = other.holders ?: holders
    )
}

interface Step {
    fun name(): String
    fun execute(ctx: CardCtx): Result<CardCtx>
}

sealed interface Outcome<out T> {
    data class Ok<T>(val value: T) : Outcome<T>
    data class Err(val status: Int, val message: String) : Outcome<Nothing>
}
