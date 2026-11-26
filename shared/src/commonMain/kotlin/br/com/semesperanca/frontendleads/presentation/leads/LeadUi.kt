package br.com.semesperanca.frontendleads.presentation.leads

import br.com.semesperanca.frontendleads.data.dto.LeadDto
import kotlinx.serialization.Serializable

@Serializable
data class LeadUi(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val propertyName: String,
    val propertyValue: Double,
    val nature: String,
    val transactionType: String,
    val isArchived: Boolean,
    val isFavorite: Boolean,
    val isNew: Boolean,
    val temperature: String = "",
    val source: String = "",
    val receivedAt: String = "",
    val message: String = "",
)

enum class LeadFilter(val label: String) {
    TODOS("Todos"),
    FUTUROS("Futuros"),
    FAVORITOS("Favoritos"),
    ARQUIVADOS("Arquivados"),
}

private fun formatPhone(digits: String): String =
    if (digits.isEmpty()) {
        digits
    } else {
        "(${digits.take(2)}) ${digits.substring(2, minOf(7, digits.length))}-${digits.substring(7, minOf(11, digits.length))}"
    }

fun LeadDto.toLeadUi(): LeadUi {
    val digits = phone?.toString().orEmpty().filter { it.isDigit() }
    val isSale = transactionType == "SALE"
    val nature = when {
        transactionType == "RENT" -> "Aluguel"
        else -> "Venda"
    }
    return LeadUi(
        id = id ?: "",
        name = name.orEmpty(),
        email = email.orEmpty(),
        phone = if (digits.isNotEmpty()) formatPhone(digits) else "",
        propertyName = "Imóvel",
        propertyValue = 0.0,
        nature = nature,
        transactionType = when {
            isSale -> "SALE"
            transactionType == "RENT" -> "RENT"
            else -> "SALE"
        },
        isArchived = status == "archived",
        isFavorite = false,
        isNew = false,
        temperature = temperature ?: "",
        source = source ?: "",
        receivedAt = createdAt ?: "",
        message = message ?: "",
    )
}

fun LeadUi.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.lowercase()
    return name.lowercase().contains(q) ||
        email.lowercase().contains(q) ||
        phone.contains(q) ||
        propertyName.lowercase().contains(q)
}

fun applyLocalFilters(leads: List<LeadUi>, filter: LeadFilter): List<LeadUi> {
    val filtered = when (filter) {
        LeadFilter.FAVORITOS -> leads.filter { it.isFavorite && !it.isArchived }
        LeadFilter.ARQUIVADOS -> leads.filter { it.isArchived }
        LeadFilter.FUTUROS -> emptyList()
        LeadFilter.TODOS -> leads.filter { !it.isArchived }
    }
    return filtered.sortedBy { it.id }
}