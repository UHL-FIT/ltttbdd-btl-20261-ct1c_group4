# Giải thích Chi tiết Từng Dòng Code: Trang Xếp hạng & Vòng đời

Tài liệu này bóc tách toàn bộ các thành phần quan trọng trong file `TierScreen.kt` và luồng xử lý MVVM để trả lời các câu hỏi về "Luồng dữ liệu" và "Tùy chỉnh tính năng".

---

## 1. MÀN HÌNH CHÍNH (TierScreen.kt)

### A. Khai báo Trạng thái (State) và Vòng đời (Lifecycle)
*   **Dòng 70-74**: Lấy các State từ ViewModel bằng `collectAsState()`.
    *   *Ý nghĩa*: Chuyển đổi dữ liệu luồng (Flow) thành trạng thái Compose. Khi dữ liệu trong ViewModel thay đổi, UI sẽ tự động nhận biết.
*   **Dòng 76-80 (QUAN TRỌNG - Vòng đời)**:
    ```kotlin
    LaunchedEffect(pokemonList.size) {
        if (pokemonList.size < 500) { viewModel.loadNextPage() }
    }
    ```
    *   *Giải thích*: Đây là Side-effect quản lý vòng đời. Nó theo dõi số lượng Pokemon. Nếu dữ liệu chưa đủ (dưới 500), nó tự kích hoạt tải thêm. Điều này giúp bảng xếp hạng luôn đầy đủ thông tin ngay khi mở app.
*   **Dòng 100**: `val horizontalScrollState = rememberScrollState()`
    *   *Giải thích*: Tạo ra một "linh hồn" chung cho việc cuộn ngang. Dùng để đồng bộ hóa tất cả các hàng Pokemon.

### B. Cấu trúc Giao diện (The View)
*   **Dòng 111-120**: `LazyColumn` chứa danh sách.
    *   `TierHeaderSection`: Tiêu đề và giới thiệu.
    *   `TierAccordion`: Các mục hướng dẫn (Về bảng xếp hạng, Tiêu chuẩn...).
*   **Dòng 119**: `ModeSelectorGrid` (Nút chọn PVP/PVE).
    *   *Luồng chạy*: Khi click nút -> Gọi `viewModel.setSelectedMode(it)` -> Kích hoạt Bước 4 & 5 (ViewModel xử lý và Recompose).
*   **Dòng 126-140**: `OutlinedTextField` (Ô tìm kiếm).
    *   `value = searchQuery`: Hiển thị từ khóa hiện tại.
    *   `onValueChange`: Mỗi khi gõ phím, nó cập nhật ngay lập tức vào ViewModel.

---

## 2. THÀNH PHẦN BẢNG (TierRowGrid)

### A. Đồng bộ cuộn và Kích thước (Dòng 216 - 279)
*   **Dòng 221**: `Modifier.horizontalScroll(horizontalScrollState)`.
    *   *Giải thích*: Gán trạng thái cuộn chung. Đây là lý do tại sao vuốt 1 hàng thì các hàng khác chạy theo.
*   **Dòng 221**: `.height(IntrinsicSize.Min)`.
    *   *Giải thích*: Ép các cột (Role) phải cao bằng nhau trong cùng một hàng, tạo ra vẻ ngoài vuông vức của một bảng tính.

### B. Logic chia Lưới (Dòng 253 - 275)
*   **Dòng 253**: `val rows = pokemonInRole.chunked(3)`.
    *   *Giải thích*: Chia danh sách Pokemon của mỗi Role thành các nhóm 3 con.
*   **Dòng 270-272**: `repeat(3 - rowItems.size) { Box(modifier = Modifier.size(62.dp)) }`.
    *   *Giải thích*: Nếu hàng cuối chỉ có 1 hoặc 2 Pokemon, code này vẽ thêm các ô trống tàng hình để giữ cho các ô khác không bị lệch hàng.

---

## 3. LUỒNG XỬ LÝ DỮ LIỆU (ViewModel & Data)

### A. Phân loại Pokemon (PokemonViewModel.kt)
*   **Dòng 82-116**: Hàm `tieredPokemon`.
    *   **Bước 1**: Lọc Pokemon theo `searchQuery` và `selectedType`.
    *   **Bước 2**: Nhóm (`groupBy`) theo Tier (Apex, Meta...).
    *   **Bước 3**: Nhóm tiếp theo Role (Sweeper, Support...).
    *   *Kết quả*: Tạo ra cấu trúc Map phức tạp để UI chỉ việc hiển thị.

---

## 4. ÁP DỤNG QUY TRÌNH 5 BƯỚC (Ví dụ: Đổi chế độ PVP/PVE)

Khi giảng viên chỉ vào nút PVP/PVE và hỏi: **"Khi tôi click nút này, chương trình chạy thế nào?"**, bạn trả lời theo 5 bước:

1.  **Bước 1 - View**: Đây là thành phần `ModeSelectorGrid` định nghĩa trong file `TierScreen.kt` tại **Dòng 119**.
2.  **Bước 2 - UI Config**: Màu sắc của nút được cấu hình tại **Dòng 527** (sử dụng `Color(0xFF00B0FF)` khi được chọn).
3.  **Bước 3 - Action**: Khi click, sự kiện `onClick` gọi hàm `viewModel.setSelectedMode(it)` (**Dòng 531**).
4.  **Bước 4 - ViewModel**: Chạy tới hàm `setSelectedMode` trong `PokemonViewModel.kt`. Hàm này cập nhật `_selectedMode` (**Dòng 74**).
5.  **Bước 5 - Data & Recomposition**: 
    *   Biến `tieredPokemon` (Dòng 82) thấy mode thay đổi -> Tự động tính toán lại danh sách.
    *   Dữ liệu mới được phát ra qua `StateFlow`.
    *   UI nhận thấy dữ liệu mới -> Kích hoạt **Recomposition** để vẽ lại các thẻ Pokemon vào đúng Tier/Role của chế độ PVP/PVE mới.

---

## 5. CÁC CÂU HỎI VỀ VÒNG ĐỜI (Lifecycle)

*   **Câu hỏi**: Tại sao app không bị treo khi xử lý hàng ngàn Pokemon?
    *   *Trả lời*: Vì em dùng `withContext(Dispatchers.Default)` tại **Dòng 87** trong ViewModel. Đây là kỹ thuật chạy đa luồng, đẩy việc tính toán nặng ra khỏi luồng giao diện chính.
*   **Câu hỏi**: Làm sao để lưu vị trí cuộn khi người dùng quay lại trang này?
    *   *Trả lời*: Em dùng `rememberLazyListState()` tại **Dòng 82** và gán vào `LazyColumn`. Compose sẽ ghi nhớ vị trí này trong bộ nhớ tạm (Composition Table) suốt vòng đời của Composable.
