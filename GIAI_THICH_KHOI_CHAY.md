# GIẢI THÍCH CƠ CHẾ KHỞI CHẠY (STARTUP & APPLICATION)

Tài liệu này giải thích cách ứng dụng thiết lập môi trường, cấu hình bộ nhớ đệm hình ảnh và điều hướng trang đầu tiên.

## 1. Thành phần chính
- **PokedexApplication.kt**: Khởi tạo cấu hình toàn cục (Coil, Translator).
- **MainActivity.kt**: Entry point của UI, nơi thiết lập `Scaffold` và `NavHost`.

## 2. Luồng vận hành (5 bước)

### Bước 1: Khởi tạo Application (PokedexApplication)
- **Mục đích**: Chạy ngay khi App được mở (trước cả Activity).
- **Dòng 11-30**: Cấu hình `ImageLoader` cho thư viện Coil.
    - **Memory Cache (25%)**: Sử dụng 25% bộ nhớ RAM còn lại để lưu ảnh.
    - **Disk Cache (2%)**: Lưu tối đa 2% dung lượng bộ nhớ máy để dùng khi ngoại tuyến.
- **Dòng 35**: Khởi tạo `TranslatorManager` để sẵn sàng dịch dữ liệu từ tiếng Anh sang tiếng Việt.

### Bước 2: Khởi tạo Activity (MainActivity)
- **Mục đích**: Gắn giao diện Compose vào hệ điều hành.
- **Dòng 18-20**: `enableEdgeToEdge()` giúp giao diện hiển thị tràn viền (dưới thanh trạng thái).
- **Dòng 25**: Gọi `MainScreen()`, đây là "khung xương" của toàn bộ App.

### Bước 3: Thiết lập điều hướng (NavHost)
- **Mục đích**: Xác định trang nào sẽ hiện ra đầu tiên.
- **NavGraph.kt (Dòng 25)**: Thiết lập `startDestination = "pokemon_list"`.
- App sẽ tự động tìm đến Composable `PokemonListScreen` để hiển thị danh sách Pokemon.

### Bước 4: Khởi tạo ViewModel & Data
- Khi `PokemonListScreen` được gọi, `PokemonViewModel` được khởi tạo thông qua `hiltViewModel()`.
- ViewModel gọi Repository để kiểm tra Database. Nếu trống, nó sẽ kích hoạt `fetchAndCachePokemon` để tải dữ liệu từ API.

### Bước 5: Hoàn tất Render
- Dữ liệu trả về -> Trạng thái UI thay đổi -> Compose vẽ lại giao diện hoàn chỉnh cho người dùng.

---
## 3. Câu hỏi bảo vệ trọng tâm
- **Tại sao cấu hình Coil trong Application?**
  => Để đảm bảo bộ nhớ đệm (Cache) được dùng chung cho mọi màn hình, tránh việc mỗi màn hình lại tải ảnh lại từ đầu.
- **`enableEdgeToEdge` có tác dụng gì?**
  => Giúp trải nghiệm người dùng tốt hơn, giao diện không bị các vạch đen của hệ thống cắt ngang.
- **Làm sao App biết hiện màn hình Danh sách đầu tiên?**
  => Nhờ tham số `startDestination` trong `NavHost`.