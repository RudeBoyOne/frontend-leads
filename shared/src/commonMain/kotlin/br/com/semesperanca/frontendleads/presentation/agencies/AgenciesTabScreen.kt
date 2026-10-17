package br.com.semesperanca.frontendleads.presentation.agencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.semesperanca.frontendleads.presentation.branches.BranchOfficesScreen
import br.com.semesperanca.frontendleads.presentation.ui.FilterItem
import br.com.semesperanca.frontendleads.presentation.ui.FilterRow
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.domain
import br.com.semesperanca.frontendleads.resources.storefront

private val AGENCY_FILTERS =
    listOf(
        FilterItem("Agências", Res.drawable.domain),
        FilterItem("Filiais", Res.drawable.storefront),
    )

@Composable
fun AgenciesTabScreen() {
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        FilterRow(
            items = AGENCY_FILTERS,
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
            itemSize = 48.dp,
            bottomPadding = 12.dp,
        )

        when (selectedIndex) {
            0 -> RealEstateAgenciesScreen()
            else -> BranchOfficesScreen()
        }
    }
}
