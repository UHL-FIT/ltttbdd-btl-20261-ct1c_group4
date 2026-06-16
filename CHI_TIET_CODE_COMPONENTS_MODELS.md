# CHI TIẾT TỪNG DÒNG CODE: COMPONENTS & MODELS

Tài liệu này giải thích chi tiết ý nghĩa kỹ thuật của các thành phần giao diện dùng chung và cách tổ chức dữ liệu trong App.

---

## 1. ShortcutCard.kt (Thẻ Pokemon dùng chung)

Đây là thành phần xuất hiện ở hầu hết các màn hình (Danh sách, Tìm kiếm, Build).

- **`@Composable fun ShortcutCard(...)`**: Định nghĩa một hàm vẽ giao diện. Các tham số như `pokemon`, `onClick` giúp component này có thể tái sử dụng linh hoạt.
- **`AsyncImage(model = pokemon.imageUrl, ...)`**: 
    - Sử dụng thư viện Coil để tải ảnh từ URL.
    - **`contentScale = ContentScale.Fit`**: Đảm bảo ảnh Pokemon không bị bóp méo, giữ đúng tỉ lệ gốc.
    - **`placeholder = painterResource(R.drawable.pokeball)`**: Hiện hình PokeBall trong khi chờ tải ảnh từ mạng.
- **`Text(text = pokemon.formattedId, ...)`**: Hiển thị ID đã được định dạng (ví dụ: #0025) với font chữ nhỏ, màu xám đặc trưng.
- **`Modifier.combinedClickable(...)`**: Cho phép xử lý cả sự kiện "Nhấn" (để xem chi tiết) và "Nhấn giữ" (để chọn nhanh vào đội hình).

---

## 2. PokemonSummary.kt (Dữ liệu rút gọn)

- **`data class PokemonSummary(...)`**: Sử dụng `data class` để tự động có các hàm so sánh dữ liệu, rất quan trọng khi làm việc với danh sách lớn.
- **`val total: Int get() = hp + attack + ...`**: Biến tính toán tổng chỉ số (BST). Việc dùng `get()` giúp giá trị này luôn được cập nhật chính xác nếu các chỉ số thành phần thay đổi.
- **`val pveTier: String? = null`**: Dấu `?` cho phép giá trị này có thể bị trống (null) nếu Pokemon đó chưa được phân hạng, giúp App không bị lỗi khi dữ liệu thiếu sót.

---

## 3. TypeBadge (Huy hiệu hệ - Thường nằm trong các Screen)

- **`Surface(color = getTypeColor(type), ...)`**: Tạo một khối màu nền bo góc dựa trên hệ của Pokemon.
- **`LaunchedEffect(type) { translatedName = TranslatorManager.translate(type) }`**:
    - **`LaunchedEffect`**: Một "hiệu ứng phụ" trong Compose. Khi hệ của Pokemon thay đổi (ví dụ từ "Fire" sang "Water"), nó sẽ tự động gọi AI để dịch lại tên hệ sang tiếng Việt.
- **`Text(text = translatedName.uppercase())`**: Hiển thị tên hệ bằng chữ in hoa sau khi đã dịch xong.

---

## 4. Tại sao code lại viết như vậy? (Dành cho bảo vệ)

1.  **Tại sao lại tách ra `PokemonSummary` và `Pokemon` (Detail)?**
    - => Để tiết kiệm băng thông và RAM. Khi hiện danh sách 1000 con, App chỉ tải `Summary` (nhẹ). Chỉ khi người dùng bấm vào một con cụ thể, App mới tải `Detail` (nặng, chứa nhiều thông tin moves, tiến hóa...).
2.  **Tại sao dùng `AsyncImage` thay vì `Image` thông thường?**
    - => Vì ảnh Pokemon nằm trên Server. `AsyncImage` xử lý việc tải luồng phụ, bộ nhớ đệm và hiển thị ảnh chờ một cách tự động, giúp code ngắn gọn hơn hàng chục dòng so với cách làm truyền thống.
3.  **Tại sao lại dùng `LaunchedEffect` trong Component?**
    - => Vì việc dịch thuật (Translator) tốn thời gian. Nếu gọi trực tiếp trong hàm vẽ, giao diện sẽ bị đơ. `LaunchedEffect` giúp việc dịch chạy song song với việc vẽ giao diện.
4.  **Tại sao lại dùng `combinedClickable`?**
    - => Để tăng trải nghiệm người dùng (UX). Người dùng có thể xem nhanh thông tin hoặc thao tác nhanh mà không cần phải chuyển màn hình quá nhiều.
