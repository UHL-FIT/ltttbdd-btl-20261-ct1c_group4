package com.example.pokedex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pokedex.data.TierMode
import com.example.pokedex.model.PokemonSummary
import com.example.pokedex.ui.components.PrivacyFooter
import com.example.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.launch

data class PokemonType(val name: String, val color: Color)

val pokemonTypes = listOf(
    PokemonType("All", Color(0xFF00B0FF)),
    PokemonType("Normal", Color(0xFFA8A77A)),
    PokemonType("Fire", Color(0xFFEE8130)),
    PokemonType("Water", Color(0xFF6390F0)),
    PokemonType("Grass", Color(0xFF7AC74C)),
    PokemonType("Electric", Color(0xFFF7D02C)),
    PokemonType("Ice", Color(0xFF96D9D6)),
    PokemonType("Fighting", Color(0xFFC22E28)),
    PokemonType("Poison", Color(0xFFA33EA1)),
    PokemonType("Ground", Color(0xFFE2BF65)),
    PokemonType("Flying", Color(0xFFA98FF3)),
    PokemonType("Psychic", Color(0xFFF95587)),
    PokemonType("Bug", Color(0xFFA6B91A)),
    PokemonType("Rock", Color(0xFFB6A136)),
    PokemonType("Ghost", Color(0xFF735797)),
    PokemonType("Dragon", Color(0xFF6F35FC)),
    PokemonType("Steel", Color(0xFFB7B7CE)),
    PokemonType("Dark", Color(0xFF705746)),
    PokemonType("Fairy", Color(0xFFD685AD))
)

@Immutable
data class TierDataWrapper(
    val roleMap: Map<String, List<PokemonSummary>>
)

@Composable
fun TierScreen(navController: NavController, viewModel: PokemonViewModel = viewModel()) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val tieredPokemon by viewModel.tieredPokemon.collectAsState()
    
    androidx.compose.runtime.LaunchedEffect(pokemonList.size) {
        if (pokemonList.size < 500) {
            viewModel.loadNextPage()
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showBackToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }

    val roles = remember(selectedMode) {
        listOf("Sweeper", "Support Sweeper", "Support", "Wall")
    }

    val tiers = remember { listOf("TEE", "T0", "T0.5", "T1", "T1.5", "T2", "T3", "T4", "T5") }

    // Tạo trạng thái cuộn ngang dùng chung cho toàn bộ bảng để đồng bộ
    val horizontalScrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF1E1E26),
        floatingActionButton = {
            AnimatedVisibility(
                visible = showBackToTop,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = Color(0xFF00B0FF),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Back to top")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                TierHeaderSection(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TierAccordion()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ModeSelectorGrid(selectedMode) { viewModel.setSelectedMode(it) }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm kiếm Pokemon...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00B0FF),
                        unfocusedBorderColor = Color(0xFF32323E),
                        focusedContainerColor = Color(0xFF252530),
                        unfocusedContainerColor = Color(0xFF252530),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TypeFilterBar(selectedType) { viewModel.setSelectedType(it) }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Thanh tiêu đề nhóm TIER (Ví dụ: APEX)
            item {
                MetaLineHeader("APEX CHARACTERS", Color(0xFFFF4444))
            }

            items(tiers.take(3), key = { "apex_$it" }) { tier ->
                TierRowGrid(tier, roles, tieredPokemon[tier] ?: emptyMap(), navController, horizontalScrollState)
                HorizontalDivider(color = Color(0xFF1E1E26), thickness = 2.dp)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                MetaLineHeader("META CHARACTERS", Color(0xFFFFBB33))
            }

            items(tiers.drop(3).take(3), key = { "meta_$it" }) { tier ->
                TierRowGrid(tier, roles, tieredPokemon[tier] ?: emptyMap(), navController, horizontalScrollState)
                HorizontalDivider(color = Color(0xFF1E1E26), thickness = 2.dp)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                MetaLineHeader("OTHERS", Color(0xFF44FF44))
            }

            items(tiers.drop(6), key = { "other_$it" }) { tier ->
                TierRowGrid(tier, roles, tieredPokemon[tier] ?: emptyMap(), navController, horizontalScrollState)
                HorizontalDivider(color = Color(0xFF1E1E26), thickness = 2.dp)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                PrivacyFooter()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TypeFilterBar(selectedType: String, onTypeSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(pokemonTypes) { type ->
            val isSelected = selectedType == type.name
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .background(
                        if (isSelected) type.color else Color(0xFF252530),
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        if (isSelected) Color.White.copy(alpha = 0.3f) else Color(0xFF32323E),
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onTypeSelected(type.name) }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (type.name == "All") "*" else type.name.uppercase(),
                    color = if (isSelected) Color.White else type.color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun MetaLineHeader(title: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = " $title ",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(color))
    }
}

@Composable
fun TierRowGrid(
    tier: String,
    roles: List<String>,
    roleMap: Map<String, List<PokemonSummary>>,
    navController: NavController,
    horizontalScrollState: androidx.compose.foundation.ScrollState
) {
    val tierWidth = 50.dp
    val roleColumnWidth = 220.dp // Đủ cho khoảng 3 Pokemon mỗi hàng

    Row(
        modifier = Modifier
            .horizontalScroll(horizontalScrollState)
            .height(IntrinsicSize.Min)
            .background(Color(0xFF1E1E26))
    ) {
        // Cột nhãn TIER bên trái
        Box(
            modifier = Modifier
                .width(tierWidth)
                .fillMaxHeight()
                .background(getTierColor(tier))
                .border(0.5.dp, Color.Black.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tier,
                color = if (tier == "TEE" || tier == "T2") Color.Black else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                modifier = Modifier.graphicsLayer(rotationZ = -0f) // Có thể xoay dọc nếu muốn giống ảnh hơn
            )
        }

        // Các cột Role
        roles.forEach { role ->
            val pokemonInRole = roleMap[role] ?: emptyList()
            
            Column(
                modifier = Modifier
                    .width(roleColumnWidth)
                    .fillMaxHeight()
                    .border(0.5.dp, Color(0xFF32323E))
                    .padding(8.dp)
            ) {
                // Tên Role nhỏ ở trên mỗi ô (tùy chọn, giống ảnh mẫu)
                Text(
                    text = role.uppercase(),
                    color = Color.Gray,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Chia danh sách Pokemon thành các hàng, mỗi hàng tối đa 3 item
                val rows = pokemonInRole.chunked(3)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pokemonInRole.isEmpty()) {
                        PokemonPlaceholderEmpty()
                    } else {
                        rows.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { pokemon ->
                                    key(pokemon.id) {
                                        PokemonFrame(pokemon, navController)
                                    }
                                }
                                // Thêm placeholder trắng nếu hàng chưa đủ 3 để giữ alignment
                                if (rowItems.size < 3) {
                                    repeat(3 - rowItems.size) {
                                        Box(modifier = Modifier.size(62.dp)) 
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
fun PokemonFrame(pokemon: PokemonSummary, navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageRequest = remember(pokemon.imageUrl) {
        coil.request.ImageRequest.Builder(context)
            .data(pokemon.imageUrl)
            .crossfade(true)
            .size(100, 100)
            .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .build()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clickable {
                navController.navigate("pokemon_detail/${pokemon.id}")
            }
    ) {
        // Frame chính chứa ảnh và các nhãn
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(Color(0xFF252530), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFF32323E), RoundedCornerShape(4.dp))
        ) {
            // Nhãn loại (Types) ở góc trên
            Row(
                modifier = Modifier.padding(3.dp).align(Alignment.TopStart),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                pokemon.types.take(2).forEach { typeName ->
                    val color = pokemonTypes.find { it.name.equals(typeName, ignoreCase = true) }?.color ?: Color.Gray
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                            .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            // Ảnh Pokemon
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .align(Alignment.Center)
            )
            
            // Nhãn Variant nếu có
            if (pokemon.id.contains(".")) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(Color(0xFF00B0FF).copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 2.dp)
                ) {
                    Text("V", color = Color.White, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(3.dp))
        
        // Tên Pokemon viết gọn
        Text(
            text = pokemon.baseName,
            color = Color.LightGray,
            fontSize = 8.sp,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )
        
        // Tag quan trọng nhất (Role tag)
        val mainTag = pokemon.tags.firstOrNull() ?: ""
        if (mainTag.isNotEmpty()) {
            Text(
                text = mainTag.uppercase(),
                color = Color(0xFF44FF44),
                fontSize = 7.sp,
                maxLines = 1,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 7.sp
            )
        }
    }
}

@Composable
fun PokemonPlaceholderEmpty() {
    Box(
        modifier = Modifier
            .size(62.dp)
            .background(Color(0xFF1E1E26).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF32323E).copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("?", color = Color.Gray.copy(alpha = 0.2f), fontSize = 16.sp)
    }
}

fun getTierColor(tier: String): Color = when (tier) {
    "TEE" -> Color(0xFFAA88FF)
    "T0" -> Color(0xFFFF4444)
    "T0.5" -> Color(0xFFFF7744)
    "T1" -> Color(0xFFFFAA44)
    "T1.5" -> Color(0xFFFFCC44)
    "T2" -> Color(0xFFFFFF44)
    "T3" -> Color(0xFFAAFF44)
    "T4" -> Color(0xFF44FF44)
    "T5" -> Color(0xFF44FFCC)
    else -> Color(0xFF555555)
}

@Composable
fun TierHeaderSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Home",
                color = Color(0xFF00B0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text(
                text = " / ",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = "Bảng Xếp Hạng",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Bảng Xếp Hạng",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Đây là bảng xếp hạng phổ biến nhất dành cho Pokemon, đánh giá tất cả các Pokemon dựa trên hiệu năng của nó trong PVE, PVP (1v1) và PVP (2v2)",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Last updated: 22/05/2026",
            color = Color.Gray.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TierAccordion() {
    val accordionData = listOf(
        "Về bảng xếp hạng",
        "Phân loại & Nhãn đặc biệt",
        "Thứ hạng & Meta Lines",
        "Tiêu chuẩn"
    )
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF252530), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF32323E), RoundedCornerShape(4.dp))
    ) {
        accordionData.forEachIndexed { index, title ->
            var expanded by remember { mutableStateOf(false) }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.Gray)
                }
                if (expanded) {
                    when (title) {
                        "Về bảng xếp hạng" -> AboutTierListContent()
                        "Phân loại & Nhãn đặc biệt" -> TypesAndNotesContent()
                        "Thứ hạng & Meta Lines" -> RankingsAndColumnsContent()
                        "Tiêu chuẩn" -> ScoringMethodContent()
                    }
                }
                if (index < accordionData.size - 1) HorizontalDivider(color = Color(0xFF1E1E26), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun AboutTierListContent() {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            buildAnnotatedString {
                append("Xin lưu ý rằng Pokémon là một trò chơi mà ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append("việc xây dựng đội hình (team building) là quan trọng nhất")
                }
                append(" và mặc dù bảng xếp hạng của chúng tôi tính đến các thiết lập tối ưu, rất nhiều Pokémon vẫn có thể hoạt động tốt - ngay cả những loài hạng thấp - khi bạn đầu tư vào chúng.")
            },
            color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
        )

        Text(
            buildAnnotatedString {
                append("Các bảng xếp hạng này đánh giá Pokémon dựa trên hiệu suất trung bình trong ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append("Đấu Đơn (Singles), Đấu Đôi (VGC) và Đánh Raid")
                }
                append(" bất kể các biến động tạm thời ")
                withStyle(SpanStyle(color = Color(0xFFFF4444), fontWeight = FontWeight.Bold)) {
                    append("(xét 3 giai đoạn gần nhất)")
                }
                append(". Những Pokémon hạng cao sẽ thi đấu tốt mà không cần phụ thuộc quá nhiều vào cơ chế mùa giải.")
            },
            color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
        )

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append("Quan trọng! Pokémon được sắp xếp theo bảng chữ cái trong cùng một hạng.")
                }
            },
            color = Color.Gray, fontSize = 13.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Các bảng xếp hạng hiện có:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            val categories = listOf(
                "Đấu Đơn (Singles)" to "Cách Pokémon thể hiện trong trận 1v1. Sát thương đơn mục tiêu và tốc độ là yếu tố then chốt.",
                "Đấu Đôi (VGC)" to "Cách Pokémon thể hiện trong trận 2v2. Các chiêu thức diện rộng (AoE) và hỗ trợ đồng đội cực kỳ quan trọng.",
                "Đánh Raid (Tera Raid)" to "Cách Pokémon thể hiện khi đấu Boss. Khả năng trụ sân và sát thương dồn mục tiêu được ưu tiên."
            )

            categories.forEach { (name, desc) ->
                Row(modifier = Modifier.padding(start = 8.dp)) {
                    Text("• ", color = Color.Gray)
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                                append("$name - ")
                            }
                            append(desc)
                        },
                        color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Về các đánh giá:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                "Để đưa ra thứ hạng, chúng tôi kết hợp dữ liệu thống kê từ người chơi, các đợt thử nghiệm bám sát meta và phản hồi từ cộng đồng trên các nền tảng.",
                color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun TypesAndNotesContent() {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Categories
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Categories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            
            val categories = listOf(
                Triple("Sweeper", Color(0xFFFF4444), "Pokémon tập trung hoàn toàn vào việc gây sát thương và là nguồn sát thương chính trong đội hình."),
                Triple("Support Sweeper", Color(0xFFAA88FF), "Những Pokémon thuộc nhóm này hiếm khi đóng vai trò sát thương chính đơn độc, thường đi cùng chủ lực khác để bổ trợ sát thương hoặc gây hiệu ứng."),
                Triple("Support", Color(0xFFFFBB33), "Không tập trung vào sát thương mà khuếch đại sức mạnh đồng đội qua buff hoặc làm suy yếu đối thủ qua debuff."),
                Triple("Wall / Tank", Color(0xFF44FF44), "Những Pokémon có khả năng chống chịu cao, giữ cho đội hình trụ vững trước các đợt tấn công của đối thủ.")
            )

            categories.forEach { (name, color, desc) ->
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                            append("• $name")
                        }
                        append(" - $desc")
                    },
                    color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
                )
            }
        }

        // Section: Special tags
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Special tags", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Tags are split into 3 categories: Pros, Cons and Archetype which represent the most defining features of a character's kit and the current meta archetypes they fit into.",
                color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
            )

            val tags = listOf(
                Triple("Hazard / Weather / Terrain", Color(0xFFAA88FF), "Chuyên đặt các loại bẫy trên sân, thiết lập thời tiết hoặc địa hình để hỗ trợ đồng đội."),
                Triple("Status", Color(0xFFFF4444), "Pokémon thuộc lối chơi chuyên gây các hiệu ứng bất lợi (Độc, Bỏng, Tê liệt, Ngủ) hoặc có bộ kỹ năng phụ thuộc trực tiếp vào việc kẻ địch bị dính trạng thái."),
                Triple("Pivot", Color(0xFF44FF44), "Sở hữu các kỹ năng luân chuyển linh hoạt, có khả năng vô hiệu hóa hoặc cản trở hành động của kẻ địch trong trận đấu bằng các chiêu thức ưu tiên nhằm Phá bĩnh kẻ địch."),
                Triple("Redirector", Color(0xFF33B5E5), "Pokémon hỗ trợ chuyên sử dụng các chiêu thức đặc biệt để ép mọi đòn tấn công đơn mục tiêu của đối thủ phải nhắm vào chính nó thay vì nhắm vào đồng đội, Chịu đòn thay để bảo kê an toàn cho các chủ lực sát thương (Sweeper) tấn công, hoặc tạo khoảng trống cho đồng đội sử dụng các chiêu thức Setup (như Dragon Dance, Swords Dance) và thiết lập kiểm soát sân đấu (như Trick Room, Tailwind)."),
                Triple("Wallbreaker", Color(0xFFFFBB33), "Pokémon được thiết kế with sức tấn công hủy diệt (vật lý, đặc biệt hoặc cả hai), có khả năng đánh vỡ các cấu trúc phòng ngự kiên cố (Walls/Stall) của đối phương mà các Pokémon tấn công thông thường không thể làm xước. Khác với Sweeper (Kẻ càn quét) cần tốc độ cao để dọn dẹp tàn cuộc, Wallbreaker không nhất thiết phải nhanh. Nhiệm vụ duy nhất của chúng là đục thủng những \"bức tường\" khó chịu nhất của đối thủ ở giai đoạn đầu và giữa trận. Một khi phòng tuyến của địch sụp đổ, các Sweeper ở tuyến sau mới có thể dọn sạch phần còn lại của trận đấu."),
                Triple("Speed Control", Color(0xFFFFBB33), "Pokémon có thể thao túng thứ tự ra đòn bằng cách tăng tốc cho phe mình (Tailwind), đảo ngược tốc độ (Trick Room) hoặc làm chậm đối phương (Icy Wind, Electroweb)."),
                Triple("Splashable / Low-Maintenance", Color(0xFF44FF44), "Pokémon hoạt động độc lập cực kỳ tốt, dễ dàng ghép vào hầu hết mọi đội hình mà không cần nhiều sự hỗ trợ từ đồng đội."),
                Triple("High-Maintenance", Color(0xFFFF4444), "Pokémon yêu cầu rất nhiều sự hỗ trợ xung quanh (như cần người kiểm soát tốc độ, cần người dọn hầm chông, hoặc bị khóa chiêu do mang đồ Choice) mới có thể hoạt động hiệu quả.")
            )

            tags.forEach { (name, color, desc) ->
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                            append("• $name")
                        }
                        append(" - $desc")
                    },
                    color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun RankingsAndColumnsContent() {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section: Meta Lines
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Meta Lines",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Hệ thống Meta Lines phân loại danh sách xếp hạng thành các nhóm sức mạnh (power brackets). Dưới đây là năm nhóm chính:",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            val metaLines = listOf(
                Triple("TEE (Everything Else)", Color(0xFFAA88FF), "Bậc tối thượng phá vỡ mọi quy chuẩn cân bằng, thường bị cấm ở hầu hết các thể thức."),
                Triple("Apex Characters (T0 & T0.5)", Color(0xFFFF4444), "Đỉnh cao của meta. Những Pokémon này có thể gây sát thương khổng lồ hoặc sinh tồn hoàn hảo."),
                Triple("Meta Characters (T1, T1.5, T2)", Color(0xFFFFBB33), "Những lựa chọn mạnh mẽ giúp việc giành chiến thắng dễ dàng, nhưng cần kỹ năng điều khiển cao hơn."),
                Triple("Off-meta Characters (T3 & T4)", Color(0xFF44FF44), "Thiếu hụt một vài yếu tố nhưng vẫn tỏa sáng rực rỡ nếu có đội hình và chiến thuật phù hợp."),
                Triple("The Forgotten Ones (T5)", Color(0xFF33B5E5), "Gặp nhiều khó khăn. Bộ kỹ năng không còn hiệu quả hoặc yêu cầu lượng đầu tư quá lớn để hoạt động.")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                metaLines.forEach { (title, color, desc) ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                                append("• $title")
                            }
                            append(" - $desc")
                        },
                        color = Color.LightGray.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section: Half tiers
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Half tiers",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Để phản ánh chính xác nhất chênh lệch sức mạnh, chúng tôi đã thêm các bậc rưỡi (T0.5, T1.5...). Trước đây, việc nhảy bậc nguyên khiến việc định vị Pokémon trở nên gượng ép, dẫn đến nhiều Pokémon bị dồn chung vào một bậc dù có sự chênh lệch rõ rệt về hiệu quả thực tế.",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ScoringMethodContent() {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section: Criteria
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tiêu chuẩn", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            val criteriaList = listOf(
                "Thi đấu thực tế do người chơi tính toán và điều khiển,",
                "Được đặt trong một đội hình phối hợp có tính hiệp đồng (Synergy) tối ưu cùng với các Pokémon hỗ trợ tốt nhất,",
                "Pokémon đạt Cấp độ 50 (chuẩn thi đấu VGC/Battle Stadium) hoặc Cấp độ 100 (chuẩn Smogon),",
                "Sở hữu Đặc tính (Ability) hoàn hảo nhất và bộ Chiêu thức (Moveset) được tối ưu cho vai trò của chúng,",
                "Được trang bị Vật phẩm cầm tay (Held Item) tốt nhất,",
                "Tối ưu hóa hoàn toàn các chỉ số: IVs (Chỉ số cá thể) đạt mức cần thiết, EVs (Chỉ số nỗ lực) được phân bổ chuẩn xác để vượt qua các mốc sát thương/tốc độ cụ thể, và Tính cách (Nature) lý tưởng nhất,"
            )
            criteriaList.forEach { text ->
                Text(
                    text = "• $text",
                    color = Color.LightGray.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Section: Roles
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Vai trò", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Chúng tôi so sánh các Pokémon trong cùng một nhóm vai trò - vì vậy các Chủ lực (Sweeper) sẽ được đánh giá so với các Chủ lực khác, và thứ hạng được điều chỉnh dựa trên màn thể hiện của chúng trong vai trò đó. Đừng cố gắng so sánh chéo giữa các vai trò, bởi vì xếp hạng của chúng mang những ý nghĩa hoàn toàn khác nhau.",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            val rolesInfo = listOf(
                Triple("Sweeper / Wallbreaker", Color(0xFFFF4444), "Xếp hạng bị ảnh hưởng nặng nề bởi khả năng giáng đòn kết liễu (OHKO/2HKO) and tạo áp lực càn quét đội hình đối phương. Khả năng dọn dẹp tàn cuộc càng dễ dàng và nhanh chóng, thứ hạng càng cao."),
                Triple("Support / Disruptor", Color(0xFFAA88FF), "Xếp hạng phụ thuộc vào khả năng kiểm soát sân đấu (Speed Control, rải/gỡ Hazard), tăng cường sức mạnh cho đồng đội, hoặc vô hiệu hóa các mắt xích quan trọng của kẻ địch."),
                Triple("Utility Attacker / Pivot", Color(0xFFFFBB33), "Xếp hạng dựa trên lượng sát thương cấu rỉa an toàn, khả năng kiểm soát nhịp độ (Momentum thông qua U-turn, Volt Switch) và những lợi thế chiến thuật đột biến mà chúng mang lại cho đội."),
                Triple("Wall / Redirector", Color(0xFF44FF44), "Xếp hạng được quyết định bởi khả năng chống chịu bền bỉ, bảo kê chủ lực an toàn and duy trì trạng thái tốt cho toàn đội. Độ an toàn mang lại và tính đa dụng càng cao, xếp hạng càng lớn.")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rolesInfo.forEach { (name, color, desc) ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                                append(name)
                            }
                            append(" - $desc")
                        },
                        color = Color.LightGray.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section: Other criteria
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Các Tiêu chuẩn ảnh hưởng đến việc đánh giá", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                text = buildAnnotatedString {
                    append("Mỗi bảng xếp hạng đánh giá Pokémon dựa trên giá trị trung bình của chúng trong thể thức thi đấu cụ thể (VGC, Đấu Đơn, v.v.). ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append("Thứ hạng càng cao, Pokémon đó càng có tác động lớn đến Meta và càng dễ dàng tỏa sáng trong nhiều đội hình, giúp người chơi leo Rank hiệu quả. ")
                    }
                    withStyle(SpanStyle(color = Color(0xFFFF4444))) {
                        append("Chúng tôi không chỉ nhìn vào những kịch bản \"Hoàn hảo 1 hiệp\" (1-turn Sweep),")
                    }
                    append(" mà còn xem xét:")
                },
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            val otherCriteria = listOf(
                "Tính linh hoạt (Flexibility)" to "Khả năng hoạt động của Pokémon khi đối đầu với nhiều loại đội hình khác nhau (Trick Room, Weather, Terrain) và khả năng đóng góp kể cả khi đối thủ có lợi thế về Hệ (Type matchup).",
                "Chi phí vận hành (Investment / High-Maintenance)" to "Một số Pokémon đòi hỏi phải có đồng đội hỗ trợ vô cùng khắt khe (phải có người thiết lập thời tiết, phải mớm tốc độ, hoặc bắt buộc phải tiêu tốn quyền dùng Terastallization/Dynamax của cả đội) mới có thể sống sót và gây sát thương. Yêu cầu \"bảo kê\" càng khắt khe, thứ hạng sẽ càng bị ảnh hưởng tiêu cực.",
                "Tính đa dụng / Nén vai trò (Utility / Role Compression)" to "Các Pokémon meta hiện đại thường sở hữu khả năng vượt ra ngoài vai trò chính của chúng. Ví dụ: Một Pokémon phòng thủ (Wall) nhưng có thể đe dọa sát thương lớn nhờ đòn đánh dựa trên chỉ số phòng thủ (như Body Press), hoặc một Chủ lực có khả năng tự hồi phục tốt. Khả năng gánh vác nhiều vai trò cùng lúc càng tốt, tác động tích cực đến thứ hạng của Pokémon càng lớn."
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                otherCriteria.forEach { (title, desc) ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                                append("• $title")
                            }
                            append(" - $desc")
                        },
                        color = Color.LightGray.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModeSelectorGrid(selectedMode: TierMode, onModeSelected: (TierMode) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TierMode.entries.forEach { mode ->
            val isSelected = selectedMode == mode
            Box(
                modifier = Modifier.weight(1f).height(80.dp)
                    .background(if (isSelected) Color(0xFF00B0FF).copy(0.15f) else Color(0xFF2D2D39), RoundedCornerShape(8.dp))
                    .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color(0xFF00B0FF) else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { onModeSelected(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(mode.label, color = if (isSelected) Color(0xFF00B0FF) else Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TierScreenPreview() { PokedexTheme { TierScreen(rememberNavController()) } }
