# GIẢI MÃ CHI TIẾT CODE: REPOSITORY (ĐIỀU PHỐI DỮ LIỆU)

Tài liệu giải thích cách Repository làm trung gian giữa API (Internet) và Database (Local).

### 1. PokemonRepository.kt (Logic trung tâm)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 29 | `apiService = Retrofit.Builder()...` | Khởi tạo công cụ kết nối API (Retrofit) với cấu hình chuyển đổi JSON sang đối tượng Kotlin tự động. |
| 63 | `fetchAndCachePokemon(...)` | Hàm tải dữ liệu từ Web về. Sử dụng **chunked(10)** để chia nhỏ dữ liệu tải về, tránh làm nghẽn mạng. |
| 76 | `async { ... }.awaitAll()` | **Xử lý song song**: Tải thông tin của nhiều Pokemon cùng một lúc để tăng tốc độ khởi động ứng dụng gấp nhiều lần. |
| 120 | `fetchFastPokemonDetails(...)` | Chiến lược **"Tải nhanh"**: Chỉ lấy các thông tin thiết yếu (hệ, chỉ số, ảnh) để người dùng xem danh sách trước. |
| 161 | `startBackgroundSync(...)` | **Đồng bộ hóa thông minh**: Tự động tải bù các thông tin chi tiết (chiêu thức, tiến hóa) khi người dùng không sử dụng máy. |
| 172 | `fetchAndSaveFullDetails(...)` | Hàm tải "Sâu": Thu thập toàn bộ dữ liệu phức tạp của một Pokemon và cập nhật vào Database Room. |
| 210 | `TranslatorManager.translate(...)` | Tích hợp công cụ dịch: Tự động chuyển đổi mô tả Pokemon và chiêu thức sang Tiếng Việt trước khi lưu. |
| 301 | `parseEvolutionChain(...)` | Thuật toán **Đệ quy**: Duyệt qua cây tiến hóa của Pokemon (từ Baby -> Basic -> Stage 1 -> Stage 2) để dựng sơ đồ. |

### 2. Các cơ chế xử lý dữ liệu đặc biệt

| Thành phần | Ứng dụng trong Code | Giải thích thực tế |
|:---:|:---|:---|
| **Caching** | `moveDetailCache`, `abilityDetailCache` | Lưu tạm các chiêu thức đã tải. Nếu Pokemon khác cũng có chiêu đó, app lấy từ RAM thay vì tải lại từ Web. |
| **Paging 3** | `getPokemonPaging(...)` | Cấu hình cách thức cuộn danh sách: Tải trước 5 mục tiếp theo (`prefetchDistance = 5`) để người dùng không thấy lag. |
| **Error Handling** | `try { ... } catch (e: Exception)` | Bọc mọi lệnh gọi mạng trong khối try-catch để nếu mất mạng, ứng dụng vẫn chạy bình thường thay vì bị văng (crash). |

---

### 3. Câu hỏi phản biện nhanh

*   **Tại sao không tải hết dữ liệu ngay từ đầu?** -> Vì dữ liệu của 1000 Pokemon rất lớn (vài chục MB văn bản). Tải dần giúp tiết kiệm dữ liệu di động và bộ nhớ máy.
*   **Tại sao cần `extractIdFromUrl`?** -> Vì PokeAPI trả về ID nằm trong URL (ví dụ: `.../pokemon/25/`). Ta cần tách số `25` ra để làm ID chính trong Database.
*   **Async/Await giúp ích gì?** -> Giống như việc cử 10 nhân viên đi mua hàng cùng lúc thay vì 1 người đi mua từng món một, giúp tiết kiệm thời gian chờ đợi.
