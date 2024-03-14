import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.geom.AffineTransform;

/*
 * Authors: Joey Montoya and Raghav Vaid
 */

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
    int fishWidth = 40;
    int fishHeight = 40;

    PowerUp powerUp;
    boolean makePipesWider = false;

    class PowerUp {
        int x, y, width, height;
        Image img;
        boolean active = true;
    
        PowerUp(Image img, int startX, int startY, int width, int height) {
            this.img = img;
            this.x = startX;
            this.y = startY;
            this.width = width;
            this.height = height;
        }
    }

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
        new Timer(30000, e -> spawnPowerUp()).start();
    }

    void spawnPowerUp() {
        if (powerUp == null || !powerUp.active) {
            int startX = boardWidth + 100; // Start off-screen
            int startY = random.nextInt(boardHeight - 20); // Random height, but fully visible
            Image powerUpImg = new ImageIcon(getClass().getResource("./powerup.png")).getImage();
            powerUp = new PowerUp(powerUpImg, startX, startY, 20, 20);
        }
    }

    void placePipes() {
        int openingSpace = makePipesWider ? boardHeight / 3 : boardHeight / 4; // Increase gap if power-up is collected
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
       

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
        makePipesWider = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        Graphics2D g2d = (Graphics2D) g;

        double rotationAngle = velocityY * 0.05; 
        
        int cx = fish.x + fish.width / 2;
        int cy = fish.y + fish.height / 2;
    
        AffineTransform old = g2d.getTransform();

        g2d.rotate(rotationAngle, cx, cy);
        
        // draw the fish with rotation applied
        g2d.drawImage(fish.img, fish.x, fish.y, fish.width, fish.height, null);
        
        // Restore original transform
        g2d.setTransform(old);

        // pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score and instructions
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 18)); 
        if (gameOver) {
            g.drawString("Game Over: Score: " + (int) score + ", Highest: " + (int) highestScore, 10, boardHeight / 14);
            g.drawString("Press Space to Play Again", 10, boardHeight / 14 + 30);
        } else if (firstTime) {
            g.drawString("Press Space to Start", 10, boardHeight / 12);
        } else {
            g.drawString("Score: " + (int) score, 10, 30);
        }
        if (powerUp != null && powerUp.active) {
            g.drawImage(powerUp.img, powerUp.x, powerUp.y, powerUp.width, powerUp.height, null);
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
        if (powerUp != null && powerUp.active) {
            powerUp.x += velocityX; // Move the power-up with the pipes
            if (collision(fish, powerUp)) {
                makePipesWider = true; // Activate wider pipes
                powerUp.active = false; // Deactivate the power-up
            } else if (powerUp.x + powerUp.width < 0) {
                powerUp.active = false; // Deactivate if it goes off-screen
            }
        }
    }

    boolean collision(Fish a, Pipe b) {
        return a.x < b.x + b.width &&   // a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   // a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  // a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    // a's bottom left corner passes b's top left corner
    }
    boolean collision(Fish a, PowerUp b) {
        return a.x < b.x + b.width && 
               a.x + a.width > b.x && 
               a.y < b.y + b.height && 
               a.y + a.height > b.y;
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
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
