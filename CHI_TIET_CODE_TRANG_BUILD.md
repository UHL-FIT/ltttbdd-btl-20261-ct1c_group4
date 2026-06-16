# GIẢI MÃ CODE: BUILD SCREEN & VIEWMODEL

Bảng tra cứu các dòng code xử lý logic đội hình và giao diện chọn Pokemon.

### 1. BuildViewModel.kt (Logic nghiệp vụ)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 26 | `data class BuildSlot(...)` | Định nghĩa cấu trúc một ô trong đội hình (Pokemon + Moves + Item + Ability). |
| 66 | `val blockedBaseIds = mutableSetOf<String>()` | Tạo danh sách chặn để ngăn việc chọn 2 Pokemon cùng loài (Species Clause). |
| 71 | `blockedBaseIds.add(p.id.split(".")[0])` | Trích xuất ID gốc để kiểm tra trùng lặp (ví dụ: Charizard và Mega Charizard có cùng gốc). |
| 81 | `if (p.name.contains("Mega", ignoreCase = true))` | Kiểm tra sự hiện diện của Pokemon Mega để thực thi luật "Một Mega mỗi đội". |
| 94 | `val isMegaAllowed = ...` | Logic Filter: Tự động ẩn các Pokemon Mega khác nếu đội đã có 1 Mega. |
| 152 | `updatePokemonInActiveSlot(pokemonSummary: PokemonSummary?)` | Hàm trung tâm xử lý việc thay đổi Pokemon và cập nhật dữ liệu liên quan. |
| 162 | `val hasConflict = currentTeam.indices.any { ... }` | Thuật toán kiểm tra xung đột dòng tiến hóa (VD: Không cho chọn cả Pikachu và Raichu). |
| 210 | `fun getRequiredAbility(pokemon: Pokemon): String?` | Tự động gán kỹ năng bắt buộc cho các hình thái đặc biệt (VD: Stance Change của Aegislash). |
| 223 | `fun getRequiredMove(pokemon: Pokemon): MoveInfo?` | Ép buộc học chiêu thức điều kiện (VD: Mega Rayquaza phải có Dragon Ascent). |
| 230 | `fun getAutoItemForPokemon(pokemon: Pokemon): Item?` | **AI gán Item**: Tự động tìm đúng đá Mega tương ứng với Pokemon vừa chọn (ví dụ: Alakazite cho Alakazam Mega). |
| 312 | `fun matchesRegion(id: String, region: String): Boolean` | Phân loại Pokemon theo vùng miền (Gen 1 - Gen 9) dựa trên ID quốc gia (ví dụ: 1-151 là Gen 1). |
| 400 | `fun saveTeam(context: Context, teamName: String, ...)` | Lưu đội hình vào máy dưới dạng JSON thông qua SharedPreferences. |
| 407 | `val type = object : TypeToken<MutableMap<String, List<BuildSlot>>>() {}.type` | Sử dụng Reflection để định nghĩa kiểu dữ liệu phức tạp cho Gson khi lưu/tải JSON. |

### 2. BuildScreen.kt (Giao diện tương tác)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 56 | `val team by buildViewModel.team.collectAsState()` | Kết nối UI với dữ liệu trong ViewModel để tự động cập nhật (Recomposition) khi có thay đổi. |
| 98 | `team.forEachIndexed { index, slot -> ... }` | Vẽ 6 ô Slot đội hình, xác định ô nào đang được người dùng nhấn vào thông qua `activeSlotIndex`. |
| 144 | `OutlinedTextField(..., readOnly = true, ...)` | Ô hiển thị tên nhưng không cho nhập chữ, dùng `clickable` để mở hộp thoại chọn Pokemon. |
| 264 | `if (showPokemonPicker) { LazyPicker(...) }` | Hiển thị hộp thoại chọn Pokemon với tính năng tìm kiếm và lọc dữ liệu. |
| 320 | `fun MovePicker(...)` | Component chọn chiêu thức, lọc theo Pokemon và phân loại kỹ năng. |
| 430 | `fun <T> LazyPicker(...)` | Component Generic dùng chung cho việc chọn Pokemon, Item, Kỹ năng với bộ lọc tìm kiếm. |
| 790 | `fun calculateDefenceStats(...)` | Thuật toán tính toán điểm yếu hệ của cả đội (nhân hệ số tương khắc của tất cả thành viên). |
| 808 | `fun calculateCoverageStats(...)` | Thuật toán tính độ bao phủ tấn công của 24 chiêu thức (4 chiêu x 6 Pokemon) trong đội hình. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng `baseId`?** -> Để gộp các dạng (Alola, Mega, Galar) về cùng 1 gốc để kiểm tra trùng loài.
*   **Luật Mega xử lý ở đâu?** -> Xử lý trong `filteredPokemon` của ViewModel bằng cách kiểm tra tên có chứa chữ "Mega".
*   **Lưu đội hình ở đâu?** -> Lưu trong `SharedPreferences` dưới dạng chuỗi JSON thông qua thư viện `Gson`.
