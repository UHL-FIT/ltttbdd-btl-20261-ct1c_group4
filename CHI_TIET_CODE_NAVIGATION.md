# GIẢI MÃ CHI TIẾT CODE: NAVIGATION (ĐIỀU HƯỚNG MÀN HÌNH)

Tài liệu giải thích cách ứng dụng chuyển đổi giữa các màn hình và quản lý luồng đi của người dùng.

### 1. NavGraph.kt (Sơ đồ điều hướng)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 16 | `NavHost(..., startDestination = "home_route")` | Định nghĩa điểm bắt đầu của ứng dụng là màn hình Trang chủ (Home). |
| 18 | `composable("home_route") { ... }` | Đăng ký đường dẫn "home_route". Khi điều hướng đến đây, hệ thống sẽ vẽ màn hình `HomeScreen`. |
| 23 | `route = "pokemon_detail/{pokemonId}"` | **Dynamic Routing**: Đường dẫn có chứa tham số. `{pokemonId}` sẽ thay đổi tùy thuộc vào Pokemon người dùng chọn. |
| 24 | `arguments = listOf(navArgument(...) )` | Khai báo tham số truyền vào: Định nghĩa `pokemonId` là kiểu **StringType** để trình điều hướng hiểu và xử lý. |
| 27 | `backStackEntry.arguments?.getString(...)` | Kỹ thuật lấy lại dữ liệu từ đường dẫn để hiển thị đúng Pokemon tương ứng trên màn hình chi tiết. |

### 2. MainAppLayout.kt (Giao diện khung & Drawer)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 45 | `val drawerItems = listOf(...)` | Danh sách các mục trong Menu bên trái (Drawer), giúp quản lý tập trung các đích đến trong ứng dụng. |
| 62 | `ModalNavigationDrawer(...)` | Thành phần Menu trượt: Cho phép người dùng chuyển nhanh giữa các tính năng chính từ bất kỳ đâu. |
| 79 | `launchSingleTop = true` | Tối ưu điều hướng: Nếu người dùng nhấn vào trang đang đứng, hệ thống sẽ không tạo thêm một bản sao mới của trang đó. |
| 104 | `TopAppBar(...)` | Thanh công cụ phía trên: Chứa nút mở Menu và tiêu đề ứng dụng, tạo sự nhất quán cho giao diện. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng `pokemonViewModel` chung trong NavGraph?** -> Để dữ liệu (như kết quả tìm kiếm) được giữ nguyên khi người dùng chuyển từ danh sách sang chi tiết rồi quay lại.
*   **SingleTop có tác dụng gì?** -> Ngăn chặn việc người dùng nhấn nút Menu nhiều lần tạo ra một "chồng" các màn hình giống hệt nhau, gây tốn RAM và lỗi nút quay lại.
*   **Làm sao để biết đang ở màn hình nào?** -> Sử dụng `navController.currentBackStackEntryAsState()`. Ứng dụng dùng nó để tô màu xanh cho mục đang chọn trong Menu.
