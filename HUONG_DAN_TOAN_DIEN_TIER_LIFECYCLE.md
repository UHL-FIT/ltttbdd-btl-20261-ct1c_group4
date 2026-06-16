# Hướng dẫn Toàn diện: Giải thích Code Trang Xếp hạng & Vòng đời (Phục vụ Bảo vệ)

Tài liệu này bóc tách chi tiết từng khối lệnh trong `TierScreen.kt` và `PokemonViewModel.kt`, giải thích theo quy trình 5 bước mà giảng viên yêu cầu.

---

## PHẦN 1: QUẢN LÝ VÒNG ĐỜI (LIFECYCLE) & SIDE-EFFECTS

Đây là phần "khó" nhất thường bị hỏi về cách App khởi tạo và cập nhật.

### 1. Khởi tạo dữ liệu tự động (LaunchedEffect)
- **Vị trí**: `TierScreen.kt` - **Dòng 76**.
- **Mã nguồn**: `LaunchedEffect(pokemonList.size) { ... }`
- **Giải thích 5 bước**:
    1. **Bước 1 (View)**: Đây là một Side-effect gắn vào vòng đời của màn hình.
    2. **Bước 2 (Cấu hình)**: "Key" là `pokemonList.size`. Nó sẽ chạy lại mỗi khi số lượng Pokemon thay đổi.
    3. **Bước 3 (Action)**: Tự động kiểm tra điều kiện `size < 500`.
    4. **Bước 4 (ViewModel)**: Nếu thiếu, gọi `viewModel.loadNextPage()` để tải dữ liệu từ Repository.
    5. **Bước 5 (Data Flow)**: Khi dữ liệu về -> Database cập nhật -> `pokemonList` tăng size -> UI tự động Recompose để hiện bảng.

### 2. Ghi nhớ trạng thái (Persistence)
- **Vị trí**: `TierScreen.kt` - **Dòng 82, 100**.
- **Mã nguồn**: `rememberLazyListState()`, `rememberScrollState()`.
- **Giải thích**: Giúp dữ liệu (vị trí cuộn) không bị mất khi Recomposition xảy ra. Đây là cách App "nhớ" người dùng đang đứng ở đâu trong bảng xếp hạng.

---

## PHẦN 2: CHI TIẾT CÁC THÀNH PHẦN GIAO DIỆN (UI COMPONENTS)

### 1. Bộ chọn chế độ PVP/PVE (ModeSelectorGrid)
- **Vị trí**: Định nghĩa tại **Dòng 421**, gọi tại **Dòng 119**.
- **Giải thích 5 bước**:
    1. **Bước 1 (View)**: Là một `Row` chứa các thẻ `Box` tương ứng với 3 chế độ Rank.
    2. **Bước 2 (Config)**: Màu sắc nút thay đổi dựa trên trạng thái `isSelected` (Dòng 424).
    3. **Bước 3 (Action)**: Khi click -> Gọi `onModeSelected(mode)`.
    4. **Bước 4 (ViewModel)**: Hàm `setSelectedMode` cập nhật biến `_selectedMode`.
    5. **Bước 5 (Recomposition)**: `tieredPokemon` trong ViewModel tính toán lại Map dữ liệu theo Mode mới -> UI vẽ lại toàn bộ bảng theo hạng mới.

### 2. Kỹ thuật Đồng bộ Bảng (TierRowGrid)
- **Vị trí**: Định nghĩa tại **Dòng 216**, gọi tại **Dòng 148, 156, 164**.
- **Giải thích sâu về Kỹ thuật**:
    - **Dòng 221 (`horizontalScroll`)**: Sử dụng biến `horizontalScrollState` dùng chung. Đây là câu trả lời cho câu hỏi "Làm sao vuốt 1 hàng mà các hàng khác trượt theo?".
    - **Dòng 221 (`IntrinsicSize.Min`)**: Ép chiều cao cột Nhãn (Dòng 231) phải khớp với nội dung Pokemon bên phải.
    - **Dòng 253 (`chunked(3)`)**: Chia Pokemon vào lưới 3 cột. Nếu hàng cuối thiếu, logic chèn ô trống (Dòng 270) sẽ giữ cho layout không bị vỡ.

### 3. Hiển thị Pokemon (PokemonFrame)
- **Vị trí**: Định nghĩa tại **Dòng 281**.
- **Giải thích 5 bước**:
    1. **Bước 1 (View)**: Một thẻ gồm `Box` (ảnh) và `Text` (tên).
    2. **Bước 2 (Config)**: Có các nhãn Hệ (Type) ở góc trên (Dòng 306) và nhãn Variant (V) nếu là biến thể (Dòng 325).
    3. **Bước 3 (Action)**: `clickable` tại **Dòng 293** điều hướng sang màn hình Chi tiết.
    4. **Bước 4 (ViewModel)**: Gọi hàm lấy chi tiết nếu cần.
    5. **Bước 5 (Data)**: Dùng ảnh từ URL thông qua thư viện Coil (`AsyncImage` - Dòng 319).

---

## PHẦN 3: LUỒNG DỮ LIỆU MVVM (DATA FLOW)

### Luồng xử lý "Tìm kiếm Pokemon trong bảng"
1. **Người dùng gõ chữ**: `OutlinedTextField` (Dòng 126) nhận ký tự.
2. **Action**: Gọi `viewModel.setSearchQuery(it)`.
3. **ViewModel (Xử lý nặng)**: Tại `PokemonViewModel.kt` (Dòng 82), hàm `tieredPokemon` thực hiện:
    - `debounce(300)`: Đợi 300ms (để tránh giật lag khi gõ nhanh).
    - `withContext(Dispatchers.Default)`: Chạy thuật toán lọc và nhóm (groupBy) trên luồng phụ.
4. **Kết quả**: Map `tieredPokemon` được cập nhật.
5. **Recomposition**: UI lắng nghe qua `collectAsState` (Dòng 74) -> Chỉ những Pokemon khớp từ khóa mới được hiển thị.

---

## PHẦN 4: CÁC ĐIỂM TỐI ƯU CỰC CHI TIẾT (ADVANCED)

1. **Sử dụng `key` (Dòng 265)**: `key(pokemon.id)` báo cho Compose biết ID của Pokemon đó. Khi lọc, thay vì xóa đi vẽ lại, Compose chỉ cần di chuyển vị trí của ô đó trên màn hình (Hiệu năng cực cao).
2. **Tối ưu RAM (Dòng 284)**: Dùng `RGB_565` trong `ImageRequest`. Đây là kỹ thuật tiết kiệm 50% bộ nhớ đệm hình ảnh, giúp App không bị treo khi cuộn bảng xếp hạng dài.
3. **Tính toán thông minh (derivedStateOf - Dòng 86)**: Chỉ hiện nút "Lên đầu trang" khi thực sự cần thiết (cuộn qua item 5), giảm thiểu việc tính toán lại UI khi đang cuộn (Scroll performance).

---

### GHI CHÚ CHO BẠN KHI TRẢ LỜI:
- Luôn nhắc đến **"State quản lý UI"**: Dữ liệu đổi -> State đổi -> UI vẽ lại.
- Luôn nhắc đến **"Tối ưu luồng"**: Tác vụ nặng làm ở luồng phụ (Default), UI làm ở luồng chính (Main).
- Luôn nhắc đến **"Trạng thái ghi nhớ"**: Dùng `remember` để không bị mất dữ liệu khi xoay máy hoặc Recompose.
