package com.example.pokedex.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.data.TierMode
import com.example.pokedex.ui.components.PrivacyFooter

@Composable
fun BuildScreen(
    navController: NavController,
    buildViewModel: BuildViewModel = viewModel()
) {
    val team by buildViewModel.team.collectAsState()
    val activeSlotIndex by buildViewModel.activeSlotIndex.collectAsState()
    val allPokemon by buildViewModel.filteredPokemon.collectAsState()
    val allItems by buildViewModel.allItems.collectAsState()
    val selectedFormat by buildViewModel.selectedFormat.collectAsState()
    val typeFilter by buildViewModel.typeFilter.collectAsState()
    val regionFilter by buildViewModel.regionFilter.collectAsState()

    var showPokemonPicker by remember { mutableStateOf(false) }
    var showItemPicker by remember { mutableStateOf(false) }
    var showMovePickerIndex by remember { mutableStateOf<Int?>(null) }
    var showAbilityPicker by remember { mutableStateOf(false) }
    var showTeamListDialog by remember { mutableStateOf(false) }

    val currentSlot = team[activeSlotIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E26))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header with Breadcrumbs
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Home",
                    color = Color(0xFF00B0FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("home_route") }
                )
                Text(
                    text = " > Team Build",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                val savedTeams by buildViewModel.savedTeams.collectAsState()
                if (savedTeams.isNotEmpty()) {
                    Button(
                        onClick = { showTeamListDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF).copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, Color(0xFF00B0FF)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("MY TEAMS", color = Color(0xFF00B0FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TEAM BUILD",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { buildViewModel.createNewTeam() }) {
                    Icon(Icons.Default.Refresh, "Reset", tint = Color.Gray)
                }
            }
            val currentEditingName by buildViewModel.currentEditingTeamName.collectAsState()
            if (currentEditingName != null) {
                Text(
                    text = "Đang chỉnh sửa: $currentEditingName",
                    color = Color(0xFF00B0FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Team Slots
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            team.forEachIndexed { index, slot ->
                val isActive = activeSlotIndex == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { buildViewModel.setActiveSlot(index) }
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(if (isActive) Color(0xFF32323E) else Color.Transparent, CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isActive) Color(0xFF00B0FF) else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (slot.pokemon != null) {
                            AsyncImage(model = slot.pokemon.imageUrl, contentDescription = null, modifier = Modifier.size(40.dp))
                        } else {
                            Text("?", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = (index + 1).toString(), color = if (isActive) Color.White else Color.Gray, fontSize = 12.sp)
                    if (isActive) {
                        HorizontalDivider(color = Color(0xFF00B0FF), thickness = 2.dp, modifier = Modifier.width(30.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Selection Card
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A32)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // Left Side: Pokemon Image & Name
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = currentSlot.pokemon?.name ?: "Name",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { showPokemonPicker = true },
                        enabled = false,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.White,
                            disabledBorderColor = Color.Gray,
                            disabledContainerColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.size(100.dp).background(Color(0xFF32323E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentSlot.pokemon != null) {
                            AsyncImage(model = currentSlot.pokemon.imageUrl, contentDescription = null, modifier = Modifier.size(80.dp))
                        } else {
                            Text("?", color = Color.Gray, fontSize = 40.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right Side: Moves, Item, Ability
                Column(modifier = Modifier.weight(1f)) {
                    repeat(4) { idx ->
                        SelectionField(
                            label = currentSlot.selectedMoves[idx]?.name ?: "Move",
                            onClick = { showMovePickerIndex = idx }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    SelectionField(
                        label = currentSlot.selectedItem?.name ?: "Item",
                        onClick = { showItemPicker = true }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionField(
                        label = currentSlot.selectedAbility ?: "Ability",
                        onClick = { showAbilityPicker = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Section
        val defenceStats = remember(team) { calculateDefenceStats(team) }
        val coverageStats = remember(team) { calculateCoverageStats(team) }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Team Defence", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            TypeGrid(stats = defenceStats)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Team Type Coverage", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            TypeGrid(stats = coverageStats)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs Section
        BuildTabs(
            selectedFormat = selectedFormat,
            onFormatChange = { buildViewModel.setFormat(it) },
            typeFilter = typeFilter,
            onTypeChange = { buildViewModel.setTypeFilter(it) },
            regionFilter = regionFilter,
            onRegionChange = { buildViewModel.setRegionFilter(it) },
            team = team,
            buildViewModel = buildViewModel
        )

        Spacer(modifier = Modifier.height(32.dp))
        PrivacyFooter()
        Spacer(modifier = Modifier.height(32.dp))
    }

    // Lazy Selection Pickers
    if (showPokemonPicker) {
        LazyPicker(
            title = "Select Pokemon",
            items = allPokemon,
            onDismiss = { showPokemonPicker = false },
            onSelect = { buildViewModel.updatePokemonInActiveSlot(it); showPokemonPicker = false },
            itemContent = { p ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    AsyncImage(model = p.imageUrl, contentDescription = null, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(p.baseName, color = Color(0xFF00B0FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (p.variantName.isNotEmpty()) {
                            Text(p.variantName, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            filterPredicate = { p, query -> p.name.contains(query, true) || p.id.contains(query) }
        )
    }

    if (showItemPicker) {
        LazyPicker(
            title = "Select Item",
            items = allItems,
            onDismiss = { showItemPicker = false },
            onSelect = { buildViewModel.updateItemInActiveSlot(it); showItemPicker = false },
            itemContent = { item ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.name, color = Color.White, fontSize = 16.sp)
                }
            },
            filterPredicate = { item, query -> item.name.contains(query, true) }
        )
    }

    if (showAbilityPicker) {
        LazyPicker(
            title = "Select Ability",
            items = currentSlot.pokemon?.abilities ?: emptyList(),
            onDismiss = { showAbilityPicker = false },
            onSelect = { buildViewModel.updateAbilityInActiveSlot(it.name); showAbilityPicker = false },
            itemContent = { ability ->
                Text(ability.name, color = Color.White, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
            },
            filterPredicate = { ability, query -> ability.name.contains(query, true) }
        )
    }

    if (showMovePickerIndex != null) {
        MovePicker(
            pokemon = currentSlot.pokemon,
            selectedMoves = currentSlot.selectedMoves,
            onDismiss = { showMovePickerIndex = null },
            onSelect = { move ->
                buildViewModel.updateMoveInActiveSlot(showMovePickerIndex!!, move)
                showMovePickerIndex = null
            }
        )
    }

    if (showTeamListDialog) {
        TeamSelectionDialog(
            viewModel = buildViewModel,
            onDismiss = { showTeamListDialog = false }
        )
    }
}

@Composable
fun TeamSelectionDialog(
    viewModel: BuildViewModel,
    onDismiss: () -> Unit
) {
    val savedTeams by viewModel.savedTeams.collectAsState()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E26)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF32323E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("My Teams", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Color.Gray) }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (savedTeams.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No saved teams found.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(savedTeams.toList()) { (name, slots) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.loadTeam(context, name); onDismiss() },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A32)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, color = Color(0xFF00B0FF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row {
                                            slots.forEach { slot ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(Color(0xFF1E1E26), CircleShape)
                                                        .border(0.5.dp, Color.Gray, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (slot.pokemon != null) {
                                                        AsyncImage(model = slot.pokemon.imageUrl, contentDescription = null, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(2.dp))
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.deleteTeam(context, name) }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovePicker(
    pokemon: com.example.pokedex.model.Pokemon?,
    selectedMoves: List<com.example.pokedex.model.MoveInfo?>,
    onDismiss: () -> Unit,
    onSelect: (com.example.pokedex.model.MoveInfo) -> Unit
) {
    if (pokemon == null) return
    var selectedTab by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    val tabs = listOf("LEVEL UP", "TMs", "OTHERS")

    val filteredMoves = remember(pokemon.moves, selectedTab, query) {
        pokemon.moves.filter { move ->
            val matchesQuery = move.name.contains(query, true)
            val matchesTab = when (selectedTab) {
                0 -> move.learnMethod == "level-up"
                1 -> move.learnMethod == "machine"
                else -> move.learnMethod != "level-up" && move.learnMethod != "machine"
            }
            matchesQuery && matchesTab
        }.let { list ->
            if (selectedTab == 0) list.sortedBy { it.level } else list.sortedBy { it.name }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E26)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF32323E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Select Move", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Color.Gray) }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00B0FF),
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search moves...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredMoves) { move ->
                    val isSelected = selectedMoves.any { it?.name == move.name }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color(0xFF00B0FF).copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onSelect(move) }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                    ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .background(getTypeColor(move.type), RoundedCornerShape(4.dp))
                                        .padding(vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(move.type.uppercase().take(3), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(move.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    if (move.learnMethod == "level-up" && move.level > 0) {
                                        Text("Learned at Level ${move.level}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(move.category, color = Color.LightGray, fontSize = 10.sp)
                                    Text(
                                        text = if (move.power != null) "Pow: ${move.power}" else "Pow: -",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFF32323E), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> LazyPicker(
    title: String,
    items: List<T>,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    filterPredicate: (T, String) -> Boolean
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, items) {
        if (query.isEmpty()) items else items.filter { filterPredicate(it, query) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E26)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF32323E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Color.Gray) }
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filtered) { item ->
                        Box(modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                            itemContent(item)
                        }
                        HorizontalDivider(color = Color(0xFF32323E), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionField(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
        }
    }
}

@Composable
fun TypeGrid(stats: Map<String, Int>) {
    val types = pokemonTypes.filter { it.name != "All" }
    Column {
        types.chunked(6).forEach { rowTypes ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                rowTypes.forEach { type ->
                    val value = stats[type.name.uppercase()] ?: 0
                    val displayValue = if (value > 0) "+$value" else if (value < 0) "$value" else "0"
                    val textColor = if (value > 0) Color(0xFF44FF44) else if (value < 0) Color(0xFFFF4444) else Color.White

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(2.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(type.color, RoundedCornerShape(4.dp))
                                .padding(vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(type.name.uppercase().take(3), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(displayValue, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BuildTabs(
    selectedFormat: TierMode,
    onFormatChange: (TierMode) -> Unit,
    typeFilter: String,
    onTypeChange: (String) -> Unit,
    regionFilter: String,
    onRegionChange: (String) -> Unit,
    team: List<BuildSlot>,
    buildViewModel: BuildViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("FILTERS", "CHECKLIST", "SAVE/LOAD")

    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF00B0FF),
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(modifier = Modifier.padding(16.dp)) {
            when (selectedTab) {
                0 -> FilterTabContent(selectedFormat, onFormatChange, typeFilter, onTypeChange, regionFilter, onRegionChange)
                1 -> ChecklistTabContent(selectedFormat, team)
                2 -> SaveLoadTabContent(buildViewModel)
            }
        }
    }
}

@Composable
fun SaveLoadTabContent(viewModel: BuildViewModel) {
    val context = LocalContext.current
    var teamNameInput by remember { mutableStateOf("") }
    val savedTeams by viewModel.savedTeams.collectAsState()
    val currentEditingName by viewModel.currentEditingTeamName.collectAsState()

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.createNewTeam(); teamNameInput = "" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("TẠO MỚI", fontSize = 12.sp)
            }
            
            Button(
                onClick = { viewModel.clearAllTeams(context) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("XÓA TẤT CẢ", fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (currentEditingName != null) {
            Text(
                "Đang chỉnh sửa: $currentEditingName",
                color = Color(0xFF00B0FF),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = teamNameInput,
            onValueChange = { teamNameInput = it },
            label = { Text("Tên đội hình", color = Color.Gray) },
            placeholder = { Text(currentEditingName ?: "Nhập tên mới...", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF00B0FF)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (currentEditingName != null) {
                Button(
                    onClick = { viewModel.saveTeam(context, currentEditingName!!, overwrite = true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("CẬP NHẬT")
                }
            }

            Button(
                onClick = { 
                    val nameToSave = if (teamNameInput.isNotBlank()) teamNameInput else currentEditingName
                    if (!nameToSave.isNullOrBlank()) {
                        viewModel.saveTeam(context, nameToSave, overwrite = false)
                        teamNameInput = ""
                    } else {
                        Toast.makeText(context, "Vui lòng nhập tên đội hình", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(if (currentEditingName != null) "LƯU BẢN SAO" else "LƯU ĐỘI HÌNH")
            }
        }

        if (savedTeams.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Đội hình đã lưu:", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            savedTeams.forEach { (name, slots) ->
                var expanded by remember { mutableStateOf(false) }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF32323E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = Color(0xFF00B0FF), fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        null,
                                        tint = Color.Gray
                                    )
                                }
                                IconButton(onClick = { viewModel.loadTeam(context, name) }) {
                                    Icon(Icons.Default.Refresh, "Tải", tint = Color.Green)
                                }
                                IconButton(onClick = { viewModel.deleteTeam(context, name) }) {
                                    Icon(Icons.Default.Delete, "Xóa", tint = Color.Red)
                                }
                            }
                        }
                        
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                slots.forEach { slot ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF1E1E26), CircleShape)
                                            .border(1.dp, Color.Gray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (slot.pokemon != null) {
                                            AsyncImage(
                                                model = slot.pokemon.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        } else {
                                            Text("?", color = Color.Gray, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabContent(
    format: TierMode, onFormatChange: (TierMode) -> Unit,
    type: String, onTypeChange: (String) -> Unit,
    region: String, onRegionChange: (String) -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth()) {
            DropdownFilter("Format", format.label, TierMode.values().map { it.label }, { label -> onFormatChange(TierMode.values().first { it.label == label }) }, Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            DropdownFilter("Type", type, pokemonTypes.map { it.name }, onTypeChange, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            DropdownFilter("Region", region, listOf("All", "Gen 1", "Gen 2", "Gen 3", "Gen 4", "Gen 5", "Gen 6", "Gen 7", "Gen 8", "Gen 9"), onRegionChange, Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun DropdownFilter(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Box {
            Row(Modifier.clickable { expanded = true }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(selected, color = Color.White)
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF2A2A32))) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt, color = Color.White) }, onClick = { onSelect(opt); expanded = false })
                }
            }
        }
        HorizontalDivider(color = Color.Gray)
    }
}

@Composable
fun ChecklistTabContent(format: TierMode, team: List<BuildSlot>) {
    val items = when (format) {
        TierMode.PVE -> listOf("Slave", "Catcher", "Item Farmer", "Physical Sweeper", "Special Sweeper", "Setup Sweeper", "Wall", "Staller")
        TierMode.PVP_1V1 -> listOf("Hazard Setter", "Hazard Cleaner", "Wallbreaker", "Setup Sweeper", "Revenge Killer", "Pivot", "Wall", "Cleric")
        TierMode.PVP_2V2 -> listOf("Tailwind Setter", "Trick Room Setter", "Speed Debuffer", "Redirector", "Fake Out User", "Spread Attacker", "Single-Target Nuke", "Pivot", "Weather Setters", "Terrain Setters", "Protect Support")
    }

    val teamTags = team.flatMap { it.pokemon?.tags ?: emptyList() }.toSet()

    Column {
        items.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { item ->
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        val isChecked = teamTags.contains(item)
                        Text(if (isChecked) "✅" else "❌", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(item, color = Color.White, fontSize = 11.sp)
                    }
                }
                if (row.size < 3) Spacer(Modifier.weight(3f - row.size))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

fun calculateDefenceStats(team: List<BuildSlot>): Map<String, Int> {
    val stats = mutableMapOf<String, Int>()
    val allTypes = pokemonTypes.map { it.name.uppercase() }.filter { it != "ALL" }
    allTypes.forEach { stats[it] = 0 }
    team.forEach { slot ->
        val pokemon = slot.pokemon ?: return@forEach
        allTypes.forEach { attackerType ->
            var effectiveness = 1.0
            pokemon.types.forEach { defenderType ->
                effectiveness *= getEffectiveness(attackerType, defenderType.uppercase())
            }
            if (effectiveness > 1.0) stats[attackerType] = (stats[attackerType] ?: 0) - 1
            else if (effectiveness < 1.0) stats[attackerType] = (stats[attackerType] ?: 0) + 1
        }
    }
    return stats
}

fun calculateCoverageStats(team: List<BuildSlot>): Map<String, Int> {
    val stats = mutableMapOf<String, Int>()
    val allTypes = pokemonTypes.map { it.name.uppercase() }.filter { it != "ALL" }
    allTypes.forEach { stats[it] = 0 }
    team.forEach { slot ->
        slot.selectedMoves.forEach { move ->
            if (move == null || move.power == null) return@forEach
            val attackerType = move.type.uppercase()
            allTypes.forEach { defenderType ->
                val effectiveness = getEffectiveness(attackerType, defenderType)
                if (effectiveness > 1.0) stats[defenderType] = (stats[defenderType] ?: 0) + 1
                else if (effectiveness < 1.0) stats[defenderType] = (stats[defenderType] ?: 0) - 1
            }
        }
    }
    return stats
}

fun getEffectiveness(attacker: String, defender: String): Double {
    return when (attacker) {
        "NORMAL" -> when (defender) { "ROCK", "STEEL" -> 0.5; "GHOST" -> 0.0; else -> 1.0 }
        "FIRE" -> when (defender) { "GRASS", "ICE", "BUG", "STEEL" -> 2.0; "FIRE", "WATER", "ROCK", "DRAGON" -> 0.5; else -> 1.0 }
        "WATER" -> when (defender) { "FIRE", "GROUND", "ROCK" -> 2.0; "WATER", "GRASS", "DRAGON" -> 0.5; else -> 1.0 }
        "GRASS" -> when (defender) { "WATER", "GROUND", "ROCK" -> 2.0; "FIRE", "GRASS", "POISON", "FLYING", "BUG", "DRAGON", "STEEL" -> 0.5; else -> 1.0 }
        "ELECTRIC" -> when (defender) { "WATER", "FLYING" -> 2.0; "ELECTRIC", "GRASS", "DRAGON" -> 0.5; "GROUND" -> 0.0; else -> 1.0 }
        "ICE" -> when (defender) { "GRASS", "GROUND", "FLYING", "DRAGON" -> 2.0; "FIRE", "WATER", "ICE", "STEEL" -> 0.5; else -> 1.0 }
        "FIGHTING" -> when (defender) { "NORMAL", "ICE", "ROCK", "DARK", "STEEL" -> 2.0; "POISON", "FLYING", "PSYCHIC", "BUG", "FAIRY" -> 0.5; "GHOST" -> 0.0; else -> 1.0 }
        "POISON" -> when (defender) { "GRASS", "FAIRY" -> 2.0; "POISON", "GROUND", "ROCK", "GHOST" -> 0.5; "STEEL" -> 0.0; else -> 1.0 }
        "GROUND" -> when (defender) { "FIRE", "ELECTRIC", "POISON", "ROCK", "STEEL" -> 2.0; "GRASS", "BUG" -> 0.5; "FLYING" -> 0.0; else -> 1.0 }
        "FLYING" -> when (defender) { "GRASS", "FIGHTING", "BUG" -> 2.0; "ELECTRIC", "ROCK", "STEEL" -> 0.5; else -> 1.0 }
        "PSYCHIC" -> when (defender) { "FIGHTING", "POISON" -> 2.0; "PSYCHIC", "STEEL" -> 0.5; "DARK" -> 0.0; else -> 1.0 }
        "BUG" -> when (defender) { "GRASS", "PSYCHIC", "DARK" -> 2.0; "FIRE", "FIGHTING", "POISON", "FLYING", "GHOST", "STEEL", "FAIRY" -> 0.5; else -> 1.0 }
        "ROCK" -> when (defender) { "FIRE", "ICE", "FLYING", "BUG" -> 2.0; "FIGHTING", "GROUND", "STEEL" -> 0.5; else -> 1.0 }
        "GHOST" -> when (defender) { "PSYCHIC", "GHOST" -> 2.0; "DARK" -> 0.5; "NORMAL" -> 0.0; else -> 1.0 }
        "DRAGON" -> when (defender) { "DRAGON" -> 2.0; "STEEL" -> 0.5; "FAIRY" -> 0.0; else -> 1.0 }
        "DARK" -> when (defender) { "PSYCHIC", "GHOST" -> 2.0; "FIGHTING", "DARK", "FAIRY" -> 0.5; else -> 1.0 }
        "STEEL" -> when (defender) { "ICE", "ROCK", "FAIRY" -> 2.0; "FIRE", "WATER", "ELECTRIC", "STEEL" -> 0.5; else -> 1.0 }
        "FAIRY" -> when (defender) { "FIGHTING", "DRAGON", "DARK" -> 2.0; "FIRE", "POISON", "STEEL" -> 0.5; else -> 1.0 }
        else -> 1.0
    }
}

fun getTypeColor(typeName: String): Color {
    return pokemonTypes.find { it.name.uppercase() == typeName.uppercase() }?.color ?: Color.Gray
}
