package br.com.semesperanca.frontendleads.di

import br.com.semesperanca.frontendleads.data.remote.ActivityApi
import br.com.semesperanca.frontendleads.data.remote.ApiClient
import br.com.semesperanca.frontendleads.data.remote.BranchOfficeApi
import br.com.semesperanca.frontendleads.data.remote.LeadApi
import br.com.semesperanca.frontendleads.data.remote.RealEstateAgencyApi
import br.com.semesperanca.frontendleads.data.remote.TeamApi
import br.com.semesperanca.frontendleads.data.repository.DefaultLeadRepository
import br.com.semesperanca.frontendleads.data.storage.KeyValueStore
import br.com.semesperanca.frontendleads.data.storage.createKeyValueStore
import br.com.semesperanca.frontendleads.domain.repository.LeadRepository
import br.com.semesperanca.frontendleads.presentation.activities.ActivitiesViewModel
import br.com.semesperanca.frontendleads.presentation.agencies.RealEstateAgencyViewModel
import br.com.semesperanca.frontendleads.presentation.auth.LoginViewModel
import br.com.semesperanca.frontendleads.presentation.branches.BranchOfficeViewModel
import br.com.semesperanca.frontendleads.presentation.leads.LeadDetailViewModel
import br.com.semesperanca.frontendleads.presentation.leads.LeadsViewModel
import br.com.semesperanca.frontendleads.presentation.properties.PropertyFormViewModel
import br.com.semesperanca.frontendleads.presentation.teams.TeamsViewModel
import br.com.semesperanca.frontendleads.presentation.users.UsersViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val dataModule =
    module {
        single<KeyValueStore> { createKeyValueStore() }
        single { ApiClient() }
        single { LeadApi(get()) }
        single { TeamApi(get()) }
        single { ActivityApi(get()) }
        single { RealEstateAgencyApi(get()) }
        single { BranchOfficeApi(get()) }
        single<LeadRepository> { DefaultLeadRepository(get()) }
    }

private val presentationModule =
    module {
        viewModelOf(::LeadsViewModel)
        viewModelOf(::LeadDetailViewModel)
        viewModelOf(::PropertyFormViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::UsersViewModel)
        viewModelOf(::TeamsViewModel)
        viewModelOf(::ActivitiesViewModel)
        viewModelOf(::RealEstateAgencyViewModel)
        viewModelOf(::BranchOfficeViewModel)
    }

object AppModules {
    val all: List<Module> = listOf(dataModule, presentationModule)

    fun init() {
        startKoin { modules(all) }
    }
}
