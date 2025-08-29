package dev.wfreitas.cardsbff.services.customer

interface CustomerCoreService {
    fun getCnpjByCustomerId(customerId: String): String
}
