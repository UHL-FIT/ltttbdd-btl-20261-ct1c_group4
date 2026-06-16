# HƯỚNG DẪN CHI TIẾT CẤU TRÚC GIAO DIỆN (DÀNH CHO BÁO CÁO)

Tài liệu này hướng dẫn cách tìm và giải thích code giao diện cho 3 màn hình: Danh sách Pokemon, Chi tiết Pokemon và Vật phẩm.

---

## 1. MÀN HÌNH DANH SÁCH POKEMON (Pokemon List)
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Cách giải thích cấu trúc & Tùy chỉnh |
| :--- | :--- | :--- |
| **Tiêu đề (Header)** | 351 | Hàm `PokemonHeaderSection`: Chứa Breadcrumb (Home / List) và tiêu đề lớn 28.sp. |
| **Ô tìm kiếm** | 133 | `OutlinedTextField`: Tùy chỉnh màu viền tại `focusedBorderColor` (Dòng 141) và màu nền tại `focusedContainerColor` (Dòng 143). |
| **Bộ lọc hệ** | 296 | Hàm `DummyFilters`: Sử dụng `LazyRow` (Dòng 324) để cuộn ngang. Tùy chỉnh khoảng cách các nút bằng `Arrangement.spacedBy(8.dp)`. |
| **Đầu bảng (Header)** | 371 | Hàm `PokemonTableHeader`: Chứa các cột #, Name, Type, BST. Tùy chỉnh màu nền tại `background(Color(0xFF2D2D39))` (Dòng 381). |
| **Hàng Pokemon** | 209 | Hàm `PokemonRow`: Vẽ từng dòng Pokemon. Tùy chỉnh độ cao hàng tại `padding(vertical = 12.dp)` (Dòng 220). |
| **Ảnh Pokemon** | 229 | `AsyncImage`: Tùy chỉnh kích thước ảnh tại `size(40.dp)` (Dòng 226) và bo góc khung tại `RoundedCornerShape(4.dp)`. |
| **Huy hiệu hệ** | 408 | Hàm `TypeBadge`: Chứa bảng màu `when` (Dòng 409). Muốn đổi màu một hệ cụ thể thì sửa mã màu Hex ở đây. |
| **Bảng chỉ số mở rộng** | 280 | Hàm `BaseStatsGrid`: Chia 7 cột bằng `Row` và `Arrangement.SpaceBetween` (Dòng 291). |

---

## 2. MÀN HÌNH CHI TIẾT POKEMON (Pokemon Detail)
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonDetailScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Cách giải thích cấu trúc & Tùy chỉnh |
| :--- | :--- | :--- |
| **Khung ảnh chính (Banner)** | 149 | Hàm `MainBannerFromSummary`: Dùng `border` với `Brush.verticalGradient` (Dòng 155) để tạo viền màu chuyển sắc. |
| **Tên Pokemon** | 161 | `Text`: Chữ in hoa (`uppercase()`), cỡ 32.sp, độ đậm `FontWeight.Black`. |
| **Nút chọn Tab** | 221 | Hàm `DetailTabButtons`: Vẽ nút Profile. Tùy chỉnh màu khi được chọn tại `if (isSelected) color else Color.Gray` (Dòng 230). |
| **Tiêu đề mục** | 252 | Hàm `DetailSectionHeader`: Có một `Box` nhỏ làm icon vuông (Dòng 255). Thay đổi kích thước tại `size(12.dp)`. |
| **Bảng dữ liệu** | 271 | Hàm `PokedexDataTable`: Hiển thị Height, Weight... Tùy chỉnh khoảng cách giữa các hàng tại `padding(vertical = 8.dp)` (Dòng 331). |
| **Thanh chỉ số (Stats)** | 358 | Hàm `StatRow`: Vẽ thanh năng lượng. Lớp nền xám (Dòng 362) và lớp màu đè lên (Dòng 363) có độ dài dựa trên `value / 255f`. |
| **Sơ đồ tiến hóa** | 375 | Hàm `EvolutionSection`: Vẽ dạng cây. Tùy chỉnh màu mũi tên tại `tint = Color(0xFF00B0FF)` (Dòng 433). |
| **Bảng kỹ năng (Moves)** | 489 | Hàm `MovesTable`: Sử dụng vòng lặp `forEach` để vẽ danh sách các chiêu thức từ dữ liệu API. |

---

## 3. MÀN HÌNH DANH SÁCH VẬT PHẨM (Items)
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/ItemScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Cách giải thích cấu trúc & Tùy chỉnh |
| :--- | :--- | :--- |
| **Tiêu đề trang** | 315 | Hàm `ItemHeaderSection`: Chứa chữ "Item List" và mô tả ngắn. |
| **Nút lọc danh mục** | 286 | Hàm `ItemCategoryFilters`: Tùy chỉnh màu nút khi nhấn tại `if (isSelected) Color(0xFF00B0FF) else Color(0xFF252530)` (Dòng 296). |
| **Hàng vật phẩm** | 204 | Hàm `ItemRow`: Mỗi hàng có icon 32.dp (Dòng 223). Nhấn vào hàng sẽ đổi màu nền sang xám nhạt (Dòng 212). |
| **Mô tả hiệu ứng** | 248 | Chữ hiển thị công dụng vật phẩm. Tùy chỉnh khoảng cách dòng tại `lineHeight = 18.sp` (Dòng 268). |

---

## QUY TRÌNH 5 BƯỚC KHI GIẢI THÍCH MỘT PHẦN CODE BẤT KỲ
(Theo yêu cầu của ảnh Loại 2)

- **Bước 1 (View):** Chỉ ra thành phần đó là Composable nào (Button, Text, Box, Row, Column) và nó nằm ở hàm nào, file nào, dòng bao nhiêu.
- **Bước 2 (Cấu hình):** Giải thích cách trang trí giao diện bằng `Modifier`. Ví dụ: `Modifier.padding(16.dp)` để tạo khoảng cách, `Modifier.background()` để đổ màu.
- **Bước 3 (Chữ & Màu):** Chỉ ra nội dung chữ nằm ở `Text(...)` và màu sắc lấy từ hệ thống màu của App (Ví dụ: `Color(0xFF00B0FF)` cho màu xanh chính).
- **Bước 4 (Sắp xếp):** Giải thích bố cục. `Row` là xếp ngang, `Column` là xếp dọc.
- **Bước 5 (Trạng thái):** Giải thích các biến `remember { mutableStateOf(...) }` giúp giao diện thay đổi khi người dùng tương tác (Vd: Nhấn vào thì hiện thêm thông tin).
