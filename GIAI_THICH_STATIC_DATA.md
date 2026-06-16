# LẬP LUẬN VỀ SỬ DỤNG DỮ LIỆU TĨNH (STATIC DATA)

Tài liệu này cung cấp các lý do kỹ thuật để trả lời giảng viên khi được hỏi: "Tại sao không dùng Database (SQLite) hay API?"

---

## 1. Bản chất dữ liệu của Game Pokemon
Các quy tắc trong game Pokemon (như Bảng hệ, Tính cách, Chỉ số cơ bản của loài) là **Dữ liệu bất biến**:
*   Từ khi game ra mắt, các chỉ số này gần như không bao giờ thay đổi.
*   Không cần cập nhật liên tục từ Server.

---

## 2. Ưu điểm của việc dùng Map/List tĩnh (Hard-code thông minh)

### A. Hiệu năng truy xuất O(1)
*   Khi sử dụng `Map<String, Map<String, String>>` cho bảng khắc chế hệ:
    ```kotlin
    val effectiveness = mapOf("FIRE" to mapOf("GRASS" to "2"))
    val result = effectiveness["FIRE"]?["GRASS"] // Lấy ra ngay lập tức
    ```
*   **Lợi ích**: Không tốn thời gian mở kết nối Database, không tốn thời gian Query. Truy xuất cực nhanh.

### B. Tiết kiệm tài nguyên (Offline hoàn toàn)
*   Không cần quyền Internet.
*   Dung lượng ứng dụng nhỏ gọn vì không kèm theo các thư viện Database cồng kềnh (như Room hay SQLDelight).

### C. Đơn giản hóa kiến trúc code
*   Không cần xử lý các trạng thái: Đang tải (Loading), Lỗi mạng (Error), Dữ liệu rỗng (Empty).
*   Giúp nhóm tập trung hoàn toàn vào việc thiết kế giao diện (UI) và trải nghiệm người dùng (UX).

---

## 3. Cách trả lời câu hỏi phản biện

**Câu hỏi: Nếu sau này Pokemon có thêm hệ mới (như hệ Fairy ở Gen 6) thì sao?**
*   **Trả lời**: Với cấu trúc hiện tại, việc bảo trì cực kỳ đơn giản. Chỉ cần thêm một dòng vào `Map` dữ liệu hiện có. Vì đây là ứng dụng Bách khoa toàn thư cho một phiên bản game cụ thể, việc dữ liệu tĩnh giúp đảm bảo tính chính xác tuyệt đối mà không sợ lỗi đồng bộ server.

**Câu hỏi: Dữ liệu lớn có làm nặng RAM không?**
*   **Trả lời**: Các chuỗi văn bản và bảng tính cách chiếm dung lượng cực nhỏ (vài chục KB). So với việc lưu trữ hình ảnh, phần dữ liệu văn bản này không đáng kể đối với RAM của các thiết bị Android hiện đại.

---

## 4. Kết luận
Việc sử dụng dữ liệu tĩnh trong đồ án này là **lựa chọn tối ưu về mặt kỹ thuật** để đảm bảo ứng dụng chạy mượt mà, phản hồi tức thì và hoạt động ổn định trong mọi điều kiện.
