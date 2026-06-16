# GIẢI MÃ CHI TIẾT TỪNG DÒNG CODE: TRANSLATOR & UTILS

Tài liệu này giải thích chi tiết mục đích và công dụng của từng dòng code trong phần dịch thuật và các hàm tiện ích, giúp bạn trả lời các câu hỏi "Dòng này dùng để làm gì?" trong buổi bảo vệ.

---

## 1. Tệp TranslatorManager.kt (Xử lý dịch thuật AI)

Đây là thành phần quan trọng nhất giúp ứng dụng hỗ trợ tiếng Việt mà không cần dữ liệu có sẵn từ API.

| Dòng code | Công dụng / Giải thích chi tiết |
|:---|:---|
| `object TranslatorManager` | Khai báo một Singleton. Đảm bảo toàn bộ ứng dụng chỉ có một bộ máy dịch duy nhất, tiết kiệm tài nguyên RAM. |
| `private val translationCache = ConcurrentHashMap<String, String>()` | Bản đồ lưu trữ các từ đã dịch. `ConcurrentHashMap` giúp việc đọc/ghi dữ liệu an toàn khi có nhiều luồng (Thread) truy cập cùng lúc. |
| `private val pendingTranslations = ConcurrentHashMap<String, Deferred<String>>()` | Lưu trữ các tác vụ dịch đang chạy. Giúp tránh việc dịch trùng lặp một từ nhiều lần nếu có nhiều UI cùng yêu cầu một lúc. |
| `private val semaphore = Semaphore(3)` | Chốt chặn (Semaphore). Chỉ cho phép tối đa 3 tác vụ dịch được xử lý đồng thời để tránh làm quá tải CPU/RAM của điện thoại. |
| `private val options = TranslatorOptions.Builder()` | Khởi tạo cấu hình cho bộ dịch Google ML Kit. |
| `.setSourceLanguage(TranslateLanguage.ENGLISH)` | Thiết lập ngôn ngữ nguồn là tiếng Anh (ngôn ngữ gốc của PokeAPI). |
| `.setTargetLanguage(TranslateLanguage.VIETNAMESE)` | Thiết lập ngôn ngữ đích là tiếng Việt. |
| `private val translator = Translation.getClient(options)` | Tạo đối tượng thực thi dịch từ cấu hình đã thiết lập. |
| `suspend fun translate(text: String)` | Hàm dịch chính. Từ khóa `suspend` cho phép hàm này chạy ngầm (Coroutine) mà không làm treo giao diện người dùng. |
| `translationCache[text]?.let { return@coroutineScope it }` | Kiểm tra bộ nhớ đệm. Nếu từ này đã được dịch rồi thì trả về ngay lập tức để tăng tốc độ. |
| `pendingTranslations.computeIfAbsent(text) { ... }` | Nếu chưa dịch và cũng chưa có ai đang dịch từ này, thì mới tạo một tác vụ dịch mới. |
| `semaphore.withPermit { ... }` | Yêu cầu "giấy phép". Tác vụ phải đợi cho đến khi có một trong 3 "khe" trống trong Semaphore mới được bắt đầu dịch. |
| `ensureModelDownloaded()` | Kiểm tra xem gói ngôn ngữ tiếng Việt (khoảng 30MB) đã được tải về máy chưa. Nếu chưa sẽ thực hiện tải về. |
| `val result = translator.translate(text).await()` | Gọi thư viện AI thực hiện dịch chuỗi văn bản và chờ kết quả trả về. |
| `translationCache[text] = result` | Sau khi dịch xong, lưu vào cache để lần sau không phải dịch lại từ này nữa. |

---

## 2. Tệp Pokemon.kt & Converters.kt (Tiện ích dữ liệu)

Các dòng code này giúp định dạng dữ liệu thô từ Database/API thành dữ liệu đẹp mắt trên giao diện.

| Dòng code | Công dụng / Giải thích chi tiết |
|:---|:---|
| `val formattedId: String` | Biến tính toán (Computed property) để tạo ra mã ID chuyên nghiệp cho Pokemon. |
| `id.split(".")[0]` | Cắt bỏ phần thập phân. Ví dụ: ID "100.1" (dạng đặc biệt) sẽ chỉ lấy phần gốc là "100". |
| `.padStart(4, '0')` | Thêm các số 0 vào phía trước. Ví dụ: số `1` sẽ hiện thành `0001`, giúp danh sách trông đều đặn hơn. |
| `val baseName: String by lazy { ... }` | Dùng để tìm tên gốc của Pokemon. `by lazy` giúp tiết kiệm tài nguyên, chỉ tính toán khi thực sự cần. |
| `.substringBefore(" Mega")` | Loại bỏ chữ " Mega" khỏi tên (Ví dụ: "Lucario Mega" -> "Lucario") để nhận diện loài gốc. |
| `@TypeConverter` | Đánh dấu cho Room Database biết đây là hàm chuyển đổi kiểu dữ liệu đặc biệt. |
| `gson.toJson(value)` | Biến một danh sách (List) thành một chuỗi văn bản (JSON) để có thể lưu vào bộ nhớ máy. |
| `gson.fromJson(value, listType)` | Đọc chuỗi văn bản từ bộ nhớ và biến nó ngược lại thành danh sách đối tượng để hiển thị lên màn hình. |

---

## 3. Câu hỏi bảo vệ trọng tâm (Line-by-line focus)

- **Tại sao lại dùng `await()` thay vì gọi trực tiếp?**
  => Vì việc dịch AI mất thời gian (không thể xong ngay lập tức). `await()` giúp dừng luồng đó lại để chờ kết quả mà không làm đơ toàn bộ ứng dụng.
- **Tại sao phải giới hạn Semaphore bằng 3?**
  => Google ML Kit là một mô hình AI thu nhỏ. Nếu dịch quá nhiều từ cùng lúc (ví dụ 100 từ khi cuộn danh sách), điện thoại sẽ bị quá nhiệt và tràn RAM. Số 3 là con số tối ưu đã được thử nghiệm.
- **Nếu `ensureModelDownloaded()` thất bại thì sao?**
  => Trong khối `catch`, ứng dụng sẽ trả về chính từ tiếng Anh gốc (Fallback). Điều này đảm bảo ứng dụng vẫn hoạt động bình thường, không bị văng (Crash).
- **`ConcurrentHashMap` giải quyết vấn đề gì?**
  => Trong ứng dụng đa luồng, hai màn hình có thể cùng ghi dữ liệu vào Map một lúc. `ConcurrentHashMap` ngăn chặn việc xung đột dữ liệu, giúp ứng dụng ổn định hơn.
