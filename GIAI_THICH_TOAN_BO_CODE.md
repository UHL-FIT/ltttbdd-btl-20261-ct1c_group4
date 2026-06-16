# HƯỚNG DẪN GIẢI THÍCH TOÀN BỘ CODE (DÀNH CHO BẢO VỆ ĐỒ ÁN)

Tài liệu này tổng hợp cách trả lời cho câu hỏi: "Khi click vào button/phần tử này thì chương trình chạy như thế nào?". Cấu trúc trả lời đi từ giao diện (View) xuống logic (ViewModel) và dữ liệu (Repository/Data).

---

## 1. TRANG CHỦ & ĐIỀU HƯỚNG (Home & Navigation)
**File:** `app/src/main/java/com/example/pokedex/ui/screens/HomeScreen.kt`

### Click vào một Shortcut (Ví dụ: "Pokémon")
- **Bước 1 (View):** Nút này là một `ShortcutCard` (Dòng 96 file `HomeScreen.kt`).
- **Bước 2 (UI):** Màu nền chỉnh ở `CardDefaults.cardColors` trong file `ShortcutCard.kt`. Hình ảnh lấy từ `R.drawable.pokemon`.
- **Bước 3 (Action):** Khi click, hàm `onShortcutClick(shortcut.route)` được gọi (Dòng 99 file `HomeScreen.kt`).
- **Bước 4 (Navigation):** Lệnh này nhảy sang `NavGraph.kt`. Tại đây, route `"pokedex_route"` sẽ kích hoạt màn hình danh sách Pokémon.

---

## 2. TRANG DANH SÁCH POKEMON (Pokedex)
**File:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonScreen.kt`

### Click vào một Pokemon để xem chi tiết
- **Bước 1 (View):** Mỗi dòng Pokemon là một Composable `PokemonRow`.
- **Bước 2 (Action):** Khi click, lệnh `navController.navigate("pokemon_detail/${pokemon.id}")` được gọi.
- **Bước 3 (Logic):** `viewModel.loadPokemonDetails(pokemon.id)` (Dòng 193 file `PokemonViewModel.kt`) nạp dữ liệu từ Database/API.

---

## 3. TRANG CHI TIẾT POKEMON (Detail)
**File:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonDetailScreen.kt`

### Xem chỉ số Pokemon (Stats)
- **Vị trí:** Composable `StatRow`.
- **Dòng code quan trọng:** `Modifier.fillMaxWidth(value / 255f)`.
- **Giải thích:** Đây là logic vẽ thanh năng lượng. Vì chỉ số tối đa là 255, lấy giá trị hiện tại chia 255 để ra tỉ lệ phần trăm độ dài thanh màu.

---

## 4. TRANG GIỚI THIỆU GAME & DỮ LIỆU TĨNH (Guide)
**File:** `app/src/main/java/com/example/pokedex/ui/screens/GuideScreen.kt`

### Tại sao dùng dữ liệu tĩnh (Hard-coded)?
- **Trả lời:** Các thông tin như "Bảng tính cách (Nature)" hay "Bảng khắc chế hệ" là quy tắc cố định. Việc lưu trữ trực tiếp giúp app load tức thì, không độ trễ mạng và hoạt động 100% offline.

---

## TỔNG KẾT QUY TRÌNH 5 BƯỚC GIẢI THÍCH:
1. **Vị trí file:** Xác định file `.kt` chứa nút đó.
2. **Thành phần UI:** Nút đó là `Button`, `Card` hay `Row`?
3. **Chỉnh giao diện:** Giải thích `Modifier` (padding, background, weight...).
4. **Hàm xử lý:** Khi click gọi hàm gì?
5. **Dòng dữ liệu:** Lấy từ List tĩnh hay Database?
