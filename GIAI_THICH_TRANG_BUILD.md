# GIẢI THÍCH TRANG XÂY DỰNG ĐỘI HÌNH (BUILD SCREEN)

Tài liệu này giải thích logic phức tạp nhất của App: Kiểm tra xung đột và lưu trữ đội hình.

## 1. Logic kiểm tra xung đột (Dòng 130-150 trong BuildViewModel)
App áp dụng các quy tắc giống như trong game Pokemon thực tế:
- **Species Rule**: Không cho phép chọn 2 Pokemon cùng loài (ví dụ: không được có 2 con Pikachu trong 1 team).
- **Evolution Line Rule**: Kiểm tra `baseId`. Nếu đã có con trong cùng nhánh tiến hóa (ví dụ: đã có Raichu thì không được thêm Pikachu), App sẽ báo lỗi.
- **Mega Rule**: Mỗi đội chỉ được phép có tối đa 1 Pokemon ở dạng Mega.

## 2. Luồng vận hành (5 bước)

1.  **View (UI)**: Người dùng nhấn nút "+" để chọn Pokemon.
2.  **UI Config**: Danh sách Pokemon hiện ra. Các con vi phạm quy tắc trên sẽ bị làm mờ hoặc hiện cảnh báo.
3.  **Action**: Người dùng chọn 1 con hợp lệ -> Gọi `onPokemonSelected()`.
4.  **ViewModel (Processing)**: 
    - Kiểm tra `selectedPokemon.size < 6`.
    - Kiểm tra logic xung đột (Base ID).
    - Cập nhật danh sách `_selectedPokemon`.
5.  **Recomposition**: UI vẽ lại các ô Pokemon đã chọn, nút "Save Team" hiện lên.

## 3. Lưu trữ (JSON Serialization)
- **Mục đích**: Lưu đội hình vào bộ nhớ máy (SharedPreferences).
- **Cách làm**: Chuyển danh sách Pokemon thành chuỗi JSON bằng thư viện **Gson**.
- **Lợi ích**: Dễ dàng lưu và đọc lại các cấu trúc dữ liệu phức tạp (List trong List).

## 4. Câu hỏi bảo vệ trọng tâm
- **Làm sao nhận biết được 2 Pokemon cùng nhánh tiến hóa?**
  => Dựa vào trường `speciesId` hoặc `baseId` trong Model. Nếu chúng giống nhau, nghĩa là cùng một gốc.
- **Tại sao dùng JSON để lưu thay vì Database?**
  => Đội hình là dữ liệu nhỏ, cấu trúc linh hoạt. Dùng SharedPreferences + JSON giúp code ngắn gọn và tốc độ truy xuất cực nhanh.
- **Làm thế nào để xóa Pokemon khỏi đội?**
  => UI gọi hàm `removePokemon(id)`. ViewModel sẽ dùng `filterNot` để lọc danh sách và cập nhật lại State.