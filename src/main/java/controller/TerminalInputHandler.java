package controller;

import model.Paddle;

import java.io.IOException;

public class TerminalInputHandler implements Runnable {

    private final Paddle paddle;

    public TerminalInputHandler(Paddle paddle) {
        this.paddle = paddle;
    }

    @Override
    public void run() {
        try {

            // Vòng lặp vô tận để nghe
            while (true) {
                // Hàm blocking, chờ đến khi có một phím được bấm
                // Chạy trên đa luồng riêng nên không ảnh hưởng đến game
                int input = System.in.read();

                System.out.println("Nhận phím: " + input);

                switch (input) {
                    case 'a':
                    case 'A':
                        paddle.moveLeft();
                        break;
                    case 'd':
                    case 'D':
                        paddle.moveRight();
                        break;
                    case 'q':
                    case 'Q':
                        System.exit(0);
                        break;
                }
            }
        } catch (IOException e) {
            // Xử lí nếu có lỗi khi đọc input
            e.printStackTrace();
        }
    }
}
