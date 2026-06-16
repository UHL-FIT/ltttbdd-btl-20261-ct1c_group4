# GIẢI THÍCH CHI TIẾT TỪNG DÒNG CODE: TRANSLATOR & UTILS

Tài liệu này giải thích công dụng của **mọi dòng code quan trọng** trong phần dịch thuật và tiện ích dữ liệu để bạn tự tin trả lời vấn đáp.

---

## 1. Tệp TranslatorManager.kt (Bộ máy dịch thuật AI)

Tệp này quản lý việc chuyển đổi ngôn ngữ Anh -> Việt bằng thư viện Google ML Kit chạy trực tiếp trên điện thoại (Offline).

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `object TranslatorManager` | Khai báo dạng Singleton. Giúp App chỉ tạo duy nhất 1 "bộ dịch" dùng chung, tránh lãng phí RAM. |
| `private val translationCache = ConcurrentHashMap<String, String>()` | Bản đồ lưu kết quả trên RAM. Dùng `ConcurrentHashMap` để an toàn khi nhiều màn hình cùng gọi dịch một lúc (Thread-safe). |
| `private val semaphore = Semaphore(3)` | **Chốt chặn quan trọng**. Chỉ cho phép tối đa 3 từ được dịch cùng lúc. Giúp điện thoại không bị nóng hoặc lag khi cuộn danh sách nhanh. |
| `private val options = TranslatorOptions.Builder()` | Khởi tạo cấu hình cho ML Kit. |
| `.setSourceLanguage(TranslateLanguage.ENGLISH)` | Quy định ngôn ngữ nguồn là tiếng Anh (dữ liệu từ PokeAPI). |
| `.setTargetLanguage(TranslateLanguage.VIETNAMESE)` | Quy định ngôn ngữ đích cần dịch sang là tiếng Việt. |
| `suspend fun translate(text: String)` | Hàm "treo" (suspend). Cho phép App dịch ngầm ở luồng phụ, giúp giao diện người dùng vẫn mượt mà. |
| `translationCache[text]?.let { return@coroutineScope it }` | Nếu từ này đã có trong bộ nhớ đệm (Cache), trả về ngay lập tức để tiết kiệm pin và thời gian. |
| `pendingTranslations.computeIfAbsent(text) { ... }` | Nếu chưa dịch, App sẽ kiểm tra xem có ai đang dịch từ này chưa. Nếu chưa mới bắt đầu dịch. |
| `semaphore.withPermit { ... }` | Yêu cầu quyền dịch. Nếu đã có 3 từ đang dịch, từ thứ 4 phải đứng đợi "xếp hàng". |
| `ensureModelDownloaded()` | Tải gói ngôn ngữ Việt Nam (khoảng 30MB) về máy lần đầu tiên. |
| `val result = translator.translate(text).await()` | Lệnh thực thi dịch thực sự và chờ kết quả trả về từ mô hình AI. |
| `translationCache[text] = result` | Lưu kết quả mới vào Cache để lần sau không phải dịch lại. |

---

## 2. Các hàm tiện ích trong Pokemon.kt (Xử lý dữ liệu)

Các dòng code này giúp biến dữ liệu thô từ API thành thông tin hiển thị đẹp mắt cho người dùng.

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `val formattedId: String get() { ... }` | Biến tính toán để tạo ID dạng chuyên nghiệp (Ví dụ: #0025). |
| `val baseId = id.split(".")[0]` | Lấy ID gốc. Ví dụ ID "6.1" của Mega Charizard sẽ lấy là "6". |
| `.padStart(4, '0')` | Thêm các số 0 vào trước cho đủ 4 chữ số (1 -> 0001, 25 -> 0025). |
| `val baseName: String by lazy { ... }` | Tìm tên gốc của Pokemon. `by lazy` giúp tiết kiệm tài nguyên (chỉ tính khi cần). |
| `.substringBefore(" Mega")` | Cắt bỏ phần "Mega" khỏi tên (Ví dụ: "Lucario Mega" -> "Lucario"). |
| `.substringBefore(" Alola")` | Tương tự, cắt bỏ các hậu tố vùng miền như Alola, Galar để nhận diện cùng một loài Pokemon. |

---

## 3. Câu hỏi bảo vệ trọng tâm (Line-specific)

1.  **Tại sao em lại dùng `await()` trong hàm dịch?**
    - *Trả lời:* Vì AI cần thời gian để tính toán. `.await()` giúp chương trình chờ kết quả mà không làm đứng màn hình (Main Thread).
2.  **Tại sao phải giới hạn Semaphore là 3?**
    - *Trả lời:* Mô hình AI của Google ML Kit tiêu tốn rất nhiều tài nguyên. Giới hạn là 3 giúp App vừa dịch nhanh vừa đảm bảo máy không bị quá nhiệt.
3.  **Điều gì xảy ra nếu không tải được gói ngôn ngữ (Offline)?**
    - *Trả lời:* Trong code em đã dùng `try-catch`. Nếu dịch lỗi, App sẽ trả về chính từ tiếng Anh đó (Fallback) để người dùng vẫn có thông tin xem thay vì bị lỗi trắng màn hình.
4.  **Tại sao dùng `ConcurrentHashMap` thay vì `HashMap` thường?**
    - *Trả lời:* Vì việc dịch diễn ra ở luồng nền (Background). `ConcurrentHashMap` ngăn chặn lỗi xung đột khi nhiều luồng cùng ghi dữ liệu vào Cache một lúc.
