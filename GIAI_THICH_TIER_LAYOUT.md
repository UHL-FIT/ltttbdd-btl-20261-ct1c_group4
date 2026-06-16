# Giải thích Kỹ thuật: Cấu trúc Giao diện Bảng xếp hạng

Tài liệu này giải thích cấu trúc mã nguồn của màn hình xếp hạng dựa trên quy trình 5 bước để trả lời giảng viên.

### 1. Vị trí File
- **Đường dẫn**: `app/src/main/java/com/example/pokedex/ui/screens/TierScreen.kt`

### 2. Chi tiết Quy trình giải thích UI

#### Bước 1: View (Định nghĩa màn hình)
- **Code**: `fun TierScreen(...)` tại **Dòng 68**.
- **Giải thích**: Đây là Composable chính quản lý giao diện bảng xếp hạng. Nó sử dụng `Scaffold` (Dòng 104) để tạo khung nền và `LazyColumn` (Dòng 111) để hiển thị danh sách các nhóm Tier (Apex, Meta, Others).

#### Bước 2: Cấu hình giao diện (UI Config)
- **Đồng bộ cuộn ngang**: `val horizontalScrollState = rememberScrollState()` tại **Dòng 100**. 
- **Giải thích**: Biến này được truyền vào tất cả các `TierRowGrid` (Dòng 227). Nhờ dùng chung một `state`, khi người dùng vuốt ngang ở một hàng, tất cả các hàng khác sẽ trượt theo đồng bộ, giúp các cột (Sweeper, Support, Wall...) luôn thẳng hàng.
- **Phân chia lưới (Grid)**: `val rows = pokemonInRole.chunked(3)` tại **Dòng 253**. Code này chia danh sách Pokemon thành các hàng nhỏ (tối đa 3 con/hàng) để tạo giao diện bảng cân đối.
- **Thẻ Pokemon**: `PokemonFrame` tại **Dòng 281** định nghĩa cách hiển thị ảnh, hệ và tên rút gọn của từng Pokemon trong bảng.
