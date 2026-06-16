# HƯỚNG DẪN TRẢ LỜI CÂU HỎI BẢO VỆ ĐỒ ÁN (UI & FLOW)

Tài liệu này hướng dẫn cách chỉ ra vị trí code và giải thích luồng hoạt động của 3 màn hình chính theo quy trình chuẩn.

---

## 1. MÀN HÌNH DANH SÁCH POKEMON (Pokemon List)
**Vị trí file:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonScreen.kt`

### Câu hỏi: Giải thích luồng khi người dùng nhập vào thanh Tìm kiếm
- **Bước 1 (View):** Tìm Composable `OutlinedTextField` tại **Dòng 133**.
- **Bước 2 (Cấu hình):** Giao diện sử dụng `Modifier.fillMaxWidth()`, màu sắc định nghĩa trong `colors = OutlinedTextFieldDefaults.colors(...)` (Dòng 140-147).
- **Bước 3 (Click/Event):** Khi người dùng nhập, sự kiện `onValueChange` được kích hoạt (Dòng 135).
- **Bước 4 (ViewModel):** Nó gọi hàm `viewModel.setSearchQuery(it)`.
- **Bước 5 (Data):** ViewModel cập nhật `StateFlow`. UI sử dụng `collectAsState()` (Dòng 69) để nhận dữ liệu mới và tự động vẽ lại (Recompose) danh sách.

### Câu hỏi: Giải thích luồng khi Click vào một Pokemon để xem chi tiết
- **Bước 1 (View):** Tìm Composable `PokemonRow` được gọi tại **Dòng 177**.
- **Bước 2 (Cấu hình):** Hàng có `Modifier.clickable` (Dòng 213) để phản hồi khi nhấn.
- **Bước 3 (Event):** Khi nhấn vào Tên, nó gọi lambda `onNameClick(pokemon.id)` (Dòng 248).
- **Bước 4 (Navigation):** Tại màn hình chính (Dòng 172), lệnh `navController.navigate("pokemon_detail/$id")` được thực thi.

---

## 2. MÀN HÌNH CHI TIẾT POKEMON (Pokemon Detail)
**Vị trí file:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonDetailScreen.kt`

### Câu hỏi: Giải thích cách hiển thị các Thanh chỉ số (Base Stats)
- **Bước 1 (View):** Tìm Composable `StatRow` định nghĩa tại **Dòng 358**.
- **Bước 2 (Cấu hình):** 
    - Giá trị số hiển thị ở `Text` (Dòng 361).
    - Thanh Progress Bar được vẽ bằng 2 lớp `Box` lồng nhau (Dòng 362-364).
    - Độ dài thanh dựa vào `Modifier.fillMaxWidth(value / 255f)` (Dòng 363).
- **Bước 3 (Logic):** Màu sắc thanh (`color`) được truyền vào từ hàm `getSingleTypeColor` (Dòng 589) dựa theo hệ của Pokemon.

### Câu hỏi: Giải thích luồng hiển thị bảng Kỹ năng (Moves)
- **Bước 1 (View):** Tìm Composable `MovesTable` định nghĩa tại **Dòng 489**.
- **Bước 2 (Cấu hình):** Sử dụng `.filter { it.learnMethod == "level-up" }` để chia nhóm kỹ năng (Dòng 490).
- **Bước 3 (Event):** Mỗi chiêu thức nằm trong `MoveRow` (Dòng 528). Khi nhấn vào hàng, biến `expanded` đảo ngược giá trị (`!expanded`) tại **Dòng 535**.
- **Bước 4 (UI Update):** `AnimatedVisibility` (Dòng 557) dựa vào biến `expanded` để ẩn/hiện chi tiết hiệu ứng của chiêu thức đó.

---

## 3. MÀN HÌNH DANH SÁCH VẬT PHẨM (Item List)
**Vị trí file:** `app/src/main/java/com/example/pokedex/ui/screens/ItemScreen.kt`

### Câu hỏi: Giải thích luồng khi nhấn chọn một Danh mục (Category) để lọc
- **Bước 1 (View):** Tìm Composable `ItemCategoryFilters` định nghĩa tại **Dòng 286**.
- **Bước 2 (Cấu hình):** Sử dụng `LazyRow` (Dòng 287) để cho phép cuộn ngang các nút lọc.
- **Bước 3 (Event):** Mỗi nút lọc có `Modifier.clickable { onSelect(category) }` tại **Dòng 308**.
- **Bước 4 (State):** Khi nhấn, biến `selectedCategory` trong `ItemScreen` (Dòng 44) được cập nhật.
- **Bước 5 (Logic UI):** Danh sách `filteredList` (Dòng 81) tự động tính toán lại và `LazyColumn` vẽ lại các vật phẩm thỏa mãn điều kiện lọc.

### Câu hỏi: Cách hiển thị ảnh Vật phẩm
- **Bước 1 (View):** Trong hàm `ItemRow`, tìm Composable `AsyncImage` tại **Dòng 228**.
- **Bước 2 (Logic):** Sử dụng thư viện **Coil** để tải ảnh từ URL `item.imageUrl`. Có cấu hình `diskCachePolicy` (Dòng 232) để lưu ảnh vào bộ nhớ đệm, giúp lần sau mở lại không cần tải lại từ internet.
