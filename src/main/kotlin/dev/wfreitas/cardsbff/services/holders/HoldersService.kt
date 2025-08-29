package dev.wfreitas.cardsbff.services.holders

import dev.wfreitas.cardsbff.dto.Cardholder

interface HoldersService {
    fun getHolders(cardId: String): List<Cardholder>
}
