# ArkanoidProject
[Class Diagram](https://drive.google.com/file/d/1qFvk1plaTcm8dhSfcbZ_RjjshwER9DKc/view?usp=sharing)
[Documentation](https://dear-verbena-8f4.notion.site/ArkanoidProject-2870bd3adf9e801bb8aac5d13e2d6270)
- **Gameplay core nâng cấp**: Paddle điều khiển nhạy, ball bounce theo vị trí va chạm.
- **Combo & milestone scoring**: Combo càng dài, điểm càng nhân; các mốc điểm (milestone) hiển thị bằng floating banner.
- **Đa dạng gạch & power-up**: Gạch thường, gạch bền (2 HP), gạch power-up; mỗi viên power-up có thể mở rộng paddle, tạo magnet, nhân số bóng, hoặc cộng mạng.
- **Endless Mode**: Hệ thống spawn hàng gạch mới theo thời gian, đẩy độ khó tăng dần.
- **State-driven UX**: Menu chính, hướng dẫn, bảng điểm, settings, pause, game over/game won – tất cả được điều phối qua GameState & transition animation.
- **Âm thanh toàn diện**: BGM riêng cho menu, gameplay và game over; SFX cho paddle hit, brick break, power-up, thao tác UI. Master & SFX volume có thể tinh chỉnh độc lập.
- **Floating HUD & feedback**: Combo text, điểm thưởng, cảnh báo “Life Lost!”… giúp người chơi nắm bắt trạng thái trận đấu.
- **Highscore persistence**: Điểm số cao được lưu vào `highscores.txt`, cho phép ghi danh bằng tên sau mỗi trận.
- **Asset management tiện lợi**: `AssetManager` và `SoundManager` tự động tải hình/sound trong `/images` và `/sounds`, hỗ trợ mở rộng asset nhanh chóng.
- **Mã nguồn module hóa**: Controller–Core–Model–View tách bạch, dễ bảo trì và mở rộng; tương thích với Java 17 & JavaFX 21 qua Maven.
