# GIẢI MÃ CHI TIẾT CODE: ROOM DATABASE (LƯU TRỮ NỘI BỘ)

Tài liệu giải thích cách ứng dụng lưu trữ và truy vấn dữ liệu Pokemon ngoại tuyến (Offline).

### 1. PokemonDao.kt (Định nghĩa truy vấn)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 16 | `@Query("SELECT id, name...")` | Truy vấn tối ưu: Chỉ lấy các cột cần thiết (Summary) thay vì lấy toàn bộ Object lớn, giúp tiết kiệm RAM. |
| 22 | `searchPagingPokemonSummary` | Truy vấn tìm kiếm nâng cao, hỗ trợ kết hợp đồng thời từ khóa, hệ (Type) và sắp xếp (Sort). |
| 28 | `WHERE (name LIKE '%' || :query || '%')` | Kỹ thuật tìm kiếm chuỗi (Full-text search cơ bản) cho phép tìm kiếm Pokemon theo tên hoặc ID một cách linh hoạt. |
| 31 | `CASE WHEN :sortType = 'NAME' ...` | **Dynamic Sorting**: Logic sắp xếp động ngay trong câu lệnh SQL, giúp UI phản hồi nhanh mà không cần tải lại dữ liệu. |
| 43 | `getPokemonByIdFlow(id: String): Flow<...>` | Trả về dữ liệu dưới dạng **Flow**. Khi dữ liệu trong DB thay đổi, UI sẽ tự động cập nhật mà không cần gọi lại hàm. |
| 46 | `@Insert(onConflict = ...REPLACE)` | Chiến lược ghi đè: Nếu Pokemon đã tồn tại (trùng ID), Room sẽ tự động cập nhật thông tin mới nhất. |
| 64 | `getPokemonMissingDetails(...)` | Truy vấn dùng cho tính năng đồng bộ ngầm: Tìm các Pokemon chỉ mới có thông tin cơ bản để tải thêm chi tiết. |

### 2. Cấu trúc Database (PokemonDatabase.kt)

| Thành phần | Vai trò kỹ thuật |
|:---:|:---|
| `@Database(entities = [...])` | Khai báo các bảng dữ liệu sẽ có trong Database (Pokemon, Items). |
| `@TypeConverters(...)` | Bộ chuyển đổi dữ liệu: Cho phép Room lưu trữ các kiểu dữ liệu phức tạp (như List<String>) dưới dạng chuỗi văn bản. |
| `.fallbackToDestructiveMigration()` | Chính sách nâng cấp DB: Nếu cấu trúc bảng thay đổi, DB cũ sẽ bị xóa để tạo mới, tránh lỗi crash do không tương thích phiên bản. |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao không lưu tất cả vào RAM?** -> Vì danh sách hơn 1000 Pokemon kèm hình ảnh rất lớn, lưu vào DB giúp app khởi động nhanh và hoạt động được khi không có mạng.
*   **PagingSource có tác dụng gì?** -> Kết nối trực tiếp DB với thư viện Paging 3, giúp tải dữ liệu theo từng trang (20 mục một lần) khi người dùng cuộn màn hình.
*   **Làm sao Room lưu được mảng (List)?** -> Nhờ `StringListConverter`, nó biến mảng `["Fire", "Flying"]` thành chuỗi `"Fire,Flying"` trước khi lưu vào SQLite.
