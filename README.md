# Ứng Dụng Thông Tin Cá Nhân

Ứng dụng Android hiển thị và chỉnh sửa thông tin cá nhân với giao diện đẹp mắt, hỗ trợ chọn ảnh từ thư viện hoặc chụp ảnh bằng camera.

## Tính Năng

### 1. MainActivity - Màn Hình Chính

- Hiển thị avatar của người dùng (ảnh tròn với hiệu ứng đổ bóng)
- Hiển thị thông tin: Tên và Email
- Nút "Chỉnh sửa thông tin" để chuyển sang màn hình chỉnh sửa
- Hiệu ứng fade-in khi load dữ liệu
- Giao diện Material Design với màu tím và xanh lá

### 2. EditActivity - Màn Hình Chỉnh Sửa

- Chỉnh sửa tên và email
- Chọn ảnh từ thư viện ảnh
- Chụp ảnh trực tiếp bằng camera
- Nút lưu thông tin
- Hiệu ứng scale-in khi chọn ảnh
- Kiểm tra quyền truy cập camera và thư viện

### 3. Lưu Trữ Dữ Liệu

- Sử dụng SharedPreferences để lưu trữ dữ liệu cục bộ
- Avatar được lưu dưới dạng Base64
- Dữ liệu tự động được tải lại khi quay về MainActivity

### 4. Hiệu Ứng & Animation

- Slide transitions khi chuyển giữa các Activity
- Fade-in animation khi load dữ liệu
- Scale-in animation khi tương tác với buttons và images
- Smooth transitions với thời gian 300-400ms

## Cấu Trúc Dự Án

```
app/src/main/
├── java/com/example/myapplication/
│   ├── MainActivity.java          # Màn hình hiển thị thông tin
│   └── EditActivity.java          # Màn hình chỉnh sửa thông tin
├── res/
│   ├── layout/
│   │   ├── activity_main.xml      # Layout màn hình chính
│   │   └── activity_edit.xml      # Layout màn hình chỉnh sửa
│   ├── drawable/
│   │   ├── avatar_background.xml   # Background tròn cho avatar
│   │   ├── button_background.xml   # Background cho buttons
│   │   ├── card_background.xml     # Background cho cards
│   │   ├── edit_text_background.xml # Background cho EditText
│   │   ├── ic_person.xml           # Icon người dùng mặc định
│   │   └── ic_camera.xml           # Icon camera
│   ├── anim/
│   │   ├── slide_in_right.xml      # Animation trượt từ phải
│   │   ├── slide_out_left.xml      # Animation trượt sang trái
│   │   ├── slide_in_left.xml       # Animation trượt từ trái
│   │   ├── slide_out_right.xml     # Animation trượt sang phải
│   │   ├── fade_in.xml             # Animation fade-in
│   │   └── scale_in.xml            # Animation scale-in
│   ├── values/
│   │   ├── colors.xml              # Bảng màu
│   │   └── strings.xml             # Chuỗi văn bản
│   └── xml/
│       └── file_paths.xml          # Cấu hình FileProvider
└── AndroidManifest.xml             # Cấu hình ứng dụng
```

## Quyền (Permissions)

Ứng dụng yêu cầu các quyền sau:

- `CAMERA` - Để chụp ảnh
- `READ_EXTERNAL_STORAGE` (Android 12 trở xuống) - Để đọc ảnh từ thư viện
- `READ_MEDIA_IMAGES` (Android 13+) - Để đọc ảnh từ thư viện

## Giao Diện

### Bảng Màu

- **Purple Primary**: `#6A4C9C` - Màu chính (buttons)
- **Purple Dark**: `#512B81` - Màu tối
- **Purple Light**: `#B39DDB` - Màu sáng
- **Green Accent**: `#4CAF50` - Màu nhấn (avatar background)
- **Background Light**: `#F8F5FF` - Màu nền
- **Card Background**: `#FFFFFF` - Màu nền card

### Fonts & Typography

- Tiêu đề: 24sp, Bold
- Label: 16sp, Bold
- Nội dung: 18sp, Normal
- Button: 16sp, Bold

## Yêu Cầu Hệ Thống

- Android SDK 24+ (Android 7.0 Nougat trở lên)
- Target SDK 36
- Java 11
- Gradle 8.13.0

## Cách Build & Chạy

### Sử dụng Android Studio

1. Mở project trong Android Studio
2. Đợi Gradle sync hoàn tất
3. Chọn thiết bị (emulator hoặc thiết bị thật)
4. Nhấn Run (Shift+F10)

### Sử dụng Command Line

```bash
# Build APK
./gradlew assembleDebug

# Install APK
./gradlew installDebug

# Build và chạy
./gradlew installDebug
```

## Cách Sử Dụng

1. **Xem Thông Tin**: Mở ứng dụng để xem thông tin cá nhân hiện tại
2. **Chỉnh Sửa**: Nhấn nút "Chỉnh sửa thông tin"
3. **Đổi Avatar**:
   - Nhấn "Chọn ảnh"
   - Chọn "Thư viện" để chọn ảnh có sẵn
   - Hoặc chọn "Chụp ảnh" để chụp ảnh mới
4. **Cập Nhật Thông Tin**: Nhập tên và email mới
5. **Lưu**: Nhấn "Lưu thông tin" để lưu lại
6. **Quay Lại**: Thông tin sẽ tự động hiển thị trên màn hình chính

## Đặc Điểm Kỹ Thuật

### MainActivity

- Load dữ liệu từ SharedPreferences
- Hiển thị avatar dưới dạng Bitmap (convert từ Base64)
- Animation fade-in khi load
- Intent transition với custom animations

### EditActivity

- Activity Result API để xử lý chọn ảnh và camera
- Runtime permissions cho Camera và Storage
- Image optimization: resize ảnh về 500x500 trước khi lưu
- Base64 encoding để lưu ảnh vào SharedPreferences
- AlertDialog để chọn nguồn ảnh (Gallery/Camera)
- Validation input trước khi lưu

## Đánh Giá Theo Tiêu Chí

| Tiêu Chí               | Mô Tả                                                                            | Điểm    |
| ---------------------- | -------------------------------------------------------------------------------- | ------- |
| Giao diện & Layout     | Bố cục Material Design, rõ ràng, đẹp với màu tím/xanh, cards có bo góc và shadow | 0.5     |
| Xử lý Intent & sự kiện | Chuyển Activity với animation, chọn ảnh từ gallery và camera, xử lý permissions  | 1.0     |
| Lưu dữ liệu            | SharedPreferences lưu tên, email, avatar (Base64), tự động reload khi quay lại   | 0.5     |
| Sáng tạo & hiệu ứng    | 6 animations (slide, fade, scale), màu sắc cá nhân hóa, transitions mượt mà      | 0.5     |
| **Tổng cộng**          |                                                                                  | **2.5** |

## Công Nghệ Sử Dụng

- **Language**: Java
- **UI**: XML Layouts với Material Design Components
- **Storage**: SharedPreferences
- **Image Handling**: Bitmap, Base64 encoding
- **Permissions**: Activity Result API
- **Animations**: XML Animation Resources
- **Architecture**: Simple Activity-based architecture

## Tác Giả

Bài tập giữa kỳ - Lập trình Android
