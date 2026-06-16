# GIẢI MÃ CHI TIẾT TỪNG DÒNG CODE: STARTUP & APPLICATION

Tài liệu này giải thích chi tiết ý nghĩa kỹ thuật của từng dòng code trong quá trình khởi tạo ứng dụng.

### 1. Tệp `PokedexApplication.kt` (Cấu hình hệ thống)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|:---|
| 11 | `class PokedexApplication : Application(), ImageLoaderFactory` | Khai báo lớp ứng dụng chính. Triển khai `ImageLoaderFactory` để tùy biến trình tải ảnh Coil. |
| 13 | `override fun newImageLoader(): ImageLoader` | Khởi tạo trình tải ảnh tùy chỉnh cho toàn bộ ứng dụng. |
| 14 | `return ImageLoader.Builder(this)` | Sử dụng Builder Pattern để thiết lập các thông số kỹ thuật cho thư viện Coil. |
| 17 | `MemoryCache.Builder(this)` | Khởi tạo cấu hình bộ nhớ đệm trên RAM (Memory Cache) giúp hiện ảnh ngay lập tức. |
| 18 | `.maxSizePercent(0.25)` | Dành 25% dung lượng RAM trống của thiết bị để lưu trữ ảnh Pokemon, đảm bảo hiệu năng cao. |
| 22 | `DiskCache.Builder()` | Khởi tạo cấu hình bộ nhớ đệm trên ổ cứng (Disk Cache) để xem ảnh khi không có mạng. |
| 23 | `.directory(this.cacheDir.resolve("image_cache"))` | Chỉ định thư mục lưu trữ ảnh tạm thời trên bộ nhớ máy. |
| 24 | `.maxSizePercent(0.02)` | Giới hạn dung lượng cache trên đĩa là 2% bộ nhớ máy để tối ưu không gian lưu trữ. |
| 28 | `.diskCachePolicy(CachePolicy.ENABLED)` | Bật chính sách cho phép lưu trữ và đọc ảnh từ bộ nhớ đĩa. |
| 29 | `.memoryCachePolicy(CachePolicy.ENABLED)` | Bật chính sách cho phép lưu trữ và đọc ảnh từ RAM. |
| 31 | `.logger(DebugLogger())` | Bật ghi log để theo dõi quá trình tải ảnh (hữu ích khi gỡ lỗi đường truyền). |
| 32 | `.respectCacheHeaders(false)` | **Kỹ thuật tối ưu**: Bỏ qua quy định của Server để ưu tiên dữ liệu trong máy, giúp tải ảnh cực nhanh. |

### 2. Tệp `MainActivity.kt` (Khởi tạo giao diện)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|:---|
| 13 | `class MainActivity : ComponentActivity()` | Lớp Activity chính, là nền tảng khởi chạy giao diện của App. |
| 16 | `override fun onCreate(...)` | Hàm chạy đầu tiên khi mở App, dùng để thiết lập giao diện và dữ liệu ban đầu. |
| 18 | `Log.d(TAG, "onCreate")` | Ghi log vòng đời (Lifecycle) để kiểm soát quá trình khởi động ứng dụng. |
| 19 | `setContent { ... }` | Hàm định nghĩa giao diện của màn hình bằng Jetpack Compose. |
| 21 | `val navController = rememberNavController()` | Tạo bộ điều khiển để quản lý việc chuyển đổi giữa các màn hình. |
| 22 | `MainAppLayout(navController)` | Gọi khung giao diện chính chứa thanh menu và vùng nội dung hiển thị. |
