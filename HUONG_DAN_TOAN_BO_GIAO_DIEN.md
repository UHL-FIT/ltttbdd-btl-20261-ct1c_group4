# HƯỚNG DẪN CHI TIẾT TOÀN BỘ GIAO DIỆN (UI ONLY)

Tài liệu này tập trung vào việc giải thích các thành phần giao diện (UI Element) và cách chúng được vẽ trên màn hình.

---

## 1. GIAO DIỆN TRANG DANH SÁCH POKEMON
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Đặc điểm giao diện (Modifier/Properties) |
| :--- | :--- | :--- |
| **Khung nền chính (Scaffold)** | 92 | Màu nền tối `Color(0xFF1E1E26)`. |
| **Nút cuộn lên đầu (FAB)** | 94 | Hình tròn (`CircleShape`), màu xanh dương `0xFF00B0FF`. |
| **Danh sách cuộn (LazyColumn)** | 111 | Tối ưu bộ nhớ, chỉ vẽ các phần tử đang hiện trên màn hình. |
| **Tiêu đề trang (Header)** | 351 | Gồm đường dẫn (Breadcrumb) và chữ "Pokemon List" cỡ 28.sp. |
| **Thanh tìm kiếm (Search Field)** | 133 | Có Icon lúp, bo góc 4.dp, viền đổi màu khi nhấn (Dòng 141). |
| **Bộ lọc hệ (Horizontal Filter)** | 296 | Danh sách cuộn ngang (`LazyRow`), mỗi nút có màu riêng theo hệ. |
| **Đầu bảng (Sticky Header)** | 155 | Luôn nằm trên cùng khi cuộn. Chia 4 cột: #, Name, Type, BST. |
| **Dòng Pokemon (Item Row)** | 209 | Chiều ngang đầy đủ, có hiệu ứng đổi màu nền khi nhấn mở rộng. |
| **Ảnh Pokemon nhỏ** | 229 | Nằm trong `Box` bo góc, có viền 1.dp màu xám. |
| **Huy hiệu hệ (Type Badge)** | 408 | Khung hình chữ nhật bo góc, màu nền lấy từ bảng màu hệ (Dòng 409). |
| **Chỉ số mở rộng (Stats Grid)** | 280 | Hiển thị 7 cột chỉ số (HP, ATK...) khi nhấn vào hàng. |

---

## 2. GIAO DIỆN TRANG CHI TIẾT POKEMON
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/PokemonDetailScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Đặc điểm giao diện (Modifier/Properties) |
| :--- | :--- | :--- |
| **Banner thông tin chính** | 149 | Có viền Gradient đổi màu từ `color1` sang `color2` (Dòng 155). |
| **Tên Pokemon lớn** | 161 | Chữ in hoa, cỡ 32.sp, độ đậm `Black` (Dày nhất). |
| **Ảnh Pokemon lớn** | 164 | Kích thước 200.dp, nằm chính giữa Banner. |
| **Nút chuyển Tab** | 221 | Hiển thị chữ "Profile" có gạch chân màu hệ bên dưới. |
| **Tiêu đề phân đoạn (Section)** | 252 | Có một ô vuông nhỏ màu hệ phía trước chữ tiêu đề. |
| **Bảng dữ liệu Pokedex** | 271 | Chia làm 2 cột: Nhãn (Gray) và Giá trị (White/Bold). |
| **Thanh chỉ số (Stat Row)** | 358 | Nhãn 100.dp, Số 40.dp, và Thanh Progress chiếm phần còn lại. |
| **Sơ đồ tiến hóa (Evolution)** | 375 | Sử dụng `Row` và `Column` lồng nhau để vẽ dạng cây (Tree). |
| **Nút tiến hóa (Node)** | 450 | Ảnh Pokemon trong khung 90.dp, bo góc 16.dp. |
| **Bảng kỹ năng (Moves)** | 489 | Liệt kê dạng danh sách dọc, có vạch ngăn cách mờ (Dòng 503). |

---

## 3. GIAO DIỆN TRANG DANH SÁCH VẬT PHẨM
**Tệp tin:** `app/src/main/java/com/example/pokedex/ui/screens/ItemScreen.kt`

| Thành phần UI | Vị trí (Dòng) | Đặc điểm giao diện (Modifier/Properties) |
| :--- | :--- | :--- |
| **Tiêu đề vật phẩm** | 315 | Chữ "Item List" trắng, đậm, cỡ 28.sp. |
| **Thanh lọc danh mục** | 286 | Các nút: All, Ball, Berry... Bo góc 4.dp, cao 36.dp. |
| **Đầu bảng vật phẩm** | 148 | Gồm 2 cột chính: Name (180.dp) và Category (Phần còn lại). |
| **Dòng vật phẩm (Row)** | 204 | Khi nhấn vào sẽ đổi màu nền sang `0xFF252530` (Dòng 212). |
| **Icon vật phẩm** | 222 | Kích thước nhỏ (24.dp), đặt trong Box 32.dp để căn giữa. |
| **Mô tả hiệu ứng (Effect)** | 248 | Chữ trắng, cỡ 13.sp, nằm trong khung xám mờ hiện ra khi nhấn. |

---

## CÁCH TRẢ LỜI 5 BƯỚC (ÁP DỤNG CHO UI)
Khi thầy cô hỏi: **"Cái này được vẽ như thế nào?"**

1. **Bước 1 (Định nghĩa):** "Thành phần này là một Composable [Tên hàm] nằm ở file [Tên file] dòng [Số dòng]."
2. **Bước 2 (Bố cục):** "Nó sử dụng `Row` (hàng ngang) / `Column` (hàng dọc) / `Box` (chồng lớp) để sắp xếp."
3. **Bước 3 (Kích thước/Căn lề):** "Em dùng `Modifier.size()` hoặc `weight()` để chia tỉ lệ và `padding()` để tạo khoảng cách."
4. **Bước 4 (Màu sắc/Hình dạng):** "Màu sắc sử dụng mã Hex (Vd: `0xFF...`) và bo góc bằng `RoundedCornerShape`."
5. **Bước 5 (Trạng thái):** "Khi trạng thái [biến] thay đổi, giao diện sẽ tự động vẽ lại thông qua cơ chế Recomposition của Jetpack Compose."
