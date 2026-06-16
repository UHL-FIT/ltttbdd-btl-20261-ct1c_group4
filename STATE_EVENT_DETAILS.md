# Chi tiết Luồng State & Event trong App Pokedex

Tài liệu này giải thích cách thức hoạt động của Unidirectional Data Flow (UDF) trong ứng dụng thông qua một ví dụ cụ thể về sự kiện lọc Pokemon theo Hệ (Type).

---

## 1. Câu hỏi tình huống
**Tình huống:** Người dùng đang ở màn hình danh sách Pokemon và nhấn vào nút lọc hệ "Fire". Điều gì xảy ra từ lúc nhấn nút cho đến khi danh sách được cập nhật?

---

## 2. Luồng xử lý (Step-by-Step)

### Bước 1: Truyền sự kiện (Event) từ UI đến ViewModel
Trong Composable `DummyFilters`, mỗi nút hệ là một `Box` có sự kiện `clickable`:

```kotlin
// UI Layer (PokemonScreen.kt)
@Composable
fun DummyFilters(selectedType: String, onTypeSelected: (String) -> Unit) {
    // ...
    Box(
        modifier = Modifier.clickable { 
            onTypeSelected(name) // 1. Lambda được gọi khi người dùng click
        }
    ) { /* ... */ }
}
```

Tại `PokemonScreen`, lambda này được kết nối trực tiếp với ViewModel:
```kotlin
// PokemonScreen.kt
DummyFilters(selectedType) { type -> 
    viewModel.setSelectedType(type) // 2. Gọi hàm xử lý trong ViewModel
}
```

### Bước 2: Thay đổi trạng thái (State) trong ViewModel
ViewModel nhận sự kiện và cập nhật `MutableStateFlow` nội bộ:

```kotlin
// ViewModel Layer (PokemonViewModel.kt)
private val _selectedType = MutableStateFlow("All")
val selectedType = _selectedType.asStateFlow()

fun setSelectedType(type: String) {
    _selectedType.value = type // 3. Trạng thái mới được ghi vào StateFlow
}
```

### Bước 3: Phản ứng của luồng dữ liệu (Data Stream)
Ứng dụng sử dụng toán tử `combine` để tự động tính toán lại danh sách khi có bất kỳ tiêu chí nào thay đổi (Query, Type, Sort):

```kotlin
// PokemonViewModel.kt
val pagedPokemonList: Flow<PagingData<PokemonSummary>> = combine(
    _searchQuery.debounce(300),
    _selectedType, // 4. Khi _selectedType thay đổi, khối combine này chạy lại
    _sortType,
    _sortOrder
) { query, type, sType, sOrder ->
    // 5. Yêu cầu Repository lấy dữ liệu mới từ Database dựa trên filter mới
    repository.getPokemonPaging(query, type, sType.name, sOrder.name)
}.flatMapLatest { it }.cachedIn(viewModelScope)
```

### Bước 4: UI phản ứng (Recomposition)
Màn hình `PokemonScreen` đăng ký lắng nghe (collect) các State này:

```kotlin
// UI Layer (PokemonScreen.kt)
val selectedType by viewModel.selectedType.collectAsState() // Observe state đơn lẻ cho UI filter
val pagedPokemon = viewModel.pagedPokemonList.collectAsLazyPagingItems() // Observe danh sách paged

// 6. Khi pagedPokemon có dữ liệu mới từ Flow:
// - Compose phát hiện sự thay đổi trạng thái.
// - Kích hoạt quá trình Recomposition (Vẽ lại).
// - LazyColumn chỉ vẽ lại các item Pokemon thỏa mãn điều kiện Hệ "Fire".
```

---

## 3. Tóm tắt mô hình hoạt động

| Thành phần | Vai trò | Hành động trong ví dụ |
| :--- | :--- | :--- |
| **Event** (Sự kiện) | UI -> ViewModel | `onTypeSelected("Fire")` |
| **Action** (Hành động) | Xử lý logic | `viewModel.setSelectedType(type)` |
| **State** (Trạng thái) | Nguồn sự thật (Single Source of Truth) | `_selectedType` cập nhật giá trị mới |
| **Effect** (Hệ quả) | Cập nhật luồng dữ liệu | `combine` tạo ra danh sách Pokemon đã lọc |
| **Recomposition** | Cập nhật giao diện | Danh sách Pokemon hệ "Fire" hiển thị trên màn hình |

---

## 4. Tại sao làm như vậy?
1.  **Tách biệt logic (Separation of Concerns):** UI không cần biết dữ liệu được lọc như thế nào, nó chỉ gửi "ý định" (intent) và chờ hiển thị "trạng thái" (state).
2.  **Dễ kiểm thử (Testability):** Có thể viết Unit Test cho `PokemonViewModel` để kiểm tra xem khi gọi `setSelectedType` thì danh sách trả về có đúng không mà không cần chạy giao diện.
3.  **Đồng bộ dữ liệu:** Nếu có hai nơi cùng hiển thị Hệ đang chọn, cả hai sẽ cùng cập nhật vì chúng cùng lắng nghe một nguồn `selectedType` duy nhất.
