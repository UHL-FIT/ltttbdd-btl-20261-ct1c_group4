# GIẢI MÃ CHI TIẾT CODE: VIEWMODELS & STATE MANAGEMENT

Tài liệu giải thích cách ViewModel quản lý trạng thái UI và xử lý luồng dữ liệu (Data Stream) phản ứng.

### 1. PokemonViewModel.kt (Quản lý danh sách & Phân trang)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 51 | `val pagedPokemonList = combine(...)` | Sử dụng toán tử **combine** để tự động tính toán lại danh sách hiển thị mỗi khi Search, Type hoặc Sort thay đổi. |
| 52 | `_searchQuery.debounce(300)` | Kỹ thuật **Debouncing**: Chỉ kích hoạt tìm kiếm sau khi người dùng ngừng gõ 300ms, giúp giảm tải cho Database. |
| 58 | `flatMapLatest { it }` | Đảm bảo nếu người dùng thay đổi bộ lọc liên tục, chỉ kết quả của yêu cầu cuối cùng được hiển thị (hủy các luồng cũ). |
| 75 | `val tieredPokemon: StateFlow<...>` | Luồng dữ liệu phức tạp dùng cho màn hình Xếp hạng (Tier List), kết hợp nhiều tiêu chí lọc cùng lúc. |
| 84 | `withContext(Dispatchers.Default)` | Đưa các tác vụ tính toán logic nặng (lọc, nhóm Pokemon) ra luồng phụ để không gây giật lag UI. |
| 89 | `.groupBy { p -> ... }` | Thuật toán gom nhóm đa cấp: Đầu tiên nhóm theo Tier (S, A, B), sau đó nhóm theo Role (Attacker, Tanker). |
| 125 | `startBackgroundSync()` | Cơ chế **Đồng bộ ngầm**: Tự động tải chi tiết các Pokemon còn thiếu khi máy đang rảnh (Idle). |
| 145 | `repository.getAllPokemonSummaryFromDb()` | Lắng nghe trực tiếp sự thay đổi từ Room Database. Dữ liệu trên màn hình sẽ tự cập nhật khi DB thay đổi. |
| 153 | `loadNextPage()` | Logic phân trang thủ công (Offset-based pagination) để kiểm soát chính xác lượng dữ liệu tải lên RAM. |

### 2. Thành phần kỹ thuật cốt lõi

| Khái niệm | Ứng dụng trong Code | Giải thích thực tế |
|:---:|:---|:---|
| **StateFlow** | `_searchQuery`, `_isLoading` | Giữ trạng thái hiện tại của UI. Khi xoay màn hình, dữ liệu không bị mất. |
| **viewModelScope** | `viewModelScope.launch { ... }` | Phạm vi vòng đời của Coroutine. Khi đóng màn hình, mọi tác vụ ngầm sẽ tự động dừng để tránh rò rỉ bộ nhớ. |
| **asStateFlow()** | `val searchQuery = _searchQuery.asStateFlow()` | Nguyên tắc **Encapsulation**: Chỉ ViewModel mới có quyền sửa dữ liệu, UI chỉ có quyền đọc. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng Debounce?** -> Để tránh việc ứng dụng thực hiện truy vấn Database quá nhiều lần khi người dùng đang gõ phím nhanh.
*   **Tại sao dùng `Dispatchers.Default` cho TieredPokemon?** -> Vì việc `groupBy` và `filter` trên một danh sách hơn 1000 Pokemon là tác vụ tốn CPU, cần chạy ở luồng tính toán riêng.
*   **Làm sao để UI biết khi nào đang tải dữ liệu?** -> Thông qua biến `_isLoading`. UI sẽ lắng nghe biến này để hiển thị vòng quay Loading.
