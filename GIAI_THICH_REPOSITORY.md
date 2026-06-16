# GIẢI THÍCH ĐIỀU PHỐI DỮ LIỆU (REPOSITORY)

Tài liệu này giải thích "Bộ não" quản lý dữ liệu của ứng dụng, nơi quyết định lấy dữ liệu từ mạng (API) hay từ máy (Local).

## 1. Vai trò của PokemonRepository
Repository đứng ở giữa ViewModel và Data Source:
- Giấu kín nguồn gốc dữ liệu (ViewModel không cần biết dữ liệu đến từ đâu).
- Thực hiện logic Offline-first (Ưu tiên hiện dữ liệu trong máy nếu có).

## 2. Luồng xử lý (5 bước)
1.  **Request**: ViewModel gọi `repository.getPokemonList()`.
2.  **Local Check**: Repository kiểm tra Database (Room).
3.  **Decision**:
    - Nếu DB có dữ liệu: Trả về UI ngay lập tức.
    - Nếu DB trống hoặc cần làm mới: Gọi API (Retrofit).
4.  **Syncing**: Khi API trả về, Repository lưu ngay vào Database để lần sau không cần tải lại.
5.  **Return**: Trả về dữ liệu cuối cùng cho ViewModel.

## 3. Background Sync (Đồng bộ ngầm)
- App có cơ chế `startBackgroundSync` để tải dần các thông tin chi tiết của Pokemon khi người dùng đang xem danh sách, giúp trang chi tiết mở ra nhanh hơn.

## 4. Câu hỏi bảo vệ trọng tâm
- **Tại sao cần Repository mà không gọi thẳng Dao hay Service trong ViewModel?**
  => Để code dễ kiểm soát. Nếu sau này ta đổi API khác, ta chỉ cần sửa ở Repository, không cần sửa ở tất cả các ViewModel.
- **Làm sao xử lý lỗi khi không có mạng?**
  => Repository sẽ bắt lỗi (try-catch) và trả về dữ liệu cũ từ Database kèm thông báo "Đang ngoại tuyến".
- **`Flow` được dùng như thế nào ở đây?**
  => Dữ liệu được trả về dưới dạng `Flow`. Khi Database thay đổi, Repository tự động đẩy dữ liệu mới lên UI mà không cần gọi lại hàm.