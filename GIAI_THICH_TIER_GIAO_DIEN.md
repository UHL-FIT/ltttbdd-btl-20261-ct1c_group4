# Giải thích Kỹ thuật Chuyên sâu: Giao diện Bảng xếp hạng (Tier Screen)

Tài liệu này phân tích chi tiết từng dòng code giao diện, giúp bạn giải thích cách hệ thống xây dựng một bảng dữ liệu phức tạp.

### 1. Cấu trúc Tổng thể (The Backbone)
- **Vị trí**: `app/src/main/java/com/example/pokedex/ui/screens/TierScreen.kt`
- **Thành phần chính**: `fun TierScreen` (Dòng 68).
- **Kỹ thuật Scaffold**: Sử dụng `containerColor = Color(0xFF1E1E26)` (Dòng 105) để tạo giao diện Dark Mode đồng nhất.
- **Tối ưu cuộn**: `LazyColumn` (Dòng 111) kết hợp với `listState` giúp hệ thống chỉ render các phần tử đang hiển thị trên màn hình, giúp tiết kiệm RAM khi danh sách Pokemon lên tới hàng nghìn con.

### 2. Kỹ thuật Đồng bộ Cuộn ngang (Shared Scroll State)
- **Code**: `val horizontalScrollState = rememberScrollState()` (Dòng 100).
- **Giải thích sâu**: Đây là điểm mấu chốt. Thông thường mỗi hàng sẽ tự cuộn độc lập. Bằng cách khởi tạo một `ScrollState` duy nhất ở cấp màn hình và truyền nó vào từng `TierRowGrid` (Dòng 227), chúng ta ép buộc tất cả các hàng phải chia sẻ cùng một vị trí cuộn.
- **Ứng dụng**: Khi giảng viên hỏi "Làm sao để các cột thẳng hàng khi vuốt?", hãy chỉ vào dòng 100 và dòng 227.

### 3. Xử lý Layout linh hoạt (Grid & Intrinsic Size)
- **Code**: `height(IntrinsicSize.Min)` (Dòng 221).
- **Giải thích**: Đây là kỹ thuật đo lường trước kích thước. Nó giúp cột nhãn Tier bên trái (Dòng 231) tự động kéo dài chiều cao cho khớp với nội dung của lưới Pokemon bên phải, dù hàng đó có 1 hay 10 Pokemon.
- **Phân chia lưới**: `chunked(3)` (Dòng 253) biến danh sách phẳng thành một ma trận. Nếu hàng cuối chỉ có 1 Pokemon, logic tại **Dòng 270** sẽ tự động chèn các `Box` trống để giữ cho các ô không bị xê dịch.

### 4. Tối ưu hóa Hình ảnh (Image Optimization)
- **Vị trí**: `PokemonFrame` (Dòng 281).
- **Kỹ thuật**: `coil.request.ImageRequest` (Dòng 284).
- **Chi tiết**: 
    - `bitmapConfig(RGB_565)`: Giảm dung lượng bộ nhớ của mỗi ảnh đi 50% so với mặc định (ARGB_8888).
    - `size(100, 100)`: Resize ảnh ngay khi tải để không lãng phí tài nguyên GPU.
    - `diskCachePolicy`: Lưu ảnh vào bộ nhớ máy để lần sau mở lại không cần tải từ Internet.

### 5. Nút "Trở lại đầu trang" (Back to Top)
- **Code**: `derivedStateOf { listState.firstVisibleItemIndex > 5 }` (Dòng 86).
- **Giải thích**: Sử dụng `derivedStateOf` để tránh việc tính toán lại (Recomposition) quá nhiều lần khi người dùng cuộn. Nút FAB (Dòng 92) chỉ xuất hiện khi người dùng cuộn qua item thứ 5.
