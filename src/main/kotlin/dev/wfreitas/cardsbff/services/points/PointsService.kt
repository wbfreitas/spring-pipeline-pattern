package dev.wfreitas.cardsbff.services.points

interface PointsService {
    fun getPoints(cardId: String): Long
}
