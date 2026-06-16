# GIẢI THÍCH LOGIC XỬ LÝ (VIEWMODELS)

Tài liệu này giải thích vai trò của ViewModel trong việc quản lý trạng thái và tách biệt logic khỏi UI.

## 1. Nguyên tắc MVVM
- **View**: Chỉ hiển thị giao diện, không tính toán.
- **ViewModel**: Nhận lệnh từ View, yêu cầu dữ liệu từ Repository, và giữ "Trạng thái UI" (UI State).
- **StateFlow/MutableStateFlow**: Công cụ để đẩy dữ liệu từ ViewModel sang View một cách tự động.

## 2. Các ViewModel tiêu biểu
### PokemonViewModel (Danh sách)
- Quản lý `pokemonList` dưới dạng Paging Data (tải tới đâu hiện tới đó).
- Xử lý tìm kiếm (`searchQuery`).

### BuildViewModel (Đội hình)
- Quản lý `selectedPokemon` (Tối đa 6 con).
- Xử lý logic lọc (`filteredPokemon`) dựa trên Pokemon đã chọn để tránh trùng lặp.

## 3. Luồng xử lý dữ liệu (5 bước)
1.  **View**: Gọi `viewModel.onEvent(intent)`.
2.  **ViewModel**: Kiểm tra điều kiện (ví dụ: Team chưa đủ 6 con).
3.  **Data Fetching**: ViewModel gọi `repository.getData()`.
4.  **State Update**: Khi có dữ liệu, cập nhật vào biến `_uiState.value = ...`.
5.  **View Observation**: Vì View đang "quan sát" (collect) State này, nó sẽ tự động vẽ lại (Recomposition) mà không cần load lại trang.

## 4. Câu hỏi bảo vệ trọng tâm
- **Tại sao không gọi API trực tiếp trong UI mà phải qua ViewModel?**
  => Để khi người dùng xoay điện thoại, dữ liệu không bị mất (ViewModel sống lâu hơn Activity). Ngoài ra giúp dễ viết unit test cho logic.
- **Lợi ích của `StateFlow` so với `LiveData`?**
  => `StateFlow` mạnh mẽ hơn khi kết hợp với Coroutines, hỗ trợ các toán tử như `map`, `filter`, `combine` để xử lý dữ liệu phức tạp.
- **`viewModelScope` dùng để làm gì?**
  => Để quản lý các tác vụ chạy ngầm. Khi màn hình bị đóng, các tác vụ này sẽ tự động bị hủy để tránh rò rỉ bộ nhớ (Memory Leak).