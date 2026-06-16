# GIẢI THÍCH CHI TIẾT TRANG GIỚI THIỆU (GUIDE SCREEN)

Tài liệu này giải thích các kỹ thuật hiển thị văn bản phức tạp và bảng dữ liệu trong `GuideScreen.kt`.

---

## 1. Thành phần chính
1. **Breadcrumbs (Dòng 39 - 63)**: Thanh điều hướng "Home > Giới thiệu về game".
2. **Nội dung hướng dẫn**: Sử dụng component `GuideSection` để tái sử dụng giao diện.
3. **Bảng tra cứu**: Bảng Tính cách (Nature) và Bảng Khắc chế hệ (Type Effectiveness).

---

## 2. Giải thích chi tiết theo 5 bước

### A. Kỹ thuật Định dạng văn bản (Rich Text)
*   **Hàm sử dụng**: `buildAnnotatedString` (Dòng 127, 247).
*   **Thành phần**: `withStyle(SpanStyle(fontWeight = FontWeight.Bold))`.
*   **Mục đích**: Hiển thị nhiều định dạng (in đậm, in thường, màu sắc khác nhau) trong cùng một dòng `Text`.
*   **Ví dụ**: "• **HP** - Đây là sinh lực...". Phần "HP" được bôi đậm, phần còn lại in thường.

### B. Bảng Tính cách (NatureTable - Dòng 357 - 425)
*   **UI**: `Box` kết hợp `horizontalScroll` để bảng có thể kéo ngang trên điện thoại màn hình nhỏ.
*   **Logic**: Sử dụng 2 vòng lặp `forEach` lồng nhau để vẽ lưới.
*   **Màu sắc**: Ô trên đường chéo (nơi rowIndex == colIndex) có màu xám để chỉ tính cách trung lập (không tăng giảm chỉ số).

### C. Bảng Khắc chế (TypeEffectivenessTable - Dòng 455 - 556)
*   **Dữ liệu**: Một `Map` chứa các quy tắc khắc chế (ví dụ: "FIRE" khắc "GRASS" x2).
*   **UI**: Tự động đổi màu ô dựa trên giá trị:
    *   **2**: Màu xanh (Hiệu quả cao).
    *   **½**: Màu đỏ (Ít hiệu quả).
    *   **0**: Màu đen (Không hiệu quả).
    *   **1**: Màu vàng kem (Bình thường).

### D. Điều hướng quay lại (Dòng 47 - 52)
*   **Action**: `navController.popBackStack()`.
*   **Mục đích**: Khi bấm vào chữ "Home" ở thanh Breadcrumbs, ứng dụng sẽ quay lại màn hình trước đó.

---

## 3. Các kỹ thuật Compose quan trọng
1.  **`horizontalScroll`**: Rất quan trọng cho các bảng dữ liệu nhiều cột, giúp giao diện không bị vỡ trên màn hình hẹp.
2.  **`spacedBy`**: Tạo khoảng cách đều giữa các phần tử mà không cần dùng `Spacer` thủ công nhiều lần.
3.  **Slot API**: Hàm `GuideSection` nhận một tham số `extraContent: @Composable () -> Unit`. Đây là kỹ thuật truyền cả một khối giao diện vào trong một hàm khác.

---

## 4. Câu hỏi phản biện thường gặp
*   **Q: Làm sao để xử lý dữ liệu bảng lớn như vậy mà không bị lag?**
    *   **A**: Dữ liệu bảng là tĩnh (Static Data), không thay đổi và không cần tải từ mạng. Compose chỉ vẽ lại khi thực sự cần thiết, nên hiệu năng rất cao.
*   **Q: Tại sao lại tách riêng `GuideSection`?**
    *   **A**: Để thống nhất giao diện: mọi phần (Pokemon, Tiến hóa, Vật phẩm) đều có tiêu đề có vạch xanh và đường kẻ ngang giống nhau.
