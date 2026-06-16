# GIẢI THÍCH CHI TIẾT CÔNG DỤNG CÁC DÒNG CODE GIAO DIỆN

Tài liệu này giải thích ý nghĩa kỹ thuật của các dòng code UI để bạn có thể trả lời khi thầy cô hỏi "Dòng này dùng để làm gì?".

---

## 1. MÀN HÌNH DANH SÁCH POKEMON (PokemonScreen.kt)

### Khung danh sách (LazyColumn)
- `state = listState`: Gắn bộ theo dõi vị trí cuộn (dùng để hiện nút "Back to top").
- `contentPadding = PaddingValues(...)`: Tạo khoảng trống ở dưới cùng để danh sách không bị che bởi các nút điều hướng.

### Thanh tìm kiếm (OutlinedTextField)
- `modifier = Modifier.fillMaxWidth()`: Giúp thanh tìm kiếm giãn hết chiều ngang màn hình.
- `leadingIcon = { Icon(...) }`: Đặt biểu tượng kính lúp ở đầu ô nhập để người dùng nhận diện chức năng.
- `shape = RoundedCornerShape(4.dp)`: Bo nhẹ 4 góc của ô nhập cho cảm giác hiện đại.

### Header bảng (stickyHeader)
- `stickyHeader { ... }`: Dòng này rất quan trọng, nó giúp tiêu đề bảng "dính" lại trên đỉnh màn hình khi bạn cuộn danh sách xuống dưới.
- `weight(1f)`: Dòng này dùng trong `Modifier` của cột Tên để nó tự động chiếm hết khoảng trống còn lại giữa cột ID và cột Hệ.

---

## 2. MÀN HÌNH CHI TIẾT POKEMON (PokemonDetailScreen.kt)

### Banner chính (MainBanner)
- `Brush.verticalGradient(...)`: Tạo hiệu ứng màu chuyển sắc từ trên xuống dưới cho viền khung ảnh, giúp ảnh Pokemon nổi bật hơn.
- `ContentScale.Fit`: Đảm bảo ảnh Pokemon luôn nằm trọn vẹn trong khung hình mà không bị cắt mất phần nào.

### Thanh chỉ số (StatRow)
- `Modifier.height(8.dp)`: Quy định độ dày của thanh năng lượng (HP, ATK...).
- `fillMaxWidth(value / 255f)`: Đây là logic tính toán độ dài thanh màu. Vì chỉ số Pokemon tối đa thường là 255, nên lấy giá trị hiện tại chia cho 255 để ra tỉ lệ phần trăm tương ứng trên màn hình.
- `background(Color(0xFF252530))`: Tạo lớp nền tối phía dưới, làm cho phần thanh màu phía trên trông giống như đang được nạp đầy.

### Sơ đồ tiến hóa (EvolutionTree)
- `IntrinsicSize.Min`: Giúp các hàng trong sơ đồ tiến hóa có chiều cao bằng nhau dựa trên phần tử cao nhất, tạo sự cân đối.
- `Icons.AutoMirrored.Filled.ArrowForward`: Sử dụng icon mũi tên tự động đảo hướng nếu người dùng dùng ngôn ngữ đọc từ phải sang trái (như tiếng Ả Rập).

---

## 3. MÀN HÌNH DANH SÁCH VẬT PHẨM (ItemScreen.kt)

### Bộ lọc (ItemCategoryFilters)
- `horizontalArrangement = Arrangement.spacedBy(8.dp)`: Tự động tạo khoảng cách 8.dp giữa các nút lọc mà không cần phải đặt padding cho từng nút thủ công.
- `if (isSelected) Color(0xFF00B0FF) else ...`: Logic đổi màu nút: Nếu đang được chọn thì hiện màu xanh dương, nếu không thì hiện màu tối.

### Dòng vật phẩm (ItemRow)
- `AnimatedVisibility(visible = expanded)`: Tạo hiệu ứng đóng/mở mượt mà khi xem mô tả vật phẩm thay vì hiện ra đột ngột.
- `AsyncImage`: Sử dụng thư viện Coil để tải ảnh từ mạng. `crossfade(true)` giúp ảnh hiện lên nhẹ nhàng (mờ dần rồi rõ) thay vì xuất hiện tức thì gây giật mắt.

---

## MẸO GIẢI THÍCH CÁC TỪ KHÓA PHỔ BIẾN (MODIFIER)

Nếu thầy cô chỉ vào bất kỳ dòng nào có chữ `Modifier`, hãy nhớ các quy tắc sau:
1. **`padding(16.dp)`**: "Dòng này tạo khoảng cách đệm để các thành phần không bị dính sát vào mép màn hình."
2. **`fillMaxSize()`**: "Dòng này cho phép thành phần UI chiếm toàn bộ diện tích của màn hình."
3. **`align(Alignment.Center)`**: "Dòng này dùng để đưa thành phần vào chính giữa khung chứa."
4. **`background(color, shape)`**: "Dòng này dùng để đổ màu nền và định hình hình dáng (như bo góc) cho khối UI."
5. **`border(width, color, shape)`**: "Dòng này tạo đường viền bao quanh khối UI để phân tách các khu vực rõ ràng hơn."
