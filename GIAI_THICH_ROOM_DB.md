# GIẢI THÍCH CƠ SỞ DỮ LIỆU NỘI BỘ (ROOM DATABASE)

Tài liệu này giải thích cách ứng dụng lưu trữ dữ liệu bền vững trên điện thoại để dùng khi không có mạng.

## 1. Các thành phần của Room
- **Entity**: Khai báo cấu trúc bảng (ví dụ: bảng `pokemon_table`).
- **DAO (Data Access Object)**: Các câu lệnh SQL (Query, Insert, Delete).
- **Database**: Lớp quản lý chính, thực hiện kết nối.

## 2. Các truy vấn quan trọng (DAO)
- **@Query**: Tìm kiếm Pokemon theo tên hoặc ID (Dùng toán tử `LIKE` trong SQL).
- **@Insert(onConflict = REPLACE)**: Lưu Pokemon mới. Nếu trùng ID thì ghi đè dữ liệu mới nhất.
- **PagingSource**: Tích hợp với thư viện Paging 3 để load dữ liệu theo trang (15-20 con một lần) giúp App không bị lag khi có hàng nghìn Pokemon.

## 3. Luồng vận hành (5 bước)
1.  **View**: Người dùng cuộn xuống cuối danh sách.
2.  **Paging Logic**: Kích hoạt yêu cầu lấy thêm dữ liệu.
3.  **DAO**: Thực hiện câu lệnh `SELECT * FROM pokemon LIMIT 20 OFFSET ...`.
4.  **Database**: Trả về danh sách Pokemon từ bộ nhớ máy.
5.  **Recomposition**: UI vẽ thêm 20 thẻ Pokemon mới vào danh sách.

## 4. Câu hỏi bảo vệ trọng tâm
- **Tại sao dùng Room thay vì SQLite thuần?**
  => Room giúp kiểm tra lỗi câu lệnh SQL ngay lúc biên dịch (Compile-time), tránh lỗi crash App khi chạy. Nó cũng tích hợp cực tốt với Flow và LiveData.
- **`onConflict = REPLACE` có tác dụng gì?**
  => Đảm bảo dữ liệu không bị trùng lặp. Nếu có sự thay đổi về chỉ số Pokemon từ API, nó sẽ cập nhật bản mới nhất vào máy.
- **Database Migration là gì?**
  => Là khi ta muốn thêm một cột mới vào bảng (ví dụ thêm cột "Favorite"). Ta phải tăng version và viết code chuyển đổi để không làm mất dữ liệu cũ của người dùng.