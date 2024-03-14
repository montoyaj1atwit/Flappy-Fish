import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyFish extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image fishImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // fish class
    int fishX = boardWidth / 8;
    int fishY = boardWidth / 2;
    int fishWidth = 34;
    int fishHeight = 24;

    class Fish {
        int x = fishX;
        int y = fishY;
        int width = fishWidth;
        int height = fishHeight;
        Image img;

        Fish(Image img) {
            this.img = img;
        }
    }

    // pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Fish fish;
    int velocityX = -4; // move pipes to the left speed (simulates fish moving right)
    double velocityY = 0; // move fish up/down speed.
    double gravity = 0.5;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    boolean gameStarted = false; // Indicates if the game has started
    double score = 0;
    double highestScore = 0; // Track the highest score
    boolean firstTime = true; // Indicate if it's the first time playing

    FlappyFish() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappyfishbg.png")).getImage();
        fishImg = new ImageIcon(getClass().getResource("./flappyfish.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        fish = new Fish(fishImg);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted) {
                    placePipes();
                }
            }
        });
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // fish
        g.drawImage(fishImg, fish.x, fish.y, fish.width, fish.height, null);

        // pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score and instructions
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 24)); // Reduced font size
        if (gameOver) {
            g.drawString("Game Over: Score: " + (int) score + ", Highest: " + (int) highestScore, 10, boardHeight / 4);
            g.drawString("Press Space to Play Again", 10, boardHeight / 4 + 30);
        } else if (firstTime) {
            g.drawString("Press Space to Start", 10, boardHeight / 4);
        } else {
            g.drawString("Score: " + (int) score, 10, 30);
        }
    }

    public void move() {
        if (!gameStarted || gameOver) {
            return; // Do not move anything if the game hasn't started or it's over
        }

        // fish
        velocityY += gravity;
        fish.y += velocityY;
        fish.y = Math.max(fish.y, 0); // apply gravity to current fish.y, limit the fish.y to top of the canvas

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && fish.x > pipe.x + pipe.width) {
                score += 0.5; // 0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(fish, pipe)) {
                gameOver = true;
                gameStarted = false;
                highestScore = Math.max(score, highestScore); // Update highest score
                gameLoop.stop();
                placePipeTimer.stop();
            }
        }

        if (fish.y > boardHeight) {
            gameOver = true;
            gameStarted = false;
            highestScore = Math.max(score, highestScore); // Update highest score
            gameLoop.stop();
            placePipeTimer.stop();
        }
    }

    boolean collision(Fish a, Pipe b) {
        return a.x < b.x + b.width &&   // a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   // a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  // a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // restart game by resetting conditions
                fish.y = fishY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameStarted = true; // Start the game
                gameLoop.start();
                placePipeTimer.start();
            } else if (!gameStarted) {
                gameStarted = true; // Start the game
                firstTime = false;
            }
            velocityY = -9; // Jump whether it's the first play or a restart
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Fish");
        FlappyFish game = new FlappyFish();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
