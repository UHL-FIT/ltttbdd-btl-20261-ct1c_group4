# GIẢI THÍCH CHI TIẾT TRANG CHỦ (HOME SCREEN)

Tài liệu này giải thích chi tiết cấu trúc code và logic của file `HomeScreen.kt`.

---

## 1. Cấu trúc tổng thể
Trang chủ được chia thành 3 phần chính:
1. **Header**: Lời chào và tiêu đề ứng dụng.
2. **Nội dung giới thiệu**: Đoạn văn bản chào mừng nằm trong khung xám.
3. **Danh sách Shortcuts**: Các thẻ điều hướng nhanh đến các tính năng khác.

---

## 2. Giải thích chi tiết theo 5 bước

### A. Khai báo dữ liệu Shortcut (Dòng 47 - 53)
*   **Thành phần**: Biến `shortcuts` kiểu `List<ShortcutItem>`.
*   **Logic**: Chứa thông tin về Tên hiển thị, Hình ảnh (từ `R.drawable`) và Route (đường dẫn điều hướng).
*   **Mục đích**: Để khi muốn thêm một nút mới, chỉ cần thêm 1 dòng vào danh sách này mà không cần sửa code giao diện.

### B. Header Section (Dòng 155 - 183)
*   **UI**: Sử dụng `Row` để xếp Icon và Text nằm ngang.
*   **Modifier**: `size(50.dp)` và `RoundedCornerShape(8.dp)` tạo khung hình vuông bo góc cho biểu tượng Home.
*   **Dữ liệu**: Hiển thị text "Pokedex Database" và "Chào mừng bạn trở lại!".

### C. Khung văn bản chào mừng (Dòng 83 - 104)
*   **UI**: `Box` bao quanh `Text`.
*   **Modifier**: `background(Color(0xFF32323E))` tạo nền xám đậm, `padding` tạo khoảng trống giúp chữ không sát mép.
*   **Nội dung**: Giới thiệu sơ lược về ứng dụng.

### D. Danh sách các nút bấm (Dòng 128 - 138)
*   **UI**: `Column` với `Arrangement.spacedBy(12.dp)` giúp các nút cách đều nhau 12 đơn vị.
*   **Hàm gọi (Action)**: `shortcuts.forEach` duyệt qua danh sách dữ liệu và tạo ra các `ShortcutCard`.
*   **Logic**: Khi bấm vào (`onClick`), hàm `onShortcutClick(shortcut.route)` sẽ được gọi để chuyển màn hình.

---

## 3. Các kỹ thuật Compose quan trọng
1.  **`verticalScroll(rememberScrollState())`**: Cho phép toàn bộ trang có thể cuộn lên xuống nếu nội dung dài hơn màn hình.
2.  **`HorizontalDivider`**: Vẽ đường kẻ ngang màu xanh dương (0xFF00B0FF) để phân tách các phần, tạo điểm nhấn thị giác.
3.  **`Modifier.weight()`** (nếu có): Thường dùng trong `Row` để chia tỉ lệ không gian.

---

## 4. Câu hỏi phản biện thường gặp
*   **Q: Tại sao không dùng LazyColumn cho Shortcuts?**
    *   **A**: Vì số lượng shortcut ít (5 mục) và cố định, dùng `Column` kết hợp `verticalScroll` đơn giản hơn và đủ hiệu năng.
*   **Q: Màu sắc được quản lý thế nào?**
    *   **A**: Sử dụng mã màu Hex trực tiếp (ví dụ: `0xFF1E1E26`) để đảm bảo giao diện luôn đúng ý đồ thiết kế "Dark Mode" mà không phụ thuộc vào cấu hình hệ thống.
