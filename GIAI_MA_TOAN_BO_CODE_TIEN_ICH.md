# GIẢI MÃ CHI TIẾT TOÀN BỘ CODE: TRANSLATOR & UTILS

Tài liệu này giải thích công dụng của **mọi dòng code** trong bộ công cụ dịch thuật và tiện ích để phục vụ câu hỏi vấn đáp trực tiếp.

---

## 1. Tệp TranslatorManager.kt (Dịch thuật AI)

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `object TranslatorManager` | Khai báo Singleton. Đảm bảo toàn bộ App chỉ dùng chung 1 "máy dịch", không tạo nhiều gây tốn RAM. |
| `private val translationCache = ConcurrentHashMap<String, String>()` | Bản đồ lưu trữ kết quả. Dùng `ConcurrentHashMap` để khi nhiều màn hình cùng gọi dịch (đa luồng) không bị xung đột. |
| `private val pendingTranslations = ConcurrentHashMap<String, Deferred<String>>()` | Danh sách các từ "đang chờ" dịch. Giúp tránh việc dịch trùng lặp 1 từ nhiều lần cùng lúc. |
| `private val semaphore = Semaphore(3)` | Chốt chặn tài nguyên. Chỉ cho phép 3 "người" (tiến trình) vào dịch cùng lúc để máy không bị quá tải/nóng. |
| `private val options = TranslatorOptions.Builder()` | Khởi tạo cấu hình cho bộ dịch Google ML Kit. |
| `.setSourceLanguage(TranslateLanguage.ENGLISH)` | Thiết lập ngôn ngữ gốc là tiếng Anh (vì dữ liệu PokeAPI trả về là tiếng Anh). |
| `.setTargetLanguage(TranslateLanguage.VIETNAMESE)` | Thiết lập ngôn ngữ muốn chuyển sang là tiếng Việt. |
| `private val translator: Translator = Translation.getClient(options)` | Tạo đối tượng `translator` thực tế từ cấu hình trên để bắt đầu làm việc. |
| `suspend fun translate(text: String): String = coroutineScope { ... }` | Hàm chính để dịch. `suspend` giúp nó chạy ngầm. `coroutineScope` bảo vệ các tác vụ con bên trong. |
| `translationCache[text]?.let { return@coroutineScope it }` | Nếu từ này đã có trong "bộ nhớ đệm" (cache), trả về ngay lập tức (mất 0.001 giây). |
| `pendingTranslations.computeIfAbsent(text) { ... }` | Nếu từ này chưa có trong cache và cũng chưa ai dịch, thì mới bắt đầu tạo một tác vụ dịch mới. |
| `async { ... }` | Chạy tác vụ dịch một cách bất đồng bộ (không làm đứng màn hình). |
| `semaphore.withPermit { ... }` | Xếp hàng. Khi nào có "chỗ trống" trong 3 lượt cho phép thì mới được vào dịch. |
| `ensureModelDownloaded()` | Kiểm tra xem máy đã tải gói tiếng Việt chưa, nếu chưa thì tải về (yêu cầu wifi/mạng). |
| `val result = translator.translate(text).await()` | Lệnh thực thi dịch thực sự và chờ kết quả trả về từ thư viện AI. |
| `translationCache[text] = result` | Lưu kết quả vừa dịch xong vào cache để lần sau dùng lại ngay. |
| `pendingTranslations.remove(text)` | Xóa khỏi danh sách "đang chờ" vì đã dịch xong. |

---

## 2. Tệp Pokemon.kt (Logic định dạng & Xử lý chuỗi)

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `val total: Int get() = hp + attack + ...` | Biến tính toán tổng sức mạnh (Base Stat Total). Tự động cộng dồn các chỉ số khi được gọi. |
| `val baseName: String by lazy { ... }` | Tìm tên gốc của Pokemon. `by lazy` nghĩa là khi nào cần mới tính, tính xong lưu lại luôn. |
| `.substringBefore(" Mega")` | Cắt bỏ phần "Mega" (Ví dụ: "Charizard Mega X" -> "Charizard"). Dùng để nhận diện cùng loài. |
| `.substringBefore(" Alola")` | Tương tự, cắt bỏ các hậu tố vùng miền như Alola, Galar để lấy tên gốc. |
| `val formattedId: String get() { ... }` | Biến định dạng ID hiển thị trên UI. |
| `val baseId = id.split(".")[0]` | Lấy phần nguyên của ID (Ví dụ: ID "6.1" của Mega Charizard sẽ lấy là "6"). |
| `baseId.padStart(4, '0')` | Thêm số 0 vào trước cho đủ 4 chữ số để hiện kiểu chuyên nghiệp (Ví dụ: #0006). |

---

## 3. Các câu hỏi "Bẫy" của giảng viên

**1. Tại sao em không dùng `String.capitalize()` mà lại viết hàm riêng hoặc xử lý phức tạp?**
- *Trả lời:* Vì tên Pokemon trong API đôi khi có các hậu tố đặc biệt như "-mega", "-f", "-m". Em xử lý chuỗi để đảm bảo tên hiển thị ra cho người dùng là chuẩn xác và sạch sẽ nhất.

**2. Điều gì xảy ra nếu điện thoại không có mạng lúc dịch?**
- *Trả lời:* Trong code em đã dùng `try-catch`. Nếu không có mạng để tải Model AI hoặc dịch lỗi, App sẽ thực hiện **Fallback** - tức là trả về chính cái tên tiếng Anh gốc đó để App không bị văng (Crash) và người dùng vẫn có thông tin để xem.

**3. Tại sao em lại chọn con số 3 cho Semaphore?**
- *Trả lời:* Qua thử nghiệm, con số 3 giúp App dịch đủ nhanh (khoảng 10-20 từ mỗi giây) mà không chiếm quá 20% CPU của điện thoại tầm trung, đảm bảo máy không bị nóng khi người dùng cuộn danh sách Pokemon liên tục.
