# GIẢI MÃ CHI TIẾT TỪNG DÒNG CODE: TRANSLATOR & UTILS

Tài liệu này giải thích công dụng của **từng dòng code** trong các tệp thuộc phần Tiện ích (Utils) và Dịch thuật (Translator).

---

## 1. Tệp `TranslatorManager.kt`

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|:---|
| 17 | `object TranslatorManager` | Sử dụng `object` (Singleton) để đảm bảo chỉ có duy nhất một bộ máy dịch chạy trong toàn App. |
| 19 | `private val translationCache = ...` | Tạo một bản đồ (Map) trên RAM để lưu các từ đã dịch. `ConcurrentHashMap` giúp tránh lỗi khi nhiều màn hình cùng gọi dịch một lúc. |
| 20 | `private val pendingTranslations = ...` | Lưu các từ "đang được dịch". Nếu 2 chỗ cùng gọi dịch chữ "Fire", dòng này giúp App chỉ thực hiện 1 lần duy nhất. |
| 23 | `private val semaphore = Semaphore(3)` | **Chốt chặn tài nguyên**: Giới hạn tối đa 3 tiến trình dịch cùng lúc để tránh làm nóng máy hoặc treo ứng dụng. |
| 25-29 | `private val options = ...` | Thiết lập cấu hình dịch: Nguồn là tiếng Anh (`ENGLISH`), đích là tiếng Việt (`VIETNAMESE`). |
| 30 | `private val translator = ...` | Khởi tạo đối tượng thực thi dịch từ cấu hình trên thông qua thư viện Google ML Kit. |
| 32 | `suspend fun translate(text: String): String` | Hàm dịch chính. `suspend` cho phép tạm dừng luồng để chờ kết quả mà không làm đơ giao diện. |
| 33 | `if (text.isBlank()) return text` | Nếu văn bản truyền vào là rỗng thì trả về ngay, không tốn công xử lý của AI. |
| 36 | `translationCache[text]?.let { return it }` | Kiểm tra bộ nhớ đệm. Nếu từ này đã dịch rồi thì lấy ra dùng luôn (tốc độ xử lý ~0ms). |
| 39 | `pendingTranslations.computeIfAbsent(text) { ... }` | Nếu từ này chưa có ai dịch, thì đăng ký một tác vụ dịch mới để tránh trùng lặp. |
| 40 | `viewModelScope.async(Dispatchers.IO) { ... }` | Chạy tác vụ dịch ở luồng phụ (IO) để không ảnh hưởng đến luồng giao diện chính. |
| 42 | `semaphore.withPermit { ... }` | Xin "giấy phép" từ Semaphore. Giới hạn 3 máy dịch cùng lúc để tránh làm nóng CPU. |
| 43 | `ensureModelDownloaded()` | Gọi hàm kiểm tra và tải gói ngôn ngữ Việt Nam về máy (nếu chưa có). |
| 44 | `val result = translator.translate(text).await()` | Lệnh thực thi dịch AI và dùng `.await()` để chờ kết quả trả về. |
| 45 | `translationCache[text] = result` | Sau khi có kết quả, lưu ngay vào Cache để lần sau không cần dịch lại. |
| 52 | `pendingTranslations.remove(text)` | Xóa khỏi danh sách "đang chờ" sau khi đã hoàn thành hoặc gặp lỗi. |
| 60 | `private suspend fun ensureModelDownloaded()` | Hàm nội bộ quản lý việc tải dữ liệu AI (khoảng 30MB) từ Google. |
| 62-65 | `val conditions = DownloadConditions.Builder()...` | Thiết lập điều kiện: Yêu cầu Wifi để tải gói ngôn ngữ, tránh tốn dung lượng 4G. |
| 67 | `translator.downloadModelIfNeeded(conditions).await()` | Thực hiện tải gói ngôn ngữ và đợi cho đến khi hoàn tất mới cho phép dịch. |

---

## 2. Các hàm Logic trong `Pokemon.kt`

| Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|
| `val total: Int get() = hp + attack + defense + spAtk + spDef + speed` | Biến tính toán tổng chỉ số (BST). Tự động cộng dồn 6 chỉ số cơ bản mỗi khi truy cập. |
| `val baseId = id.split(".")[0]` | Tách ID để lấy phần số nguyên (VD: "6.1" thành "6"). Giúp gộp các dạng biến thể vào cùng gốc. |
| `.padStart(4, '0')` | Định dạng số thành chuỗi 4 ký tự (VD: `1` thành `0001`). Dùng để hiển thị mã Pokemon chuyên nghiệp (#0001). |
| `val baseName: String by lazy { ... }` | Xác định tên gốc bằng cách cắt bỏ các hậu tố (Mega, Alola...). `by lazy` giúp tối ưu bộ nhớ. |
| `.substringBefore(" Mega")` | Lệnh cắt chuỗi: Lấy phần văn bản đứng trước chữ " Mega" để nhận diện loài Pokemon gốc. |

---

## 3. Tệp chuyển đổi dữ liệu (`Converters`)

Nằm cuối tệp `Pokemon.kt`, dùng để giúp Database Room lưu trữ danh sách.

| Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|
| `@TypeConverter` | Đánh dấu cho Room biết đây là hàm hỗ trợ chuyển đổi kiểu dữ liệu. |
| `gson.toJson(value)` | Biến một danh sách (List) thành chuỗi văn bản JSON để lưu vào ổ cứng điện thoại. |
| `gson.fromJson(value, listType)` | Đọc chuỗi văn bản JSON từ ổ cứng và biến nó ngược lại thành danh sách đối tượng để App hiển thị. |
