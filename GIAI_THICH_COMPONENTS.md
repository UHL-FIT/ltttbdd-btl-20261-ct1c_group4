# GIẢI THÍCH CÁC THÀNH PHẦN DÙNG CHUNG (SHARED COMPONENTS)

Tài liệu này giải thích các "viên gạch" UI (Custom Components) được tái sử dụng trong toàn bộ ứng dụng.

## 1. PokemonCard (Thành phần quan trọng nhất)
- **Vị trí**: Xuất hiện ở trang Danh sách, trang Tìm kiếm và trang Chọn đội hình.
- **Tính năng**: 
    - Hiển thị ảnh Pokemon bằng Coil.
    - Hiển thị tên và ID định dạng `#XXX`.
    - Màu nền thay đổi theo hệ của Pokemon.

## 2. TypeBadge (Huy hiệu hệ)
- **Mục đích**: Hiển thị tên hệ (Lửa, Nước...) với màu sắc đặc trưng.
- **Logic**: Tự động gọi `TranslatorManager` để dịch tên hệ sang tiếng Việt.

## 3. SearchBar (Thanh tìm kiếm)
- **Thiết kế**: Bo tròn, có icon kính lúp.
- **Tính năng**: Sử dụng kỹ thuật **Debounce** (chỉ tìm kiếm sau khi người dùng ngừng gõ 300ms) để tiết kiệm tài nguyên API/Database.

## 4. Luồng hiển thị Component (5 bước)
1.  **View**: Gọi Composable `PokemonCard(pokemon)`.
2.  **UI Config**: Component nhận dữ liệu từ tham số truyền vào.
3.  **Action**: Nếu nhấn vào Card -> Kích hoạt callback `onClick`.
4.  **ViewModel**: Navigation thực hiện chuyển trang dựa trên ID của Pokemon đó.
5.  **Recomposition**: Trạng thái "đã chọn" (trong trang Build) được cập nhật -> Card đổi viền sang màu nổi bật.

## 5. Câu hỏi bảo vệ trọng tâm
- **Tại sao phải tách ra các Component nhỏ?**
  => Để code dễ đọc, dễ bảo trì và quan trọng nhất là **tái sử dụng**. Thay đổi một chỗ (ví dụ đổi cỡ chữ tên Pokemon) sẽ cập nhật cho toàn App.
- **Làm sao để một Card biết đổi màu theo hệ?**
  => Sử dụng một hàm map (Helper) nhận vào tên hệ (String) và trả về đối tượng `Color` tương ứng.
- **Làm sao xử lý khi ảnh Pokemon tải bị lỗi?**
  => Trong Coil, sử dụng tham số `error(R.drawable.placeholder)` để hiện ảnh mặc định nếu không có mạng.