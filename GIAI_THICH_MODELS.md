# GIẢI THÍCH CẤU TRÚC DỮ LIỆU (MODELS)

Tài liệu này giải thích cách tổ chức các đối tượng dữ liệu (Data Classes) trong App.

## 1. PokemonSummary (Dữ liệu rút gọn)
Dùng cho màn hình danh sách để tiết kiệm bộ nhớ.
- `id`: Định danh duy nhất.
- `name`: Tên tiếng Anh.
- `types`: Danh sách các hệ.
- `imageUrl`: Đường dẫn ảnh từ server.

## 2. PokemonDetail (Dữ liệu chi tiết)
Dùng cho màn hình chi tiết, chứa nhiều thông tin hơn:
- `stats`: Chỉ số (HP, Attack, Speed...).
- `abilities`: Các kỹ năng đặc biệt.
- `evolutionChain`: Thông tin về các cấp tiến hóa.

## 3. Team (Đối tượng đội hình)
- `id`: Mã đội.
- `name`: Tên đội người dùng đặt.
- `pokemonIds`: Danh sách ID của 6 Pokemon được chọn.

## 4. Ánh xạ dữ liệu (Mapper)
App có 3 loại Model cho cùng một đối tượng:
- **Remote Model**: Dữ liệu thô từ API (JSON).
- **Entity Model**: Cấu trúc bảng trong Database (Room).
- **Domain Model**: Dữ liệu đã sạch sẽ để UI sử dụng.
=> Việc chuyển đổi giữa chúng giúp App không bị phụ thuộc vào sự thay đổi của API bên ngoài.

## 5. Câu hỏi bảo vệ trọng tâm
- **Tại sao dùng `data class` mà không dùng `class` thường?**
  => Kotlin tự động tạo các hàm `equals()`, `hashCode()`, `toString()` và đặc biệt là `copy()`, giúp việc so sánh và cập nhật dữ liệu cực kỳ dễ dàng.
- **Trường `baseId` dùng để làm gì?**
  => Để xác định nguồn gốc tiến hóa. Ví dụ: Pichu, Pikachu, Raichu đều có chung `baseId` là ID của Pichu.
- **Tại sao cần tách riêng Summary và Detail model?**
  => Để tối ưu hóa hiệu năng. Khi hiện danh sách 1000 con, ta không cần tải các thông tin nặng như chỉ số Stat hay Evolution của từng con.