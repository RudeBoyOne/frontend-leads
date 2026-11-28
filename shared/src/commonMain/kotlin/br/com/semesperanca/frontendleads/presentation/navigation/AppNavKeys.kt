package br.com.semesperanca.frontendleads.presentation.navigation

import androidx.navigation3.runtime.NavKey
import br.com.semesperanca.frontendleads.presentation.leads.LeadUi
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {

    @Serializable
    data object Leads : AppNavKey

    @Serializable
    data object Teams : AppNavKey

    @Serializable
    data object Activities : AppNavKey

    @Serializable
    data object Properties : AppNavKey

    @Serializable
    data object Agencies : AppNavKey

    @Serializable
    data object Users : AppNavKey

    @Serializable
    data object LeadForm : AppNavKey

    @Serializable
    data class LeadDetail(val lead: LeadUi) : AppNavKey

    @Serializable
    data class TeamForm(val id: String? = null, val values: Map<String, String> = emptyMap()) : AppNavKey

    @Serializable
    data class ActivityForm(val id: String? = null, val values: Map<String, String> = emptyMap()) : AppNavKey

    @Serializable
    data class PropertyForm(val id: String? = null, val values: Map<String, String> = emptyMap()) : AppNavKey

    @Serializable
    data object AssociateDevice : AppNavKey

    @Serializable
    data class AgencyForm(val id: String? = null, val values: Map<String, String> = emptyMap()) : AppNavKey

    @Serializable
    data class BranchOfficeForm(
        val id: String? = null,
        val values: Map<String, String> = emptyMap(),
        val wasActive: Boolean = false,
    ) : AppNavKey

    @Serializable
    data object UserForm : AppNavKey
}

val topLevelKeys: List<AppNavKey> =
    listOf(
        AppNavKey.Leads,
        AppNavKey.Teams,
        AppNavKey.Activities,
        AppNavKey.Properties,
        AppNavKey.Agencies,
        AppNavKey.Users,
    )
