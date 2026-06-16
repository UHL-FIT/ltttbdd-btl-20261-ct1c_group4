# Giải thích Kỹ thuật Chuyên sâu: Cơ chế Recomposition (Vẽ lại giao diện)

Tài liệu này phân tích cách Jetpack Compose cập nhật giao diện một cách thông minh và hiệu quả nhất.

### 1. Luồng kích hoạt (Recomposition Trigger)
- **Tác nhân**: Mọi thay đổi trong ứng dụng đều bắt đầu từ **State**. 
- **Ví dụ**: Khi bạn gõ vào ô tìm kiếm (`Dòng 126` trong `TierScreen.kt`), nó cập nhật `searchQuery` trong ViewModel.
- **Cơ chế**: Compose đăng ký lắng nghe (observe) các State này. Khi State thay đổi, nó sẽ đánh dấu (mark) Composable tương ứng là "Dirty" (cần vẽ lại).

### 2. Kỹ thuật "Bỏ qua thông minh" (Smart Skipping)
- **Giải thích**: Một trong những ưu điểm lớn nhất của Compose là nó không vẽ lại toàn bộ màn hình.
- **Tại sao?**: Framework kiểm tra các tham số đầu vào của Composable. Nếu tham số không thay đổi, nó sẽ **Skip** (bỏ qua) việc render lại hàm đó.
- **Ứng dụng**: Khi bạn cuộn danh sách, các thành phần như `TierHeaderSection` (Dòng 137) sẽ không bao giờ bị vẽ lại vì dữ liệu của nó là tĩnh. Điều này giúp giảm tải cực lớn cho CPU.

### 3. Định danh đối tượng (Positional Memoization)
- **Code**: `key(pokemon.id)` tại **Dòng 265**.
- **Giải thích sâu**: 
    - Khi danh sách Pokemon thay đổi (ví dụ: lọc theo hệ), vị trí của các Pokemon trong mảng sẽ bị xáo trộn.
    - Nếu không có `key`, Compose sẽ tưởng rằng đó là các đối tượng mới và vẽ lại toàn bộ từ đầu.
    - Với `key(pokemon.id)`, Compose nhận diện được: "À, đây vẫn là Pikachu, nó chỉ chuyển từ vị trí A sang vị trí B". Nhờ vậy, nó chỉ di chuyển vị trí vẽ thay vì render lại ảnh, giúp ứng dụng đạt tốc độ **60 khung hình/giây (60 FPS)** ngay cả trên máy cấu hình yếu.

### 4. Trạng thái không thay đổi (Stability)
- **Kỹ thuật**: Sử dụng `@Immutable` cho `TierDataWrapper` và các Model dữ liệu.
- **Ý nghĩa**: Bằng cách khẳng định với Compiler rằng dữ liệu này không bao giờ thay đổi sau khi khởi tạo, chúng ta giúp Compose tự tin bỏ qua các bước kiểm tra so sánh phức tạp, tăng tốc độ phản hồi của giao diện lên mức tối đa.

### 5. Quản lý tác vụ render (Concurrent Composition)
- **Giải thích**: Compose có thể tính toán cấu trúc giao diện mới ở một luồng khác trước khi áp dụng nó vào màn hình. Điều này kết hợp với `Dispatchers.Default` trong ViewModel tạo thành một "dây chuyền sản xuất" khép kín:
    - Luồng 1 (Default): Lọc và sắp xếp dữ liệu Pokemon.
    - Luồng 2 (Composition): Tính toán các ô giao diện sẽ nằm ở đâu.
    - Luồng 3 (Main/UI): Vẽ thực tế lên màn hình.
=> Sự phối hợp nhịp nhàng này là lý do tại sao ứng dụng Pokedex của bạn vẫn mượt mà dù phải hiển thị hàng trăm tấm ảnh cùng lúc.
