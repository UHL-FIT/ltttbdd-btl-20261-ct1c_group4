# GIẢI THÍCH CHI TIẾT KỸ THUẬT: HOME, GUIDE & STATIC DATA

Tài liệu này giải thích chi tiết công dụng của các dòng code quan trọng về mặt kỹ thuật, giúp bạn trả lời câu hỏi "Tại sao lại code như thế này?".

---

## 1. MÀN HÌNH TRANG CHỦ (HomeScreen.kt)

### Quản lý danh sách phím tắt (shortcuts)
- **`val shortcuts = listOf(...)`**: Thay vì viết code giao diện cho 5 nút bấm khác nhau, ta đưa dữ liệu vào một danh sách. Điều này tuân thủ nguyên tắc **DRY (Don't Repeat Yourself)**. Khi cần thêm chức năng, chỉ cần thêm 1 dòng vào List.

### Khối Layout chính (Column)
- **`.verticalScroll(rememberScrollState())`**: Đây là dòng cực kỳ quan trọng. Nó biến một cột tĩnh thành một danh sách có thể cuộn. `rememberScrollState()` giúp ứng dụng ghi nhớ vị trí người dùng đang cuộn dù họ có xoay màn hình.
- **`Arrangement.spacedBy(12.dp)`**: Tự động tạo khoảng cách 12dp giữa các `ShortcutCard`. Kỹ thuật này tốt hơn việc đặt `padding` cho từng Card vì nó không tạo ra khoảng trống thừa ở phần tử đầu và cuối.

### Thành phần tiêu đề (HomeHeaderSection)
- **`Icons.Default.Home`**: Sử dụng thư viện icon chuẩn của Google, giúp app nhẹ hơn vì không phải chứa file ảnh Vector rời.
- **`RoundedCornerShape(8.dp)`**: Tạo độ bo góc mềm mại, phù hợp với phong cách thiết kế hiện đại của Android (Material 3).

---

## 2. MÀN HÌNH GIỚI THIỆU (GuideScreen.kt)

### Kỹ thuật văn bản nâng cao (Rich Text)
- **`buildAnnotatedString { ... }`**: Dòng này cho phép chúng ta "vẽ" một đoạn văn bản có nhiều định dạng. 
- **`withStyle(SpanStyle(fontWeight = FontWeight.Bold))`**: Dùng để bôi đậm riêng một từ (như **HP**, **ATK**) ngay trong đoạn văn. Kỹ thuật này giúp tiết kiệm tài nguyên hơn là việc chia nhỏ thành nhiều thẻ `Text` nằm trong `Row`.

### Bảng dữ liệu linh hoạt (Nature & Type Table)
- **`horizontalScroll(...)`**: Các bảng này có rất nhiều cột. Nếu không có dòng này, bảng sẽ bị ép chặt lại (squashed) hoặc bị mất cột trên điện thoại nhỏ. Dòng này tạo ra một "khung cửa sổ" cho phép người dùng vuốt ngang để xem hết dữ liệu.
- **`forEachIndexed { rowIndex, rowNatures -> ... }`**: Sử dụng vòng lặp lồng nhau để vẽ lưới. `Indexed` giúp chúng ta biết được tọa độ (hàng, cột) để xử lý logic đặc biệt.
- **`if (isNeutral) Color(0xFFB0BEC5)`**: Logic kiểm tra tọa độ: nếu hàng và cột trùng nhau (đường chéo), hệ thống tự đổi màu nền sang xám, giúp người dùng dễ dàng nhận diện các tính cách trung lập.

### Logic màu sắc động trong Bảng Hệ
- **`val (bgColor, textColor) = when (value) { ... }`**: Đây là kỹ thuật **Mapping dữ liệu**. Thay vì viết code UI phức tạp, ta chỉ cần kiểm tra giá trị:
    - Nếu là "2" -> Trả về màu Xanh.
    - Nếu là "½" -> Trả về màu Đỏ.
    - Giúp code sạch và cực kỳ dễ bảo trì.

---

## 3. LOGIC ĐỔ DỮ LIỆU TĨNH (Static Data)

### Tại sao dùng `Map` và `List` trong code?
- **Độ phức tạp O(1)**: Khi bạn tìm kiếm hiệu quả của hệ Lửa đối với hệ Cỏ, code chỉ cần chạy đúng 1 lần (`effectiveness["FIRE"]?["GRASS"]`) là ra kết quả. Nếu dùng Database, bạn phải thực hiện truy vấn (Query) tốn nhiều thời gian và bộ nhớ hơn.
- **Async vs Sync**: Dữ liệu tĩnh là dữ liệu **đồng bộ (Sync)**. Nó luôn có sẵn ngay khi app mở lên, không bao giờ gặp tình trạng "đang tải" hay "lỗi kết nối", giúp trải nghiệm người dùng đạt điểm 10 về độ mượt.

### Cách tái sử dụng Component (Slot API)
- **`extraContent: @Composable (() -> Unit)? = null`**: Đây là một kỹ thuật cao cấp trong Compose. Nó cho phép hàm `GuideSection` để trống một "khe cắm" (slot). Chúng ta có thể truyền bất cứ thứ gì vào đó (Text, Ảnh, hoặc cả một cái Bảng), giúp code linh hoạt tối đa.

---
*Ghi chú: Tài liệu này giải thích về "Tại sao" và "Thế nào" ở mức độ kỹ thuật sâu hơn, dùng để đối phó với các câu hỏi hóc búa từ giảng viên về kiến trúc code.*
