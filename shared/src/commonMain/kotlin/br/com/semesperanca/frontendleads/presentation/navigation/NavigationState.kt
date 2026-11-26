package br.com.semesperanca.frontendleads.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import kotlinx.serialization.serializer

class AppNavigationState(
    val startKey: AppNavKey,
    topLevelKeyState: MutableState<AppNavKey>,
    val backStacks: Map<AppNavKey, NavBackStack<AppNavKey>>,
) {
    var topLevelKey: AppNavKey by topLevelKeyState
        private set

    val currentBackStack: NavBackStack<AppNavKey>
        get() = backStacks.getValue(topLevelKey)

    val visibleBackStack: List<AppNavKey>
        get() =
            if (topLevelKey == startKey) {
                backStacks.getValue(startKey).toList()
            } else {
                backStacks.getValue(startKey).toList() + currentBackStack.toList()
            }

    internal fun switchTopLevel(key: AppNavKey) {
        topLevelKey = key
    }

    internal fun navigate(key: AppNavKey) {
        currentBackStack.add(key)
    }

    internal fun goBack(): Boolean =
        if (currentBackStack.size > 1) {
            currentBackStack.removeAt(currentBackStack.lastIndex)
            true
        } else if (topLevelKey != startKey) {
            topLevelKey = startKey
            true
        } else {
            false
        }
}

@Composable
private fun rememberAppNavBackStack(startKey: AppNavKey): NavBackStack<AppNavKey> =
    rememberSerializable(
        startKey,
        serializer = NavBackStackSerializer(serializer<AppNavKey>()),
    ) {
        NavBackStack(startKey)
    }

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    val topLevelKey = remember { mutableStateOf<AppNavKey>(topLevelKeys.first()) }

    @Suppress("UNCHECKED_CAST")
    val backStacks =
        topLevelKeys.associateWith { key ->
            rememberAppNavBackStack(key)
        }

    return remember(topLevelKey, backStacks) {
        AppNavigationState(
            startKey = topLevelKeys.first(),
            topLevelKeyState = topLevelKey,
            backStacks = backStacks,
        )
    }
}

class Navigator(
    private val state: AppNavigationState,
) {
    fun switchTopLevel(key: AppNavKey) = state.switchTopLevel(key)

    fun navigate(key: AppNavKey) = state.navigate(key)

    fun goBack(): Boolean = state.goBack()
}
