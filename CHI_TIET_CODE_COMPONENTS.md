# GIẢI MÃ CHI TIẾT CODE: UI COMPONENTS (THÀNH PHẦN GIAO DIỆN)

Tài liệu giải thích cách xây dựng các thành phần giao diện tái sử dụng và hiển thị dữ liệu linh hoạt.

### 1. PokemonScreen.kt (Danh sách hiển thị)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 79 | `val showBackToTop by remember { ... }` | Sử dụng **derivedStateOf**: Chỉ tính toán lại trạng thái hiện nút "Lên đầu trang" khi vị trí cuộn thay đổi, giúp tối ưu hiệu năng. |
| 141 | `stickyHeader { ... }` | Thành phần tiêu đề bảng "dính": Luôn nằm ở trên cùng khi người dùng cuộn danh sách Pokemon bên dưới. |
| 157 | `pagedPokemon.itemCount` | Kết hợp với **Paging 3**: Chỉ vẽ các dòng Pokemon đang hiển thị trên màn hình, giúp app mượt mà dù danh sách có hàng ngàn mục. |
| 162 | `LaunchedEffect(Unit) { loadNextPage() }` | **Trigger tải thêm**: Tự động gọi lệnh tải dữ liệu từ Internet khi người dùng cuộn gần đến cuối danh sách hiện có. |
| 230 | `AsyncImage(...)` | Sử dụng thư viện **Coil**: Tự động tải ảnh từ URL, có cơ chế bộ nhớ đệm (Cache) để không phải tải lại ảnh đã xem. |
| 263 | `AnimatedVisibility(visible = expanded)` | Hiệu ứng mở rộng: Khi nhấn vào một dòng, bảng chỉ số chi tiết sẽ hiện ra với hiệu ứng chuyển động mượt mà. |

### 2. Các thành phần giao diện nhỏ (Widgets)

| Component | Vai trò kỹ thuật | Giải thích thực tế |
|:---:|:---|:---|
| `TypeBadge` | Nhãn hệ Pokemon | Tự động thay đổi màu sắc nền dựa trên hệ (VD: Grass màu xanh lá, Fire màu đỏ) để người dùng dễ nhận diện. |
| `BaseStatsGrid` | Lưới chỉ số | Hiển thị 6 chỉ số cơ bản một cách gọn gàng, tô đậm chỉ số tổng (BST) để làm nổi bật sức mạnh Pokemon. |
| `DummyFilters` | Thanh lọc hệ | Sử dụng `LazyRow` để tạo thanh trượt ngang chứa các hệ Pokemon, giúp tiết kiệm không gian màn hình. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng `LazyColumn` thay vì `Column`?** -> `LazyColumn` chỉ render các item nhìn thấy trên màn hình. Nếu dùng `Column` cho 1000 Pokemon, app sẽ bị tràn bộ nhớ và treo ngay lập tức.
*   **Lợi ích của `rememberLazyListState`?** -> Giúp ghi nhớ vị trí cuộn của người dùng. Khi họ quay lại từ màn hình chi tiết, danh sách vẫn nằm đúng chỗ cũ.
*   **Tại sao cần `crossfade(true)` trong Coil?** -> Tạo hiệu ứng hiện ảnh từ từ khi tải xong, tránh việc ảnh hiện ra đột ngột gây khó chịu cho mắt.
