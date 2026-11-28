package br.com.semesperanca.frontendleads.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import br.com.semesperanca.frontendleads.data.dto.ActivityDto
import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgencyDto
import br.com.semesperanca.frontendleads.data.dto.TeamDto
import br.com.semesperanca.frontendleads.presentation.activities.ActivityFormScreen
import br.com.semesperanca.frontendleads.presentation.activities.ActivitiesScreen
import br.com.semesperanca.frontendleads.presentation.activities.ActivitiesViewModel
import br.com.semesperanca.frontendleads.presentation.activities.activityFormDefaults
import br.com.semesperanca.frontendleads.presentation.activities.toCreateActivityRequest
import br.com.semesperanca.frontendleads.presentation.activities.toUpdateActivityRequest
import br.com.semesperanca.frontendleads.presentation.agencies.AgenciesTabScreen
import br.com.semesperanca.frontendleads.presentation.agencies.RealEstateAgencyFormScreen
import br.com.semesperanca.frontendleads.presentation.agencies.RealEstateAgencyViewModel
import br.com.semesperanca.frontendleads.presentation.agencies.realEstateAgencyFormDefaults
import br.com.semesperanca.frontendleads.presentation.agencies.toCreateRealEstateAgencyRequest
import br.com.semesperanca.frontendleads.presentation.agencies.toUpdateRealEstateAgencyRequest
import br.com.semesperanca.frontendleads.presentation.auth.LoginScreen
import br.com.semesperanca.frontendleads.presentation.auth.LoginViewModel
import br.com.semesperanca.frontendleads.presentation.branches.BranchOfficeFormScreen
import br.com.semesperanca.frontendleads.presentation.branches.BranchOfficeViewModel
import br.com.semesperanca.frontendleads.presentation.branches.branchOfficeFormDefaults
import br.com.semesperanca.frontendleads.presentation.branches.branchOfficeStatusOf
import br.com.semesperanca.frontendleads.presentation.branches.toCreateBranchOfficeRequest
import br.com.semesperanca.frontendleads.presentation.branches.toUpdateBranchOfficeRequest
import br.com.semesperanca.frontendleads.presentation.home.MainTabsScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadDetailScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadFormScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadsListScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadsViewModel
import br.com.semesperanca.frontendleads.presentation.leads.leadFormDefaults
import br.com.semesperanca.frontendleads.presentation.navigation.AppNavKey
import br.com.semesperanca.frontendleads.presentation.navigation.Navigator
import br.com.semesperanca.frontendleads.presentation.navigation.rememberAppNavigationState
import br.com.semesperanca.frontendleads.presentation.properties.PropertyFormScreen
import br.com.semesperanca.frontendleads.presentation.properties.PropertiesScreen
import br.com.semesperanca.frontendleads.presentation.sensors.AssociateDeviceFormScreen
import br.com.semesperanca.frontendleads.presentation.teams.TeamFormScreen
import br.com.semesperanca.frontendleads.presentation.teams.TeamsScreen
import br.com.semesperanca.frontendleads.presentation.teams.TeamsViewModel
import br.com.semesperanca.frontendleads.presentation.teams.teamFormDefaults
import br.com.semesperanca.frontendleads.presentation.teams.toCreateTeamRequest
import br.com.semesperanca.frontendleads.presentation.teams.toUpdateTeamRequest
import br.com.semesperanca.frontendleads.presentation.users.UserFormSheet
import br.com.semesperanca.frontendleads.presentation.users.UsersScreen
import br.com.semesperanca.frontendleads.presentation.users.UsersViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = koinViewModel()
    val authState by loginViewModel.uiState.collectAsState()

    if (!authState.isAuthenticated) {
        LoginScreen(viewModel = loginViewModel)
        return
    }

    val navigationState = rememberAppNavigationState()
    val navigator = remember(navigationState) { Navigator(navigationState) }

    MainTabsScreen(
        currentTopLevel = navigationState.topLevelKey,
        onTabClick = { navigator.switchTopLevel(it) },
        onLogout = loginViewModel::logout,
    ) {
        NavDisplay(
            backStack = navigationState.visibleBackStack,
            onBack = { navigator.goBack() },
            entryProvider =
                entryProvider<AppNavKey> {
                    entry<AppNavKey.Leads> {
                        LeadsListScreen(
                            onLeadClick = { navigator.navigate(AppNavKey.LeadDetail(it)) },
                            onCreate = { navigator.navigate(AppNavKey.LeadForm) },
                        )
                    }

                    entry<AppNavKey.LeadForm> {
                        val values =
                            remember {
                                mutableStateMapOf<String, String>().apply { putAll(leadFormDefaults()) }
                            }
                        LeadFormScreen(
                            values = values,
                            onValueChange = { key, value -> values[key] = value },
                            onSubmit = { navigator.goBack() },
                            onDismiss = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.LeadDetail> { key ->
                        val leadsViewModel: LeadsViewModel = koinViewModel()
                        LeadDetailScreen(
                            lead = key.lead,
                            onBack = { navigator.goBack() },
                            onArchive = leadsViewModel::archiveLead,
                            onUnarchive = leadsViewModel::unarchiveLead,
                        )
                    }

                    entry<AppNavKey.Teams> {
                        TeamsScreen(
                            onCreate = { navigator.navigate(AppNavKey.TeamForm()) },
                            onEdit = { team: TeamDto ->
                                navigator.navigate(
                                    AppNavKey.TeamForm(id = team.id, values = teamFormDefaults(team)),
                                )
                            },
                        )
                    }

                    entry<AppNavKey.TeamForm> { key ->
                        val viewModel: TeamsViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()
                        val values =
                            remember(key) {
                                mutableStateMapOf<String, String>().apply { putAll(key.values) }
                            }
                        TeamFormScreen(
                            isEditing = key.id != null,
                            values = values,
                            onValueChange = { k, v -> values[k] = v },
                            isLoading = state.isLoading,
                            error = state.error,
                            onSubmit = {
                                if (key.id != null) {
                                    viewModel.update(key.id, toUpdateTeamRequest(values))
                                } else {
                                    viewModel.create(toCreateTeamRequest(values))
                                }
                                navigator.goBack()
                            },
                            onDismiss = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.Activities> {
                        ActivitiesScreen(
                            onCreate = { navigator.navigate(AppNavKey.ActivityForm()) },
                            onEdit = { activity: ActivityDto ->
                                navigator.navigate(
                                    AppNavKey.ActivityForm(id = activity.id, values = activityFormDefaults(activity)),
                                )
                            },
                        )
                    }

                    entry<AppNavKey.ActivityForm> { key ->
                        val viewModel: ActivitiesViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()
                        val values =
                            remember(key) {
                                mutableStateMapOf<String, String>().apply { putAll(key.values) }
                            }
                        ActivityFormScreen(
                            isEditing = key.id != null,
                            values = values,
                            onValueChange = { k, v -> values[k] = v },
                            isLoading = state.isLoading,
                            error = state.error,
                            onSubmit = {
                                if (key.id != null) {
                                    viewModel.update(key.id, toUpdateActivityRequest(values))
                                } else {
                                    viewModel.create(toCreateActivityRequest(values))
                                }
                                navigator.goBack()
                            },
                            onDismiss = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.Properties> {
                        PropertiesScreen(
                            onCreateProperty = { navigator.navigate(AppNavKey.PropertyForm) },
                            onAssociateDevice = { navigator.navigate(AppNavKey.AssociateDevice) },
                        )
                    }

                    entry<AppNavKey.PropertyForm> {
                        PropertyFormScreen(
                            title = "Novo Imóvel",
                            onBack = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.AssociateDevice> {
                        AssociateDeviceFormScreen(onBack = { navigator.goBack() })
                    }

                    entry<AppNavKey.Agencies> {
                        AgenciesTabScreen(
                            onAgencyCreate = { navigator.navigate(AppNavKey.AgencyForm()) },
                            onAgencyEdit = { agency: RealEstateAgencyDto ->
                                navigator.navigate(
                                    AppNavKey.AgencyForm(id = agency.id, values = realEstateAgencyFormDefaults(agency)),
                                )
                            },
                            onBranchCreate = { navigator.navigate(AppNavKey.BranchOfficeForm()) },
                            onBranchEdit = { office: BranchOfficeDto ->
                                navigator.navigate(
                                    AppNavKey.BranchOfficeForm(
                                        id = office.id,
                                        values = branchOfficeFormDefaults(office),
                                        wasActive = office.active,
                                    ),
                                )
                            },
                        )
                    }

                    entry<AppNavKey.AgencyForm> { key ->
                        val viewModel: RealEstateAgencyViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()
                        val values =
                            remember(key) {
                                mutableStateMapOf<String, String>().apply { putAll(key.values) }
                            }
                        RealEstateAgencyFormScreen(
                            isEditing = key.id != null,
                            values = values,
                            onValueChange = { k, v -> values[k] = v },
                            isLoading = state.isLoading,
                            error = state.error,
                            onSubmit = {
                                if (key.id != null) {
                                    viewModel.update(key.id, toUpdateRealEstateAgencyRequest(values))
                                } else {
                                    viewModel.create(toCreateRealEstateAgencyRequest(values))
                                }
                                navigator.goBack()
                            },
                            onDismiss = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.BranchOfficeForm> { key ->
                        val viewModel: BranchOfficeViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()
                        val values =
                            remember(key) {
                                mutableStateMapOf<String, String>().apply { putAll(key.values) }
                            }
                        BranchOfficeFormScreen(
                            isEditing = key.id != null,
                            values = values,
                            onValueChange = { k, v -> values[k] = v },
                            isLoading = state.isLoading,
                            error = state.error,
                            onSubmit = {
                                if (key.id != null) {
                                    viewModel.update(
                                        id = key.id,
                                        request = toUpdateBranchOfficeRequest(values),
                                        active = branchOfficeStatusOf(values),
                                        wasActive = key.wasActive,
                                    )
                                } else {
                                    viewModel.create(toCreateBranchOfficeRequest(values))
                                }
                                navigator.goBack()
                            },
                            onDismiss = { navigator.goBack() },
                        )
                    }

                    entry<AppNavKey.Users> {
                        UsersScreen(onCreate = { navigator.navigate(AppNavKey.UserForm) })
                    }

                    entry<AppNavKey.UserForm> {
                        val viewModel: UsersViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()
                        UserFormSheet(
                            onDismiss = { navigator.goBack() },
                            onSave = { user, password ->
                                viewModel.addUser(user, password)
                                navigator.goBack()
                            },
                            branchOffices = state.branchOffices,
                            agencies = state.agencies,
                        )
                    }
                },
        )
    }
}
