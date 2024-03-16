package src.main.FlappyFish.java;
import javax.swing.*;

//Controls the frame for the game

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Fish");
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyFish flappyFish = new FlappyFish();
        frame.add(flappyFish);
        frame.pack();
        flappyFish.requestFocus();
        frame.setVisible(true);
    }
}
