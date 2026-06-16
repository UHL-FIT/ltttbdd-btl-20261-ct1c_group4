# GIẢI THÍCH KẾT NỐI API (RETROFIT)

Tài liệu này giải thích cách ứng dụng giao tiếp với Server để lấy dữ liệu Pokemon.

## 1. Cấu hình Retrofit
- **Base URL**: `https://pokeapi.co/api/v2/` (Nguồn dữ liệu chính).
- **Converter (Gson)**: Tự động chuyển đổi chuỗi JSON nhận được từ Server thành các Object trong Kotlin.

## 2. Interface API
Định nghĩa các "cổng" giao tiếp:
- **@GET("pokemon")**: Lấy danh sách Pokemon kèm theo phân trang (`limit`, `offset`).
- **@GET("pokemon/{id}")**: Lấy thông tin chi tiết của một con cụ thể.

## 3. Luồng lấy dữ liệu (5 bước)
1.  **Repository**: Gọi hàm `apiService.getPokemon(id)`.
2.  **Retrofit**: Tạo ra một HTTP Request gửi lên Server.
3.  **Server**: Trả về dữ liệu dạng JSON.
4.  **Gson**: Phân tích (Parse) chuỗi JSON đó thành đối tượng `PokemonResponse`.
5.  **Repository**: Nhận đối tượng sạch sẽ và xử lý tiếp (lưu vào DB hoặc trả về UI).

## 4. Xử lý đa luồng (Coroutines)
- Tất cả các hàm gọi API đều có từ khóa `suspend`.
- Điều này giúp việc tải dữ liệu diễn ra ở **Background Thread (Dispatchers.IO)**, không làm đơ giao diện (Main Thread).

## 5. Câu hỏi bảo vệ trọng tâm
- **Tại sao không dùng thư viện Volley hay HttpURLConnection?**
  => Retrofit là thư viện tiêu chuẩn hiện nay, mạnh mẽ, dễ quản lý lỗi và hỗ trợ tốt cho Coroutines.
- **Làm sao để xem dữ liệu thô gửi về từ API?**
  => Sử dụng `OkHttpClient` với `HttpLoggingInterceptor`. Nó sẽ in toàn bộ nội dung gửi/nhận ra Logcat để kiểm tra.
- **Nếu API trả về lỗi 404 hoặc 500 thì sao?**
  => Ta dùng khối `try-catch` để bắt ngoại lệ và hiển thị thông báo "Máy chủ đang bảo trì" hoặc "Không tìm thấy dữ liệu".