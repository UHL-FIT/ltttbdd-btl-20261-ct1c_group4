# GIẢI THÍCH CHI TIẾT TỪNG DÒNG CODE: TRANSLATOR & UTILS

Tài liệu này giải thích công dụng của **từng dòng code** trong các tệp thuộc phần Tiện ích (Utils) và Dịch thuật (Translator).

---

## 1. Tệp `TranslatorManager.kt`

| Dòng | Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|:---|
| 1 | `package com.example.pokedex.util` | Khai báo vị trí của tệp trong cấu trúc thư mục dự án. |
| 17 | `object TranslatorManager` | Sử dụng `object` (Singleton) để đảm bảo chỉ có duy nhất một bộ máy dịch chạy trong toàn App. |
| 19 | `private val translationCache = ...` | Tạo một bản đồ (Map) trên RAM để lưu các từ đã dịch. `ConcurrentHashMap` giúp tránh lỗi khi nhiều màn hình cùng gọi dịch một lúc. |
| 20 | `private val pendingTranslations = ...` | Lưu các từ "đang được dịch". Nếu 2 chỗ cùng gọi dịch chữ "Fire", dòng này giúp App chỉ thực hiện 1 lần duy nhất. |
| 23 | `private val semaphore = Semaphore(3)` | **Chốt chặn tài nguyên**: Giới hạn tối đa 3 tiến trình dịch cùng lúc để tránh làm nóng máy hoặc treo ứng dụng. |
| 25-29 | `private val options = ...` | Thiết lập cấu hình dịch: Nguồn là tiếng Anh (`ENGLISH`), đích là tiếng Việt (`VIETNAMESE`). |
| 30 | `private val translator = ...` | Khởi tạo đối tượng thực thi dịch từ cấu hình trên thông qua thư viện Google ML Kit. |
| 33 | `suspend fun translate(text: String)` | Hàm dịch chính. `suspend` cho phép tạm dừng luồng để chờ kết quả mà không làm đơ giao diện. |
| 34 | `if (text.isBlank()) return ...` | Nếu văn bản truyền vào là rỗng thì trả về ngay, không tốn công xử lý. |
| 37 | `translationCache[text]?.let { ... }` | Kiểm tra bộ nhớ đệm. Nếu từ này đã dịch rồi thì lấy ra dùng luôn (tốc độ cực nhanh). |
| 40 | `pendingTranslations.computeIfAbsent` | Nếu từ này chưa có ai dịch, thì bắt đầu đăng ký một tác vụ dịch mới. |
| 41 | `async { ... }` | Chạy tác vụ dịch ở luồng phụ (bất đồng bộ). |
| 43 | `semaphore.withPermit { ... }` | Xin "giấy phép" từ Semaphore. Nếu đã có 3 máy đang dịch, máy thứ 4 phải đứng đợi ở đây. |
| 44 | `ensureModelDownloaded()` | Gọi hàm kiểm tra và tải gói ngôn ngữ Việt Nam về máy (nếu chưa có). |
| 45 | `val result = ...translate(text).await()` | Lệnh thực thi dịch và dùng `.await()` để chờ kết quả trả về từ AI. |
| 46 | `translationCache[text] = result` | Sau khi có kết quả, lưu ngay vào Cache để lần sau không cần dịch lại từ này. |
| 53 | `pendingTranslations.remove(text)` | Xóa khỏi danh sách "đang chờ" vì đã hoàn thành. |
| 58 | `deferred.await()` | Trả kết quả cuối cùng cho nơi gọi hàm. |
| 64-66 | `val conditions = ...` | Thiết lập điều kiện tải gói ngôn ngữ (ví dụ: chỉ tải khi có Wifi để tiết kiệm 4G). |
| 68 | `translator.downloadModelIfNeeded` | Lệnh tải gói dữ liệu ngôn ngữ từ máy chủ Google về điện thoại. |

---

## 2. Các hàm Logic trong `Pokemon.kt`

| Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|
| `val total: Int get() = ...` | Biến tính toán tổng chỉ số (BST). Tự động cộng các chỉ số HP, Atk, Def... mỗi khi được gọi. |
| `val baseId = id.split(".")[0]` | Lấy phần số nguyên của ID. Ví dụ "6.1" thành "6". Giúp gộp các dạng Mega vào cùng một gốc. |
| `.padStart(4, '0')` | Biến số `1` thành `0001`. Giúp hiển thị ID Pokemon đồng nhất và chuyên nghiệp (ví dụ: #0001, #0025). |
| `val baseName: String by lazy { ... }` | Tìm tên gốc của Pokemon. `by lazy` giúp tiết kiệm tài nguyên (chỉ tính toán khi thực sự cần dùng). |
| `.substringBefore(" Mega")` | Cắt bỏ chữ " Mega" khỏi tên (Ví dụ: "Charizard Mega" -> "Charizard"). Dùng để kiểm tra trùng loài trong đội hình. |

---

## 3. Tệp chuyển đổi dữ liệu (`Converters`)

Nằm cuối tệp `Pokemon.kt`, dùng để giúp Database Room lưu trữ danh sách.

| Đoạn Code | Giải thích ý nghĩa kỹ thuật |
|:---|:---|
| `@TypeConverter` | Đánh dấu cho Room biết đây là hàm hỗ trợ chuyển đổi kiểu dữ liệu. |
| `gson.toJson(value)` | Biến một danh sách (List) thành chuỗi văn bản JSON để lưu vào ổ cứng điện thoại. |
| `gson.fromJson(value, listType)` | Đọc chuỗi văn bản JSON từ ổ cứng và biến nó ngược lại thành danh sách đối tượng để App hiển thị. |

---

## 4. Tại sao code lại viết như vậy? (Câu hỏi bảo vệ)

1. **Tại sao dùng `ConcurrentHashMap`?**
   - => Vì việc dịch diễn ra ở luồng phụ. `ConcurrentHashMap` đảm bảo dữ liệu không bị hỏng khi nhiều luồng cùng ghi vào một lúc.
2. **Tại sao phải dùng `Semaphore(3)`?**
   - => Google ML Kit tiêu tốn rất nhiều tài nguyên. Giới hạn 3 lượt giúp App vừa dịch nhanh vừa không làm điện thoại bị quá nhiệt.
3. **Tại sao dùng `by lazy` cho tên gốc (baseName)?**
   - => Tên gốc chỉ cần khi vào trang Build đội hình. `by lazy` giúp App không tốn công tính toán chuỗi này ở các màn hình khác, giúp App chạy mượt hơn.
