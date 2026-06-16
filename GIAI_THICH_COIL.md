# GIẢI THÍCH XỬ LÝ HÌNH ẢNH (COIL)

Tài liệu này giải thích cách App hiển thị hàng nghìn bức ảnh Pokemon một cách mượt mà bằng thư viện Coil.

## 1. Tại sao dùng Coil?
- Được viết bằng Kotlin Coroutines, cực kỳ nhẹ.
- Tích hợp hoàn hảo với Jetpack Compose qua `AsyncImage`.

## 2. Cấu hình tối ưu (PokedexApplication)
App không dùng cấu hình mặc định mà tùy chỉnh để nhanh hơn:
- **Crossfade(true)**: Hiệu ứng làm mờ ảnh khi tải xong, giúp trải nghiệm nhìn mượt hơn, không bị giật.
- **Memory Cache (25%)**: Giữ ảnh trên RAM. Khi bạn cuộn lên cuộn xuống, ảnh hiện ra ngay lập tức vì không phải tải lại.
- **Disk Cache (2%)**: Lưu ảnh vào bộ nhớ máy. Nếu bạn tắt mạng và mở lại App, ảnh những con đã xem vẫn sẽ hiện lên.

## 3. Luồng hiển thị ảnh (5 bước)
1.  **UI**: Gọi `AsyncImage(model = imageUrl)`.
2.  **Coil Check**: 
    - Kiểm tra trên RAM (Memory Cache).
    - Nếu không có, kiểm tra trong máy (Disk Cache).
3.  **Download**: Nếu cả hai không có, Coil tự động tải từ Internet.
4.  **Transformation**: Tự động thay đổi kích thước ảnh cho khớp với khung hình để tiết kiệm RAM.
5.  **Display**: Vẽ ảnh lên màn hình kèm hiệu ứng chuyển cảnh.

## 4. Câu hỏi bảo vệ trọng tâm
- **Tại sao phải giới hạn Cache?**
  => Để đảm bảo App không chiếm quá nhiều dung lượng của điện thoại người dùng. 2% là con số hợp lý cho một App về thông tin.
- **Placeholder và Error image là gì?**
  => Placeholder là ảnh hiện lên lúc đang tải (ví dụ hình quả cầu PokeBall xoay). Error là ảnh hiện lên khi link bị hỏng hoặc mất mạng.
- **Làm sao để ảnh không bị méo?**
  => Sử dụng thuộc tính `contentScale = ContentScale.Fit` hoặc `Crop` trong Compose để ảnh luôn giữ đúng tỉ lệ.