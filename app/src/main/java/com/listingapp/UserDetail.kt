package com.listingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserDetails(
    app: ListApp,
    topBarState: MutableState<AppBarViewState>,
    onReturn: () -> Unit,
) {
    val viewModel: AppViewModel = viewModel()
    onReturn()
    LaunchedEffect(key1 = Unit) {
        viewModel.UpdateTopBar(app, topBarState)
    }
}