# GIẢI MÃ CHI TIẾT CODE: MODELS (CẤU TRÚC DỮ LIỆU)

Tài liệu giải thích cách định nghĩa các đối tượng Pokemon và cách chuyển đổi dữ liệu để lưu trữ.

### 1. Pokemon.kt (Lớp dữ liệu chính)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 8 | `@Entity(tableName = "pokemon")` | Đánh dấu đây là một bảng trong Database Room. Mỗi đối tượng `Pokemon` tương ứng với một dòng trong bảng. |
| 10 | `@PrimaryKey val id: String` | Khóa chính của bảng. Sử dụng kiểu **String** để hỗ trợ các ID biến thể như `6.1` (Mega Charizard X), `6.2` (Mega Charizard Y). |
| 35 | `val total: Int get() = ...` | **Computed Property**: Thuộc tính tự tính toán tổng chỉ số (BST) dựa trên 6 chỉ số cơ bản, không cần lưu trong DB. |
| 38 | `val baseName: String by lazy { ... }` | Kỹ thuật **Lazy Loading**: Chỉ tính toán tên gốc một lần duy nhất khi cần, giúp tối ưu hiệu năng xử lý văn bản. |
| 40 | `.substringBefore(" Mega")` | Thuật toán tách tên: Tự động loại bỏ các hậu tố (Mega, Alola, Galar) để lấy tên loài gốc (Species). |
| 53 | `val formattedId: String` | Định dạng hiển thị ID: Luôn hiển thị dưới dạng 4 chữ số (ví dụ: ID `25` -> `0025`) để giao diện chuyên nghiệp hơn. |

### 2. Các lớp bổ trợ (Supporting Classes)

| Class Name | Vai trò kỹ thuật | Giải thích thực tế |
|:---:|:---|:---|
| `EvolutionStep` | Cấu trúc một bước tiến hóa | Lưu thông tin: ID, tên, ảnh và **điều kiện tiến hóa** (ví dụ: "Level 16"). |
| `MoveInfo` | Thông tin chi tiết chiêu thức | Chứa mọi thông số của 1 chiêu: Hệ, Loại (Vật lý/Đặc biệt), Sát thương và cách học. |
| `Converters` | Bộ chuyển đổi dữ liệu (TypeConverter) | **Cầu nối SQLite**: Chuyển các danh sách phức tạp (Moves, Abilities) sang chuỗi JSON để SQLite có thể lưu được. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng `Double` cho `sortOrder`?** -> Để sắp xếp các biến thể (Mega, Alola) nằm ngay sau Pokemon gốc (VD: 6.0 là Charizard, 6.0001 là Mega X).
*   **Tại sao cần `hasFullDetails`?** -> Để app biết Pokemon này đã được tải đầy đủ chi tiết chưa hay mới chỉ có thông tin cơ bản, từ đó quyết định có cần tải thêm không.
*   **Lazy property có lợi ích gì?** -> Giúp giảm tải cho CPU khi hiển thị danh sách dài, vì tên gốc chỉ được tính toán khi người dùng thực sự xem chi tiết.
