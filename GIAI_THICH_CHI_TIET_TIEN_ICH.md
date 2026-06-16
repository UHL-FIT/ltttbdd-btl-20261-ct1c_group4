# GIẢI THÍCH CHI TIẾT TỪNG DÒNG CODE: TRANSLATOR & UTILS

Tài liệu này phân tích ý nghĩa của mọi dòng code trong bộ công cụ dịch thuật và tiện ích dữ liệu của ứng dụng Pokedex.

---

## 1. Tệp TranslatorManager.kt (Xử lý dịch thuật AI)

Tệp này sử dụng thư viện **Google ML Kit** để dịch dữ liệu từ tiếng Anh sang tiếng Việt ngay trên thiết bị.

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `object TranslatorManager` | Khai báo Singleton. Đảm bảo toàn App chỉ có 1 bộ máy dịch, tránh khởi tạo lãng phí RAM. |
| `private val translationCache = ConcurrentHashMap<String, String>()` | **Bộ nhớ đệm hoàn tất**: Lưu các từ đã dịch xong. Dùng `ConcurrentHashMap` để an toàn khi nhiều màn hình gọi dịch cùng lúc (Thread-safe). |
| `private val pendingTranslations = ConcurrentHashMap<String, Deferred<String>>()` | **Bộ nhớ đệm đang chờ**: Lưu các từ đang trong quá trình dịch. Tránh việc gọi AI dịch 1 từ 2 lần cùng lúc. |
| `private val semaphore = Semaphore(3)` | **Chốt chặn tài nguyên**: Chỉ cho phép 3 tiến trình dịch chạy song song. Giúp CPU không bị quá tải làm nóng máy. |
| `private val options = TranslatorOptions.Builder()...` | Thiết lập cấu hình: Nguồn là `ENGLISH`, đích là `VIETNAMESE`. |
| `private val translator: Translator = Translation.getClient(options)` | Khởi tạo đối tượng `translator` thực tế để thực hiện lệnh dịch. |
| `suspend fun translate(text: String): String = coroutineScope { ... }` | Hàm chính để dịch. `suspend` giúp chạy ngầm. `coroutineScope` quản lý các tác vụ bên trong. |
| `if (text.isBlank()) return@coroutineScope text` | Kiểm tra nhanh: Nếu văn bản rỗng thì trả về luôn, không tốn tài nguyên dịch. |
| `translationCache[text]?.let { return@coroutineScope it }` | Nếu từ này đã dịch rồi (có trong cache), trả về kết quả ngay lập tức (mất 0.001 giây). |
| `pendingTranslations.computeIfAbsent(text) { ... }` | Nếu chưa ai dịch, kiểm tra xem có ai đang dịch chưa. Nếu chưa thì mới bắt đầu tiến trình dịch. |
| `async { ... }` | Mở một tác vụ chạy bất đồng bộ (luồng phụ) để không làm đơ giao diện người dùng. |
| `semaphore.withPermit { ... }` | Xin "giấy phép". Tác vụ phải đợi đến khi có chỗ trống trong 3 lượt cho phép mới được chạy. |
| `ensureModelDownloaded()` | Gọi hàm kiểm tra xem gói ngôn ngữ Việt Nam đã tải về máy chưa. |
| `val result = translator.translate(text).await()` | **Lệnh thực thi chính**: Gọi AI dịch và dùng `.await()` để đợi kết quả trả về. |
| `translationCache[text] = result` | Lưu kết quả mới vào bộ nhớ đệm để lần sau dùng lại ngay. |
| `deferred.await()` | Trả kết quả cuối cùng cho UI. |

---

## 2. Tệp Pokemon.kt (Logic định dạng trong Model)

Các dòng code này giúp biến dữ liệu thô thành thông tin hiển thị đẹp.

| Dòng code | Giải thích chi tiết công dụng |
|:---|:---|
| `val formattedId: String get() { ... }` | Biến định dạng ID. Tự động chạy mỗi khi UI cần hiển thị số thứ tự Pokemon. |
| `val baseId = id.split(".")[0]` | Tách ID. Ví dụ: ID "6.1" (Mega Charizard) sẽ được lấy phần nguyên là "6". |
| `.padStart(4, '0')` | Thêm số 0 vào trước. Biến số `1` thành `0001`, giúp danh sách trông đều đặn, chuyên nghiệp. |
| `val baseName: String by lazy { ... }` | Tìm tên gốc. `by lazy` giúp tiết kiệm CPU (chỉ tính toán 1 lần duy nhất khi cần). |
| `.substringBefore(" Mega")` | Loại bỏ hậu tố " Mega". (Ví dụ: "Charizard Mega" -> "Charizard"). |
| `.substringBefore(" Alola")` | Tương tự, loại bỏ tên vùng miền để App biết các con này cùng một loài. |

---

## 3. Câu hỏi bảo vệ trọng tâm (Line-by-line)

**1. Tại sao em dùng `ConcurrentHashMap` mà không dùng `HashMap`?**
- *Trả lời:* Vì App của em đa luồng (Coroutines). Nếu 2 màn hình cùng ghi dữ liệu vào `HashMap` cùng lúc sẽ gây lỗi Crash. `ConcurrentHashMap` ngăn chặn xung đột này.

**2. Tại sao lại cần `Semaphore(3)`? Số 3 có ý nghĩa gì?**
- *Trả lời:* Google ML Kit là một mô hình AI thu nhỏ chạy trên điện thoại. Nó tốn rất nhiều RAM. Con số 3 là "điểm ngọt" giúp App vừa dịch nhanh vừa không làm máy bị nóng hay tràn bộ nhớ.

**3. Tại sao em lại dùng `by lazy` cho biến `baseName`?**
- *Trả lời:* Để tối ưu hiệu năng. Tên gốc chỉ cần thiết khi người dùng vào trang "Xây dựng đội hình" để kiểm tra trùng lặp. Nếu họ chỉ xem danh sách, App sẽ không tốn công tính toán chuỗi này, giúp App chạy mượt hơn.

**4. Cơ chế "Fallback" trong code này là gì?**
- *Trả lời:* Trong khối `catch`, em trả về chính cái `text` gốc (tiếng Anh). Điều này đảm bảo nếu AI lỗi hoặc không có mạng, người dùng vẫn thấy tên Pokemon (tiếng Anh) thay vì thấy App bị lỗi.
