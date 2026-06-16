# GIẢI THÍCH ĐIỀU HƯỚNG (NAVIGATION)

Tài liệu này giải thích cách người dùng di chuyển giữa các màn hình trong App.

## 1. Cấu trúc NavGraph
Sử dụng thư viện Jetpack Compose Navigation.
- **Route**: Tên định danh của màn hình (ví dụ: `"pokemon_list"`, `"pokemon_detail/{id}"`).
- **Arguments**: Tham số truyền qua lại (ví dụ: truyền `id` khi bấm vào một con Pokemon).

## 2. Luồng di chuyển (5 bước)
1.  **View (Màn hình A)**: Người dùng nhấn vào một Pokemon.
2.  **Action**: Gọi `navController.navigate("pokemon_detail/$id")`.
3.  **Route Matching**: NavHost tìm trong bản đồ điều hướng xem Route nào khớp.
4.  **ViewModel Initial**: Màn hình B (`PokemonDetailScreen`) được khởi tạo, lấy `id` từ Arguments để tải dữ liệu.
5.  **Recomposition**: Màn hình B hiện lên với dữ liệu đúng của Pokemon đã chọn.

## 3. Quản lý trạng thái điều hướng
- **Bottom Navigation**: Thanh menu dưới cùng cho phép chuyển nhanh giữa Danh sách, Xếp hạng và Đội hình.
- **Backstack**: App tự động lưu lịch sử. Nhấn nút "Back" sẽ quay lại màn hình trước đó mà không bị tải lại dữ liệu (nhờ Cache của ViewModel).

## 4. Câu hỏi bảo vệ trọng tâm
- **Làm sao để truyền dữ liệu phức tạp (như một Object) giữa các màn hình?**
  => Cách chuẩn là chỉ truyền `ID` (String/Int). Sau đó màn hình đích sẽ dùng ID đó để lấy dữ liệu từ Repository/Database. Điều này giúp dữ liệu luôn mới nhất.
- **`navController` là gì?**
  => Là "người điều phối". Nó giữ trạng thái của các màn hình và thực hiện lệnh chuyển trang.
- **Tại sao dùng chuỗi String cho Route?**
  => Đây là quy chuẩn của Compose Navigation (giống như URL trên trình duyệt web). Nó giúp việc điều hướng trở nên tường minh.