# GIẢI THÍCH BỘ TIỆN ÍCH (TRANSLATOR & UTILS)

Tài liệu này giải thích cách xử lý dịch thuật đa ngôn ngữ bằng AI và các hàm bổ trợ định dạng dữ liệu.

## 1. TranslatorManager (Xử lý dịch thuật)
Đây là phần quan trọng nhất giúp App có tiếng Việt mà không cần nhập thủ công.

### Luồng xử lý (5 bước):
1.  **UI Request**: UI gọi hàm `translateText(text)` khi cần hiển thị tên hệ hoặc mô tả Pokemon.
2.  **Kiểm tra Cache**: Sử dụng `ConcurrentHashMap` để kiểm tra xem từ này đã dịch chưa. Nếu có rồi thì trả về ngay (tiết kiệm tài nguyên).
3.  **Hàng chờ AI (Semaphore)**: Dòng 35 sử dụng `Semaphore(3)`. Điều này giới hạn chỉ cho phép tối đa 3 tiến trình dịch cùng lúc để tránh làm treo máy hoặc tràn bộ nhớ.
4.  **ML Kit Translation**: Sử dụng thư viện Google ML Kit để chuyển đổi Anh -> Việt.
5.  **Cập nhật Cache & Trả về**: Lưu kết quả mới vào Map và trả về cho UI thông qua `Flow` hoặc `State`.

## 2. Utils (Định dạng dữ liệu)
Các hàm nhỏ nhưng giúp code sạch hơn:
- **formatId(id)**: Chuyển số 1 thành `#001` (Dùng `String.format("%03d")`).
- **capitalize()**: Viết hoa chữ cái đầu cho tên Pokemon.
- **parseTypeColor**: Ánh xạ tên hệ (Fire, Water...) sang mã màu tương ứng (Đỏ, Xanh...).

## 3. Câu hỏi bảo vệ trọng tâm
- **Tại sao dùng Semaphore trong Translator?**
  => Google ML Kit tiêu tốn CPU/RAM rất lớn. Nếu dịch 100 Pokemon cùng lúc sẽ gây lag. Semaphore giới hạn "3 lượt" giúp App chạy mượt mà.
- **`ConcurrentHashMap` khác gì `HashMap` thông thường?**
  => Nó an toàn khi chạy đa luồng (Thread-safe). Vì việc dịch diễn ra ở luồng phụ (IO), việc ghi dữ liệu vào Map cần đảm bảo không bị xung đột.
- **Làm sao để dịch ngoại tuyến?**
  => Model ngôn ngữ được tải về máy một lần duy nhất lúc khởi động App thông qua `TranslatorOptions`.