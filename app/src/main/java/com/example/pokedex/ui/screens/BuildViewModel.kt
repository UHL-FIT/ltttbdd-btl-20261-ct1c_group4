package com.example.pokedex.ui.screens

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.TierMode
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.model.Item
import com.example.pokedex.model.MoveInfo
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonSummary
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BuildSlot(
    val pokemon: Pokemon? = null,
    val selectedMoves: List<MoveInfo?> = listOf(null, null, null, null),
    val selectedItem: Item? = null,
    val selectedAbility: String? = null
)

class BuildViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PokemonRepository(application)

    private val _team = MutableStateFlow(List(6) { BuildSlot() })
    val team: StateFlow<List<BuildSlot>> = _team.asStateFlow()

    private val _activeSlotIndex = MutableStateFlow(0)
    val activeSlotIndex: StateFlow<Int> = _activeSlotIndex.asStateFlow()

    private val _allPokemon = MutableStateFlow<List<PokemonSummary>>(emptyList())
    val allPokemon: StateFlow<List<PokemonSummary>> = _allPokemon.asStateFlow()

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems.asStateFlow()

    private val _typeFilter = MutableStateFlow("All")
    val typeFilter = _typeFilter.asStateFlow()

    private val _regionFilter = MutableStateFlow("All")
    val regionFilter = _regionFilter.asStateFlow()

    val filteredPokemon = combine(
        _allPokemon, 
        _typeFilter, 
        _regionFilter, 
        _team, 
        _activeSlotIndex
    ) { all, type, region, team, activeIndex ->
        // 1. Thu thập tất cả các Base ID không thể chọn (đã có trong các slot khác và dòng tiến hóa của chúng)
        // Và kiểm tra xem team đã có Pokemon Mega chưa
        val blockedBaseIds = mutableSetOf<String>()
        var hasMegaInTeam = false

        team.forEachIndexed { index, slot ->
            if (index != activeIndex) {
                slot.pokemon?.let { p ->
                    // Thêm base ID của pokemon hiện tại
                    blockedBaseIds.add(p.id.split(".")[0])
                    // Thêm tất cả ID trong chuỗi tiến hóa (Dùng repository để lấy chi tiết nếu cần, hoặc giả định từ ID)
                    // Ở bản Summary, chúng ta không có evolutionChain. 
                    // Tạm thời chỉ block chính ID đó và base ID.
                    // p.evolutionChain.forEach { step -> blockedBaseIds.add(step.id) }
                    
                    // Kiểm tra Mega
                    if (p.name.contains("Mega", ignoreCase = true)) {
                        hasMegaInTeam = true
                    }
                }
            }
        }

        // 2. Lọc danh sách
        all.filter { p ->
            val matchesType = type == "All" || p.types.any { it.equals(type, ignoreCase = true) }
            val matchesRegion = region == "All" || matchesRegion(p.id, region)
            
            val pBaseId = p.id.split(".")[0]
            val isAvailable = !blockedBaseIds.contains(pBaseId)
            
            // Nếu team đã có Mega, không cho chọn Mega khác
            val isMegaAllowed = !hasMegaInTeam || !p.name.contains("Mega", ignoreCase = true)
            
            matchesType && matchesRegion && isAvailable && isMegaAllowed
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFormat = MutableStateFlow(TierMode.PVE)
    val selectedFormat: StateFlow<TierMode> = _selectedFormat.asStateFlow()

    private val _savedTeams = MutableStateFlow<Map<String, List<BuildSlot>>>(emptyMap())
    val savedTeams: StateFlow<Map<String, List<BuildSlot>>> = _savedTeams.asStateFlow()

    private val _currentEditingTeamName = MutableStateFlow<String?>(null)
    val currentEditingTeamName: StateFlow<String?> = _currentEditingTeamName.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllPokemonSummaryFromDb().collectLatest { list -> 
                _allPokemon.value = list 
            }
        }
        
        // Luôn đảm bảo có Item
        refreshItems()

        viewModelScope.launch {
            repository.getAllItemsFromDb().collectLatest { list ->
                if (list.isEmpty()) {
                    refreshItems()
                    return@collectLatest
                }
                // Lọc thoáng hơn để đảm bảo hiển thị
                _allItems.value = list.filter { item ->
                    val cat = item.category.lowercase()
                    // Bao gồm hầu hết các category liên quan đến chiến đấu
                    cat.contains("held") || cat.contains("berry") || cat.contains("choice") ||
                    cat.contains("scarf") || cat.contains("band") || cat.contains("specs") ||
                    cat.contains("plate") || cat.contains("orb") || cat.contains("mega") ||
                    cat.contains("stone") || cat.contains("item") || cat.isEmpty()
                }.sortedBy { it.name }
            }
        }
        loadSavedTeams()
    }

    fun refreshItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val count = repository.getItemCount()
                if (count < 50) {
                    repository.fetchAndCacheItems(limit = 100, offset = 0)
                    repository.fetchAndCacheItems(limit = 100, offset = 100)
                    repository.fetchAndCacheItems(limit = 100, offset = 200)
                    repository.fetchAndCacheItems(limit = 100, offset = 300)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadSavedTeams() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("pokedex_teams", Context.MODE_PRIVATE)
        val gson = Gson()
        val existingTeamsJson = sharedPrefs.getString("saved_teams", "{}")
        
        try {
            if (existingTeamsJson == null || existingTeamsJson == "{}") {
                _savedTeams.value = emptyMap()
                return
            }

            val type = object : TypeToken<Map<String, List<BuildSlot>>>() {}.type
            val teams: Map<String, List<BuildSlot>>? = gson.fromJson(existingTeamsJson, type)
            _savedTeams.value = teams ?: emptyMap()
        } catch (e: Exception) {
            // Không xóa dữ liệu nếu chỉ là lỗi parse tạm thời
            e.printStackTrace()
            _savedTeams.value = emptyMap()
        }
    }

    fun setActiveSlot(index: Int) {
        _activeSlotIndex.value = index
    }

    fun updatePokemonInActiveSlot(pokemonSummary: PokemonSummary?) {
        val currentTeam = _team.value.toMutableList()
        val activeIndex = _activeSlotIndex.value
        
        viewModelScope.launch {
            // Ensure full details are fetched when selecting a pokemon
            pokemonSummary?.let { repository.ensureFullDetails(it.id) }

            val pokemon: Pokemon? = pokemonSummary?.let { repository.getPokemonById(it.id) }
            
            if (pokemon != null) {
                val newBaseId = pokemon.id.split(".")[0]
                
                // Kiểm tra xem có Pokemon nào cùng Base ID hoặc trong cùng Evolution Line không
                val hasConflict = currentTeam.indices.any { i ->
                    if (i == activeIndex) return@any false
                    val existingPokemon = currentTeam[i].pokemon ?: return@any false
                    
                    val existingBaseId = existingPokemon.id.split(".")[0]
                    
                    // Trùng Base ID (ví dụ Charizard và Mega Charizard)
                    if (newBaseId == existingBaseId) return@any true
                    
                    // Trùng trong chuỗi tiến hóa
                    val newEvolutionIds = pokemon.evolutionChain.map { it.id }.toSet()
                    val existingEvolutionIds = existingPokemon.evolutionChain.map { it.id }.toSet()
                    
                    newEvolutionIds.contains(existingBaseId) || existingEvolutionIds.contains(newBaseId)
                }

                if (hasConflict) {
                    Toast.makeText(getApplication(), "Không thể chọn nhiều Pokemon cùng dòng tiến hóa hoặc cùng loài!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
            }

            val currentSlot = currentTeam[activeIndex]
            val autoItem = pokemon?.let { getAutoItemForPokemon(it) }
            val requiredAbility = pokemon?.let { getRequiredAbility(it) }
            val requiredMove = pokemon?.let { getRequiredMove(it) }

            val newMoves = currentSlot.selectedMoves.toMutableList()
            if (requiredMove != null) {
                // Nếu có chiêu thức bắt buộc (như Dragon Ascent cho Rayquaza), gán vào slot 1
                newMoves[0] = requiredMove
            } else {
                // Reset moves if pokemon changed
                for (i in 0..3) newMoves[i] = null
            }

            currentTeam[activeIndex] = currentSlot.copy(
                pokemon = pokemon,
                selectedMoves = newMoves,
                selectedItem = autoItem,
                selectedAbility = requiredAbility ?: pokemon?.abilities?.firstOrNull()?.name
            )
            _team.value = currentTeam
        }
    }

    private fun getRequiredAbility(pokemon: Pokemon): String? {
        val name = pokemon.name
        return when {
            name.contains("Aegislash", true) -> "Stance Change"
            name.contains("Zygarde", true) && name.contains("Complete", true) -> "Power Construct"
            name.contains("Palafin", true) && name.contains("Hero", true) -> "Zero to Hero"
            name.contains("Wishiwashi", true) && name.contains("School", true) -> "Schooling"
            name.contains("Darmanitan", true) && name.contains("Zen", true) -> "Zen Mode"
            name.contains("Greninja", true) && name.contains("Ash", true) -> "Battle Bond"
            else -> null
        }
    }

    private fun getRequiredMove(pokemon: Pokemon): MoveInfo? {
        if (pokemon.name.contains("Rayquaza", true) && pokemon.name.contains("Mega", true)) {
            return pokemon.moves.find { it.name.equals("Dragon Ascent", true) }
        }
        return null
    }

    private fun getAutoItemForPokemon(pokemon: Pokemon): Item? {
        val name = pokemon.name
        val items = _allItems.value
        return when {
            name.contains("Mega", ignoreCase = true) -> {
                if (name.contains("Rayquaza", true)) return null
                
                val baseName = name.substringBefore(" Mega").trim()
                val suffix = if (name.contains(" X")) " X" else if (name.contains(" Y")) " Y" else ""
                
                val searchName = when (baseName) {
                    "Alakazam" -> "Alakazite"
                    "Aggron" -> "Aggronite"
                    "Aerodactyl" -> "Aerodactylite"
                    "Ampharos" -> "Ampharosite"
                    "Banette" -> "Banettite"
                    "Beedrill" -> "Beedrillite"
                    "Blastoise" -> "Blastoisinite"
                    "Blaziken" -> "Blazikenite"
                    "Camerupt" -> "Cameruptite"
                    "Garchomp" -> "Garchompite"
                    "Gardevoir" -> "Gardevoirite"
                    "Gengar" -> "Gengarite"
                    "Glalie" -> "Glalitite"
                    "Gyarados" -> "Gyaradosite"
                    "Heracross" -> "Heracronite"
                    "Houndoom" -> "Houndoominite"
                    "Kangaskhan" -> "Kangaskhanite"
                    "Latias" -> "Latiasite"
                    "Latios" -> "Latiosite"
                    "Lucario" -> "Lucarionite"
                    "Manectric" -> "Manectricite"
                    "Mawile" -> "Mawilite"
                    "Medicham" -> "Medichamite"
                    "Metagross" -> "Metagrossite"
                    "Pinsir" -> "Pinsirite"
                    "Sableye" -> "Sablenite"
                    "Salamence" -> "Salamencite"
                    "Sceptile" -> "Sceptilite"
                    "Scizor" -> "Scizorite"
                    "Sharpedo" -> "Sharpedonite"
                    "Slowbro" -> "Slowbronite"
                    "Steelix" -> "Steelixite"
                    "Swampert" -> "Swampertite"
                    "Tyranitar" -> "Tyranitarite"
                    "Venusaur" -> "Venusaurite"
                    "Gallade" -> "Galladite"
                    "Audino" -> "Audinite"
                    "Diancie" -> "Diancite"
                    "Lopunny" -> "Lopunnite"
                    "Altaria" -> "Altarianite"
                    else -> "${baseName}ite"
                }
                val finalSearch = if (suffix.isNotEmpty()) "$searchName$suffix" else searchName
                items.find { it.name.replace(" ", "").equals(finalSearch.replace(" ", ""), ignoreCase = true) }
            }
            name.contains("Primal", ignoreCase = true) -> {
                if (name.contains("Kyogre")) items.find { it.name.contains("Blue Orb", true) }
                else if (name.contains("Groudon")) items.find { it.name.contains("Red Orb", true) }
                else null
            }
            name.contains("Crowned", ignoreCase = true) -> {
                if (name.contains("Zacian")) items.find { it.name.contains("Rusted Sword", true) }
                else if (name.contains("Zamazenta")) items.find { it.name.contains("Rusted Shield", true) }
                else null
            }
            name.contains("Origin", ignoreCase = true) -> {
                when {
                    name.contains("Giratina") -> items.find { it.name.contains("Griseous Orb", true) || it.name.contains("Griseous Core", true) }
                    name.contains("Dialga") -> items.find { it.name.contains("Adamant Crystal", true) }
                    name.contains("Palkia") -> items.find { it.name.contains("Lustrous Globe", true) }
                    else -> null
                }
            }
            name.contains("Wellspring", true) -> items.find { it.name.contains("Wellspring Mask", true) }
            name.contains("Hearthflame", true) -> items.find { it.name.contains("Hearthflame Mask", true) }
            name.contains("Cornerstone", true) -> items.find { it.name.contains("Cornerstone Mask", true) }
            else -> null
        }
    }


    private fun matchesRegion(id: String, region: String): Boolean {
        val numId = id.split(".")[0].toIntOrNull() ?: return false
        return when (region) {
            "Gen 1" -> numId in 1..151
            "Gen 2" -> numId in 152..251
            "Gen 3" -> numId in 252..386
            "Gen 4" -> numId in 387..493
            "Gen 5" -> numId in 494..649
            "Gen 6" -> numId in 650..721
            "Gen 7" -> numId in 722..809
            "Gen 8" -> numId in 810..905
            "Gen 9" -> numId in 906..1025
            else -> true
        }
    }

    fun setTypeFilter(type: String) {
        _typeFilter.value = type
    }

    fun setRegionFilter(region: String) {
        _regionFilter.value = region
    }

    fun updateMoveInActiveSlot(moveIndex: Int, move: MoveInfo?) {
        val currentTeam = _team.value.toMutableList()
        val currentSlot = currentTeam[_activeSlotIndex.value]
        val pokemon = currentSlot.pokemon ?: return

        // Ràng buộc chiêu thức bắt buộc (ví dụ Mega Rayquaza cần Dragon Ascent)
        val requiredMove = getRequiredMove(pokemon)
        if (requiredMove != null && moveIndex == 0) {
            Toast.makeText(getApplication(), "Chiêu thức này là bắt buộc cho hình dạng này!", Toast.LENGTH_SHORT).show()
            return
        }

        // Prevent duplicate move selection
        if (move != null && currentSlot.selectedMoves.any { it?.name == move.name }) {
            Toast.makeText(getApplication(), "Chiêu thức này đã được chọn!", Toast.LENGTH_SHORT).show()
            return
        }

        val moves = currentSlot.selectedMoves.toMutableList()
        moves[moveIndex] = move
        currentTeam[_activeSlotIndex.value] = currentSlot.copy(selectedMoves = moves)
        _team.value = currentTeam
    }

    fun updateItemInActiveSlot(item: Item?) {
        val currentTeam = _team.value.toMutableList()
        val currentSlot = currentTeam[_activeSlotIndex.value]
        val pokemon = currentSlot.pokemon

        // Kiểm tra nếu Pokemon hiện tại yêu cầu vật phẩm cố định
        if (pokemon != null) {
            val autoItem = getAutoItemForPokemon(pokemon)
            if (autoItem != null && item?.name != autoItem.name) {
                Toast.makeText(getApplication(), "Vật phẩm này là bắt buộc cho hình dạng này!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        currentTeam[_activeSlotIndex.value] = currentSlot.copy(selectedItem = item)
        _team.value = currentTeam
    }

    fun updateAbilityInActiveSlot(ability: String?) {
        val currentTeam = _team.value.toMutableList()
        val currentSlot = currentTeam[_activeSlotIndex.value]
        val pokemon = currentSlot.pokemon

        // Kiểm tra nếu Pokemon hiện tại yêu cầu kỹ năng cố định
        if (pokemon != null) {
            val requiredAbility = getRequiredAbility(pokemon)
            if (requiredAbility != null && ability != requiredAbility) {
                Toast.makeText(getApplication(), "Kỹ năng này là bắt buộc cho hình dạng này!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        currentTeam[_activeSlotIndex.value] = currentSlot.copy(selectedAbility = ability)
        _team.value = currentTeam
    }

    fun setFormat(format: TierMode) {
        _selectedFormat.value = format
    }

    fun saveTeam(context: Context, teamName: String, overwrite: Boolean = false) {
        viewModelScope.launch {
            val sharedPrefs = context.getSharedPreferences("pokedex_teams", Context.MODE_PRIVATE)
            val gson = Gson()
            
            // Get existing teams
            val existingTeamsJson = sharedPrefs.getString("saved_teams", "{}")
            val type = object : TypeToken<MutableMap<String, List<BuildSlot>>>() {}.type
            
            val teams: MutableMap<String, List<BuildSlot>> = try {
                gson.fromJson(existingTeamsJson, type) ?: mutableMapOf()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            var finalName = teamName
            if (!overwrite && teams.containsKey(teamName)) {
                // If it exists and we're not just overwriting (e.g. "Save as copy" logic)
                // For simplicity, let's implement automatic renaming if it exists
                var count = 1
                while (teams.containsKey("$teamName ($count)")) {
                    count++
                }
                finalName = "$teamName ($count)"
            }

            // Add current team
            teams[finalName] = _team.value
            
            // Save back
            sharedPrefs.edit().putString("saved_teams", gson.toJson(teams)).apply()
            _currentEditingTeamName.value = finalName
            loadSavedTeams()
            Toast.makeText(context, "Đã lưu đội hình: $finalName", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTeam(context: Context, teamName: String) {
        viewModelScope.launch {
            val sharedPrefs = context.getSharedPreferences("pokedex_teams", Context.MODE_PRIVATE)
            val gson = Gson()
            val existingTeamsJson = sharedPrefs.getString("saved_teams", "{}")
            val type = object : TypeToken<MutableMap<String, List<BuildSlot>>>() {}.type
            
            val teams: MutableMap<String, List<BuildSlot>> = try {
                gson.fromJson(existingTeamsJson, type) ?: mutableMapOf()
            } catch (e: Exception) {
                mutableMapOf()
            }

            teams.remove(teamName)

            if (_currentEditingTeamName.value == teamName) {
                _currentEditingTeamName.value = null
            }

            sharedPrefs.edit().putString("saved_teams", gson.toJson(teams)).apply()
            loadSavedTeams()
            Toast.makeText(context, "Đã xóa đội hình: $teamName", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadTeam(context: Context, teamName: String) {
        val teams = _savedTeams.value
        
        teams[teamName]?.let {
            _team.value = it
            _currentEditingTeamName.value = teamName
            Toast.makeText(context, "Đã tải đội hình: $teamName", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Không tìm thấy đội hình!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getSavedTeamNames(context: Context): List<String> {
        return _savedTeams.value.keys.toList()
    }

    fun clearAllTeams(context: Context) {
        val sharedPrefs = context.getSharedPreferences("pokedex_teams", Context.MODE_PRIVATE)
        sharedPrefs.edit().remove("saved_teams").apply()
        _currentEditingTeamName.value = null
        loadSavedTeams()
        Toast.makeText(context, "Đã xóa tất cả đội hình", Toast.LENGTH_SHORT).show()
    }

    fun createNewTeam() {
        _team.value = List(6) { BuildSlot() }
        _currentEditingTeamName.value = null
    }
}
