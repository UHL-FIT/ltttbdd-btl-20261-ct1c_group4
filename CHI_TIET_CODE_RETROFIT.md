# GIẢI MÃ CHI TIẾT CODE: RETROFIT & API (KẾT NỐI MẠNG)

Tài liệu giải thích cách ứng dụng giao tiếp với PokeAPI để lấy dữ liệu Pokemon.

### 1. PokeApiService.kt (Định nghĩa Interface)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 10 | `@GET("pokemon")` | Định nghĩa yêu cầu lấy danh sách Pokemon. Sử dụng **suspend** để không làm treo ứng dụng khi đang tải dữ liệu. |
| 12 | `@Query("limit")` | Tham số hóa API: Cho phép điều chỉnh số lượng Pokemon muốn lấy trong một lần gọi (mặc định là 100). |
| 17 | `@Path("name")` | Truyền tham số trực tiếp vào đường dẫn URL (ví dụ: `pokemon/pikachu`) để lấy thông tin chi tiết của 1 loài. |
| 21 | `@Url url: String` | Linh hoạt: Cho phép gọi một URL bất kỳ được trả về từ kết quả trước đó (thường dùng cho Evolution Chain). |
| 35 | `@SerializedName(...)` | **Mapping dữ liệu**: Chuyển đổi tên trường từ JSON (ví dụ: `flavor_text_entries`) sang tên biến Kotlin ngắn gọn. |

### 2. Cấu trúc Data Class (Phản hồi từ API)

| Class Name | Vai trò kỹ thuật | Giải thích thực tế |
|:---:|:---|:---|
| `PokemonDetailResponse` | Chứa toàn bộ "hồ sơ" Pokemon. | Gom nhóm: Hệ (Types), Chỉ số (Stats), Ảnh (Sprites), Kỹ năng (Abilities), Chiêu thức (Moves). |
| `MoveDetailResponse` | Chứa chi tiết về một chiêu thức. | Lưu các thông tin: Sát thương (Power), Độ chính xác (Accuracy), Độ ưu tiên (Priority). |
| `ChainLink` | Cấu trúc cây (Tree) | Đại diện cho một mắt xích trong chuỗi tiến hóa, chứa danh sách các dạng tiến hóa tiếp theo (`evolves_to`). |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao dùng Retrofit thay vì HttpURLConnection?** -> Retrofit tự động hóa việc chuyển đổi JSON sang Object, giúp mã nguồn sạch sẽ, dễ bảo trì và hiệu suất cao hơn.
*   **SerializedName có bắt buộc không?** -> Không bắt buộc nếu tên biến Kotlin giống hệt tên trường trong JSON. Tuy nhiên, nên dùng để tuân thủ quy tắc đặt tên `camelCase` của Kotlin.
*   **Tại sao dùng `@GET` không có tham số cho một số hàm?** -> Vì PokeAPI thường trả về các URL đầy đủ cho Evolution hoặc Species, ta chỉ cần truyền URL đó vào để lấy tiếp dữ liệu.
