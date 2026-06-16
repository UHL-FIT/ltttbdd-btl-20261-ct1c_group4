# Giải thích Kỹ thuật Chuyên sâu: Vòng đời & Quản lý Trạng thái (Lifecycle)

Tài liệu này phân tích cách ứng dụng kiểm soát dòng đời của dữ liệu và các tác vụ nền (Background Tasks).

### 1. Cơ chế Tự động hóa Dữ liệu (LaunchedEffect Deep Dive)
- **Vị trí**: `TierScreen.kt` -> **Dòng 76**.
- **Code**: `LaunchedEffect(pokemonList.size) { ... }`
- **Giải thích kỹ thuật**: 
    - **Tại sao dùng `pokemonList.size` làm Key?**: Khi ứng dụng khởi động, danh sách Pokemon ban đầu rỗng (size = 0). `LaunchedEffect` sẽ chạy lần đầu. Khi dữ liệu bắt đầu đổ về từ Database, `size` thay đổi -> nó kích hoạt lại logic kiểm tra.
    - **Cơ chế tải bù**: Nếu danh sách < 500 con, nó tự gọi `loadNextPage()`. Điều này đảm bảo người dùng luôn có đủ nội dung để xem bảng xếp hạng mà không cần thực hiện bất kỳ thao tác thủ công nào.

### 2. Quản lý Bộ nhớ tạm (Memory Retention)
- **Code**: `remember { ... }` và `rememberLazyListState()`.
- **Giải thích**: 
    - **Vấn đề**: Khi thiết bị xoay màn hình, Android theo mặc định sẽ hủy và tạo lại Activity.
    - **Giải pháp**: Các biến nằm trong `remember` được lưu trữ trong "Composition Slot Table". Nó giữ lại trạng thái cuộn của người dùng, từ khóa tìm kiếm và các thiết lập lọc, giúp trải nghiệm người dùng không bị gián đoạn.

### 3. Vòng đời Coroutines (Safe Execution)
- **Vị trí**: `PokemonViewModel.kt` -> **Dòng 142** (`loadNextPage`).
- **Kỹ thuật**: `viewModelScope.launch`.
- **Giải thích**: Các tác vụ mạng (Network) và cơ sở dữ liệu (Database) được gắn chặt vào vòng đời của ViewModel. Nếu người dùng thoát hẳn ứng dụng, `viewModelScope` sẽ tự động hủy toàn bộ các tiến trình đang chạy ngầm, ngăn chặn việc rò rỉ bộ nhớ (Memory Leak) và lãng phí băng thông.

### 4. Tính toán Trạng thái Phụ thuộc (Derived State)
- **Code**: `derivedStateOf { listState.firstVisibleItemIndex > 5 }` (**Dòng 86**).
- **Kỹ thuật**: Đây là cách tối ưu vòng đời render. Thay vì bắt UI vẽ lại mỗi khi người dùng cuộn (pixel-by-pixel), `derivedStateOf` chỉ phát đi tín hiệu khi điều kiện "Index > 5" thay đổi từ Sai sang Đúng (và ngược lại). Điều này giúp tiết kiệm tài nguyên CPU cực lớn.
