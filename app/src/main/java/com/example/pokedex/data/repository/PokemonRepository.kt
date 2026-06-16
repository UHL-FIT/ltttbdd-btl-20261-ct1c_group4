package com.example.pokedex.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokedex.data.PokemonCustomData
import com.example.pokedex.data.local.PokemonDatabase
import com.example.pokedex.data.remote.ChainLink
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.model.EvolutionStep
import com.example.pokedex.model.MoveInfo
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonSummary
import com.example.pokedex.util.TranslatorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokemonRepository(context: Context) {
    private val database = PokemonDatabase.getDatabase(context)
    private val pokemonDao = database.pokemonDao()
    private val itemDao = database.itemDao()

    private val apiService = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PokeApiService::class.java)

    private val moveDetailCache = mutableMapOf<String, com.example.pokedex.data.remote.MoveDetailResponse>()
    private val abilityDetailCache = mutableMapOf<String, com.example.pokedex.data.remote.AbilityDetailResponse>()

    fun getAllPokemonSummaryFromDb(): Flow<List<PokemonSummary>> = pokemonDao.getAllPokemonSummary()

    suspend fun getPokemonById(id: String): Pokemon? = pokemonDao.getPokemonById(id)

    fun getPokemonByIdFlow(id: String): Flow<Pokemon?> = pokemonDao.getPokemonByIdFlow(id)

    fun getPokemonPaging(
        query: String, 
        type: String, 
        sortType: String, 
        sortOrder: String
    ): Flow<PagingData<PokemonSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                initialLoadSize = 40,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { 
                pokemonDao.searchPagingPokemonSummary(query, type, sortType, sortOrder)
            }
        ).flow
    }

    suspend fun fetchAndCachePokemon(limit: Int = 20, offset: Int = 0) = withContext(Dispatchers.IO) {
        val response = apiService.getPokemonList(limit, offset)
        val filteredResults = response.results.filter { 
            val id = extractIdFromUrl(it.url)
            id <= 1025 || id >= 10000 
        }

        if (filteredResults.isEmpty()) return@withContext

        filteredResults.chunked(10).forEach { batch ->
            batch.map { entry ->
                async {
                    val id = extractIdFromUrl(entry.url)
                    if (id > 1025 && id < 10000) return@async
                    
                    val baseIdStr = id.toString()
                    if (id <= 1025) {
                        val existing = pokemonDao.getVarietiesCount(baseIdStr)
                        if (existing == 0) {
                            fetchFastSpeciesAndVarieties(id, baseIdStr)
                        }
                    }
                }
            }.awaitAll()
        }
    }

    private suspend fun fetchFastSpeciesAndVarieties(baseIdInt: Int, baseIdStr: String) {
        try {
            val speciesResponse = apiService.getSpecies("https://pokeapi.co/api/v2/pokemon-species/$baseIdInt/")
            val allPokemonToInsert = mutableListOf<Pokemon>()

            speciesResponse.varieties.forEachIndexed { index, variety ->
                val varietyId = extractIdFromUrl(variety.pokemon.url)
                val isImportantForm = variety.isDefault || varietyId >= 10000 || 
                                     variety.pokemon.name.contains("-mega") || 
                                     variety.pokemon.name.contains("-alola") || 
                                     variety.pokemon.name.contains("-galar") || 
                                     variety.pokemon.name.contains("-hisui")
                
                if (isImportantForm) {
                    val suffix = if (variety.isDefault) "" else ".${index}"
                    val dbId = "$baseIdStr$suffix"
                    val sortOrder = baseIdInt.toDouble() + (if (variety.isDefault) 0.0 else 0.0001 * index)
                    
                    val pokemon = fetchFastPokemonDetails(variety.pokemon.name, dbId, sortOrder)
                    allPokemonToInsert.add(pokemon)
                }
            }
            if (allPokemonToInsert.isNotEmpty()) {
                pokemonDao.insertAll(allPokemonToInsert)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun fetchFastPokemonDetails(pokemonName: String, dbId: String, sortOrder: Double): Pokemon {
        val details = apiService.getPokemonDetails(pokemonName)
        val displayId = dbId.split(".")[0]
        val customInfo = PokemonCustomData.assignments[dbId] ?: PokemonCustomData.assignments[displayId]

        return Pokemon(
            id = dbId,
            name = pokemonName.replace("-", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            types = details.types.map { it.type.name.uppercase() },
            hp = details.stats.find { it.stat.name == "hp" }?.base_stat ?: 0,
            attack = details.stats.find { it.stat.name == "attack" }?.base_stat ?: 0,
            defense = details.stats.find { it.stat.name == "defense" }?.base_stat ?: 0,
            spAtk = details.stats.find { it.stat.name == "special-attack" }?.base_stat ?: 0,
            spDef = details.stats.find { it.stat.name == "special-defense" }?.base_stat ?: 0,
            speed = details.stats.find { it.stat.name == "speed" }?.base_stat ?: 0,
            height = details.height,
            weight = details.weight,
            imageUrl = details.sprites.other.officialArtwork.front_default,
            description = "", // Tải sau
            abilities = emptyList(), // Tải sau
            evolutionChain = emptyList(), // Tải sau
            moves = emptyList(), // Tải sau
            pveTier = customInfo?.pve?.tier,
            pveRole = customInfo?.pve?.role,
            pvp1v1Tier = customInfo?.pvp1v1?.tier,
            pvp1v1Role = customInfo?.pvp1v1?.role,
            pvp2v2Tier = customInfo?.pvp2v2?.tier,
            pvp2v2Role = customInfo?.pvp2v2?.role,
            tags = customInfo?.tags ?: emptyList(),
            sortOrder = sortOrder,
            hasFullDetails = false
        )
    }

    // 2. Tải chi tiết cho một Pokemon (Khi click vào Detail)
    suspend fun ensureFullDetails(pokemonId: String) {
        val current = pokemonDao.getPokemonById(pokemonId)
        if (current == null || current.hasFullDetails) return
        fetchAndSaveFullDetails(current)
    }

    suspend fun startBackgroundSync(limit: Int = 5) = withContext(Dispatchers.IO) {
        val missing = pokemonDao.getPokemonMissingDetails(limit)
        missing.forEach { pokemon ->
            try {
                fetchAndSaveFullDetails(pokemon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchAndSaveFullDetails(current: Pokemon) = withContext(Dispatchers.IO) {
        try {
            val baseIdInt = current.id.split(".")[0].toInt()
            val details = apiService.getPokemonDetails(current.name.lowercase().replace(" ", "-"))
            val speciesResponse = apiService.getSpecies("https://pokeapi.co/api/v2/pokemon-species/$baseIdInt/")
            
            // 1. Evolution Chain
            val evolutionResponse = apiService.getEvolutionChain(speciesResponse.evolutionChain.url)
            val evolutionChain = mutableListOf<EvolutionStep>()
            parseEvolutionChain(evolutionResponse.chain, evolutionChain, null)

            // 2. Moves
            val movesToUse = if (details.moves.isEmpty() && current.name.lowercase() != speciesResponse.name) {
                try {
                    apiService.getPokemonDetails(speciesResponse.name).moves
                } catch (e: Exception) {
                    details.moves
                }
            } else details.moves

            // Sử dụng chunking để tránh quá tải tài nguyên và cải thiện độ ổn định
            val moveList = mutableListOf<MoveInfo>()
            movesToUse.chunked(15).forEach { chunk ->
                val results = chunk.map { moveEntry ->
                    async { fetchSingleMove(moveEntry) }
                }.awaitAll()
                moveList.addAll(results)
            }

            // 3. Abilities
            val abilityList = details.abilities.map { abilityEntry ->
                async { fetchSingleAbility(abilityEntry.ability.name) }
            }.awaitAll()

            // 4. Description
            val description = speciesResponse.flavorTextEntries
                .find { it.language.name == "en" }
                ?.flavorText?.replace("\n", " ")?.replace("\u000c", " ")?.replace("\r", " ")?.trim() ?: ""
            val translatedDescription = TranslatorManager.translate(description)

            // Cập nhật DB
            val updatedPokemon = current.copy(
                description = translatedDescription,
                abilities = abilityList,
                evolutionChain = evolutionChain,
                moves = moveList,
                hasFullDetails = true
            )
            pokemonDao.update(updatedPokemon)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun fetchSingleMove(moveEntry: com.example.pokedex.data.remote.MoveEntry): MoveInfo {
        return try {
            val moveUrl = moveEntry.move.url
            val moveDetails = moveDetailCache.getOrPut(moveUrl) { apiService.getMoveDetails(moveUrl) }
            val effectChance = moveDetails.effectChance?.toString() ?: ""
            val flavor = moveDetails.flavorTextEntries.find { it.language.name == "en" }?.flavorText ?: ""
            val rawEffect = moveDetails.effectEntries.find { it.language.name == "en" }?.effect ?: ""
            
            val moveDescription = TranslatorManager.translate("$flavor $rawEffect".replace("\$effect_chance", effectChance).trim())

            val versionDetails = moveEntry.versionGroupDetails
            val prioritizedVersions = listOf("scarlet-violet", "sword-shield", "brilliant-diamond-shining-pearl")
            
            fun findBestDetail(methodName: String): com.example.pokedex.data.remote.VersionGroupDetail? {
                for (v in prioritizedVersions) {
                    val d = versionDetails.find { it.moveLearnMethod.name == methodName && it.versionGroup.name == v }
                    if (d != null) return d
                }
                return versionDetails.find { it.moveLearnMethod.name == methodName }
            }

            val levelUpDetail = findBestDetail("level-up")
            val machineDetail = findBestDetail("machine")
            val eggDetail = findBestDetail("egg")
            val tutorDetail = findBestDetail("tutor")
            
            var learnDetail = ""
            val method = when {
                levelUpDetail != null -> "level-up"
                machineDetail != null -> {
                    val bestMachineVer = prioritizedVersions.firstNotNullOfOrNull { v -> moveDetails.machines.find { it.versionGroup.name == v } } ?: moveDetails.machines.firstOrNull()
                    if (bestMachineVer != null) {
                        try {
                            val machineInfo = apiService.getMachineDetails(bestMachineVer.machine.url)
                            learnDetail = machineInfo.item.name.uppercase()
                        } catch (e: Exception) { learnDetail = "TM" }
                    } else learnDetail = "TM"
                    "machine"
                }
                eggDetail != null -> { learnDetail = "Egg"; "egg" }
                tutorDetail != null -> { learnDetail = "Tutor"; "tutor" }
                else -> "other"
            }

            MoveInfo(
                name = moveEntry.move.name.replace("-", " ").replaceFirstChar { it.uppercase() },
                type = moveDetails.type.name.uppercase(),
                category = moveDetails.damage_class.name.replaceFirstChar { it.uppercase() },
                power = moveDetails.power,
                accuracy = moveDetails.accuracy,
                priority = moveDetails.priority,
                description = moveDescription,
                learnMethod = method,
                level = levelUpDetail?.levelLearnedAt ?: 0,
                learnDetail = learnDetail
            )
        } catch (e: Exception) {
            MoveInfo(moveEntry.move.name, "NORMAL", "Physical", null, null, 0, "", "other", 0)
        }
    }

    private suspend fun fetchSingleAbility(abilityName: String): com.example.pokedex.model.AbilityInfo {
        return try {
            val details = abilityDetailCache.getOrPut(abilityName) { apiService.getAbilityDetails(abilityName) }
            val effect = details.effectEntries.find { it.language.name == "en" }?.effect ?: ""
            com.example.pokedex.model.AbilityInfo(
                name = abilityName.replace("-", " ").replaceFirstChar { it.uppercase() },
                effect = TranslatorManager.translate(effect)
            )
        } catch (e: Exception) {
            com.example.pokedex.model.AbilityInfo(abilityName, "Error loading.")
        }
    }

    private fun parseEvolutionChain(chain: ChainLink, list: MutableList<EvolutionStep>, parentName: String? = null) {
        val id = extractIdFromUrl(chain.species.url)
        val condition = chain.evolutionDetails.firstOrNull()?.let { details ->
            when (details.trigger.name) {
                "level-up" -> "Level ${details.minLevel ?: ""}"
                "use-item" -> "Use ${details.item?.name ?: "Item"}"
                else -> details.trigger.name
            }
        } ?: ""
        val currentName = chain.species.name.replaceFirstChar { it.uppercase() }
        list.add(EvolutionStep(id.toString(), currentName, "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png", condition, parentName))
        chain.evolvesTo.forEach { parseEvolutionChain(it, list, currentName) }
    }

    private fun extractIdFromUrl(url: String): Int = url.split("/").filter { it.isNotEmpty() }.last().toInt()

    suspend fun cleanExtraPokemon() {
        pokemonDao.deletePokemonAboveId(1025.9)
    }

    suspend fun getItemCount(): Int = itemDao.getCount()

    // ITEM METHODS
    fun getAllItemsFromDb(): Flow<List<com.example.pokedex.model.Item>> = itemDao.getAllItems()

    suspend fun fetchAndCacheItems(limit: Int = 50, offset: Int = 0) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getItemList(limit, offset)
            val itemsToInsert = response.results.chunked(10).flatMap { batch ->
                batch.map { entry ->
                    async {
                        try {
                            val details = apiService.getItemDetails(entry.name)
                            val effect = details.effectEntries.find { it.language.name == "en" }?.effect ?: ""
                            com.example.pokedex.model.Item(
                                id = details.id,
                                name = details.name.replace("-", " ").replaceFirstChar { it.uppercase() },
                                category = details.category.name,
                                effect = TranslatorManager.translate(effect),
                                imageUrl = details.sprites.default ?: ""
                            )
                        } catch (e: Exception) { null }
                    }
                }.awaitAll().filterNotNull()
            }
            if (itemsToInsert.isNotEmpty()) {
                itemDao.insertAll(itemsToInsert)
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}
