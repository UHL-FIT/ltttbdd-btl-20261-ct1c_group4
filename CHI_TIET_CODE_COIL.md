# GIẢI MÃ CHI TIẾT CODE: COIL (XỬ LÝ HÌNH ẢNH)

Tài liệu giải thích cách ứng dụng tải và quản lý hình ảnh Pokemon hiệu quả để tiết kiệm dữ liệu và bộ nhớ.

### 1. Ứng dụng Coil trong PokemonRow (PokemonScreen.kt)

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---:|:---|:---|
| 230 | `AsyncImage(...)` | Thành phần chính để tải ảnh bất đồng bộ. Nó giúp giao diện không bị "khựng" khi đang tải ảnh từ Internet. |
| 233 | `.data(pokemon.imageUrl)` | Chỉ định nguồn ảnh. Coil tự động nhận diện đây là một liên kết Web (URL) để thực hiện lệnh tải. |
| 234 | `.crossfade(true)` | Hiệu ứng chuyển cảnh: Ảnh sẽ mờ dần rồi hiện ra khi tải xong, thay vì xuất hiện đột ngột, giúp trải nghiệm người dùng mượt mà hơn. |
| 235 | `.diskCachePolicy(CachePolicy.ENABLED)` | **Bộ nhớ đệm ổ đĩa**: Lưu ảnh vào bộ nhớ máy sau lần tải đầu tiên. Lần sau xem lại, ảnh sẽ hiện ngay lập tức mà không cần mạng. |
| 231 | `LocalContext.current` | Cung cấp ngữ cảnh (Context) hiện tại cho Coil để nó biết cách quản lý tài nguyên hệ thống một cách hợp lý. |

### 2. Tại sao chọn Coil?

| Ưu điểm | Giải thích thực tế |
|:---:|:---|
| **Hiệu suất** | Tự động giảm kích thước ảnh để vừa khít với ô hiển thị, tránh việc nạp ảnh quá lớn gây tràn RAM (Out of Memory). |
| **Kotlin First** | Được viết hoàn toàn bằng Kotlin, tích hợp sâu với Jetpack Compose, giúp mã nguồn ngắn gọn và dễ hiểu. |
| **Quản lý lỗi** | Tự động xử lý các trường hợp mất mạng hoặc URL ảnh bị hỏng mà không làm ứng dụng bị treo. |

---

### 3. Câu hỏi phản biện nhanh

*   **Làm sao để ảnh hiển thị đẹp hơn?** -> Sử dụng `.placeholder()` để hiện một ảnh chờ trong lúc tải và `.error()` để hiện ảnh báo lỗi nếu tải thất bại.
*   **Coil có tốn pin không?** -> Coil rất tiết kiệm pin vì nó chỉ tải ảnh khi thành phần đó thực sự hiện lên màn hình và dừng ngay lập tức nếu người dùng cuộn đi mất.
*   **Lưu ảnh vào bộ nhớ máy có làm đầy máy không?** -> Coil có cơ chế tự động xóa các ảnh cũ (LRU Cache) khi bộ nhớ đạt đến giới hạn nhất định, đảm bảo không làm đầy dung lượng điện thoại.
