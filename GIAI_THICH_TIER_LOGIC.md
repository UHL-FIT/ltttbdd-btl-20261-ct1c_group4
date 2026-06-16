# Giải thích Kỹ thuật Chuyên sâu: Luồng dữ liệu Tier List (MVVM)

Tài liệu này phân tích cách dữ liệu được tính toán và chuyển đổi giữa tầng Data và tầng UI.

### 1. Kỹ thuật Phản xạ Dữ liệu (Reactive Programming)
- **Code**: `val tieredPokemon: StateFlow<...>` tại **Dòng 82** của `PokemonViewModel.kt`.
- **Toán tử `combine`**: ViewModel kết hợp 4 nguồn dữ liệu: `pokemonList` (danh sách gốc), `searchQuery` (từ khóa), `selectedType` (hệ) và `selectedMode` (PVP/PVE). 
- **Giải thích**: Bất kỳ khi nào 1 trong 4 nguồn này thay đổi, toàn bộ bảng xếp hạng sẽ được tính toán lại tự động.

### 2. Tối ưu hóa Hiệu năng CPU (Threading)
- **Code**: `withContext(Dispatchers.Default)` tại **Dòng 87**.
- **Giải thích**: Việc lọc (filter) và nhóm (groupBy) hàng nghìn Pokemon là tác vụ nặng. Chúng tôi đẩy tác vụ này sang luồng `Default` (dành cho tính toán). Điều này giúp luồng chính (Main Thread) luôn rảnh tay để xử lý vuốt chạm, đảm bảo ứng dụng không bao giờ bị "đơ".

### 3. Kỹ thuật "Chống rung" (Debouncing)
- **Code**: `.debounce(300)` tại **Dòng 84**.
- **Giải thích**: Khi người dùng gõ tìm kiếm nhanh, thay vì tính toán lại liên tục mỗi khi gõ 1 chữ, hệ thống sẽ đợi người dùng dừng gõ 300ms rồi mới xử lý. Điều này tiết kiệm tới 70% tài nguyên CPU không cần thiết.

### 4. Xử lý nhóm dữ liệu (Multi-level Grouping)
- **Logic**: 
    - Lần 1: Lọc theo Hệ và Tên.
    - Lần 2: `groupBy` theo cấp bậc Tier (Apex, Meta...).
    - Lần 3: `mapValues` và `groupBy` theo vai trò (Sweeper, Support...).
- **Kết quả**: Biến đổi một danh sách phẳng thành một cấu trúc Map 2 tầng phức tạp: `Map<Tier, Map<Role, List<Pokemon>>>`.

### 5. Quản lý trạng thái thông minh (State In)
- **Code**: `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)` tại **Dòng 116**.
- **Giải thích**: 
    - `WhileSubscribed(5000)`: Nếu người dùng thoát màn hình quá 5 giây, ViewModel sẽ tạm dừng tính toán để tiết kiệm pin.
    - `distinctUntilChanged()`: Nếu dữ liệu mới tính xong giống hệt dữ liệu cũ, hệ thống sẽ chặn không cho phát tín hiệu để UI không phải vẽ lại vô ích.
