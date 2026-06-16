package com.example.pokedex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokedex.ui.screens.BuildScreen
import com.example.pokedex.ui.screens.GuideScreen
import com.example.pokedex.ui.screens.HomeScreen
import com.example.pokedex.ui.screens.PokemonDetailScreen
import com.example.pokedex.ui.screens.PokemonScreen
import com.example.pokedex.ui.screens.PokemonViewModel
import com.example.pokedex.ui.screens.TierScreen

@Composable
fun PokedexNavGraph(navController: NavHostController) {
    val pokemonViewModel: PokemonViewModel = viewModel()
    NavHost(navController = navController, startDestination = "home_route") {
        composable("home_route") {
            HomeScreen(navController = navController)
        }
        composable("pokedex_route") {
            PokemonScreen(navController = navController, viewModel = pokemonViewModel)
        }
        composable(
            route = "pokemon_detail/{pokemonId}",
            arguments = listOf(navArgument("pokemonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId") ?: ""
            PokemonDetailScreen(pokemonId = pokemonId, navController = navController, viewModel = pokemonViewModel)
        }
        composable("item_route") {
            com.example.pokedex.ui.screens.ItemScreen(navController = navController)
        }
        composable("build_route") {
            BuildScreen(navController = navController)
        }
        composable("tier_route") {
            TierScreen(navController = navController, viewModel = pokemonViewModel)
        }
        composable("guide_route") {
            GuideScreen(navController = navController)
        }
    }
}