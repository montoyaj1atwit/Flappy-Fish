package src.main.FlappyFish;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Authors: Joey Montoya and Raghav Vaid
 */

public class FlappyFish extends JPanel implements ActionListener, KeyListener {
    
    //Variables needed for game logic
    public int boardWidth = 360;
    public int boardHeight = 640;

    
    public Image backgroundImg;
    public Image fishImg;
    public Image topPipeImg;
    public Image bottomPipeImg;

    
    public int fishX = boardWidth / 8;
    public int fishY = boardWidth / 2;
    public int fishWidth = 40;
    public int fishHeight = 40;

    public PowerUp powerUp;
    public boolean makePipesWider = false;

    public JButton rateGameButton;

    //Powerup compiler
   public class PowerUp {
    public int x, y, width, height;
    public Image img;
    public boolean active = true;
    
        public PowerUp(Image img, int startX, int startY, int width, int height) {
            this.img = img;
            this.x = startX;
            this.y = startY;
            this.width = width;
            this.height = height;
        }
    }

    //Fish complier
   public class Fish {
    public int x = fishX;
    public int y = fishY;
    public int width = fishWidth;
    public int height = fishHeight;
    public Image img;

        public Fish(Image img) {
            this.img = img;
        }
    }

    // Pipe variables
    public int pipeX = boardWidth;
    public int pipeY = 0;
    public int pipeWidth = 64; 
    public int pipeHeight = 512;

    // Pipe complier
  public class Pipe {
    public int x = pipeX;
    public int y = pipeY;
    public int width = pipeWidth;
    public int height = pipeHeight;
    public Image img;
    public boolean passed = false;

        public Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic, controls fish velocity/gravity. These are recommended if you are running on 144HZ+ if you are 60hz run velocityX to -6, and gravity to 0.75
    public Fish fish;
    public int velocityX = -4; 
    public double velocityY = 0; 
    public double gravity = 0.5;

    public ArrayList<Pipe> pipes;
    public Random random = new Random();

    //Variables for game logic
    public Timer gameLoop;
    public Timer placePipeTimer;
    public boolean gameOver = false;
    public boolean gameStarted = false; 
    public double score = 0;
    public double highestScore = 0; 
    public boolean firstTime = true; 

    //Game logic creates all images and stars/ends game and powerup.
    public FlappyFish() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./flappyfishbg.png")).getImage();
        fishImg = new ImageIcon(getClass().getResource("./flappyfish.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        fish = new Fish(fishImg);
        pipes = new ArrayList<Pipe>();

        rateGameButton = new JButton("Rate Game");
        rateGameButton.addActionListener(e -> showRatingDialog());
        rateGameButton.setVisible(false); 
        this.add(rateGameButton);

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


    //Spawns powerup randomly
    public void spawnPowerUp() {
        if (powerUp == null || !powerUp.active) {
            int startX = boardWidth + 100; 
            int startY = random.nextInt(boardHeight - 20); 
            Image powerUpImg = new ImageIcon(getClass().getResource("./powerup.png")).getImage();
            powerUp = new PowerUp(powerUpImg, startX, startY, 30, 30);
        }
    }

    //Places pipes randomly, and will make pipes wider if powerup is touched
    public void placePipes() {
        int openingSpace = makePipesWider ? boardHeight / 3 : boardHeight / 4; 
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
       

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
        makePipesWider = false;
    }

    //Draws all graphics
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Creates background iamge
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // Makes the fish tilt up/down based on velocity
        Graphics2D g2d = (Graphics2D) g;
        double rotationAngle = velocityY * 0.05; 
        int cx = fish.x + fish.width / 2;
        int cy = fish.y + fish.height / 2;
        AffineTransform old = g2d.getTransform();
        g2d.rotate(rotationAngle, cx, cy);
        g2d.drawImage(fish.img, fish.x, fish.y, fish.width, fish.height, null);
        g2d.setTransform(old);

        // Pipes graphics
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score and instructions graphics
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


    //Game's movement system
    public void move() {
        if (!gameStarted || gameOver) {
            return; // Do not move anything if the game hasn't started or it's over
        }

        // Fish movement
        velocityY += gravity;
        fish.y += velocityY;
        fish.y = Math.max(fish.y, 0); 

        // Pipes movement
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            //Increment for score based on the fish passing both pipes
            if (!pipe.passed && fish.x > pipe.x + pipe.width) {
                score += 0.5; 
                pipe.passed = true;
            }

            //Collision for game over
            if (collision(fish, pipe)) {
                gameOver = true;
                gameStarted = false;
                highestScore = Math.max(score, highestScore); 
                gameLoop.stop();
                placePipeTimer.stop();
            }
        }

        //If fish drops below the game then game over
        if (fish.y > boardHeight) {
            gameOver = true;
            gameStarted = false;
            highestScore = Math.max(score, highestScore); 
            gameLoop.stop();
            placePipeTimer.stop();
        }

        //Makes pipe wider if fish touches powerup
        if (powerUp != null && powerUp.active) {
            powerUp.x += velocityX; 
            if (collision(fish, powerUp)) {
                makePipesWider = true;
                powerUp.active = false; 
            } else if (powerUp.x + powerUp.width < 0) {
                powerUp.active = false; 
            }
        }
        //Rate button becomes visible after first death in game
        if (gameOver) {
            rateGameButton.setVisible(true);
        }
    }


    //Detects collision between the fish and the pipe
    public boolean collision(Fish a, Pipe b) {
        return a.x < b.x + b.width &&   
               a.x + a.width > b.x &&   
               a.y < b.y + b.height &&  
               a.y + a.height > b.y;    
    }

    //Detects collision between the fish and the power up
    public boolean collision(Fish a, PowerUp b) {
        return a.x < b.x + b.width && 
               a.x + a.width > b.x && 
               a.y < b.y + b.height && 
               a.y + a.height > b.y;
    }


    //Detects if game starts
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }


    //Starts game if key pressed
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                fish.y = fishY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameStarted = true; 
                gameLoop.start();
                placePipeTimer.start();
            } else if (!gameStarted) {
                gameStarted = true; 
                firstTime = false;
            }
            velocityY = -9; 
        }
        if (!gameStarted) {
            rateGameButton.setVisible(false);
        }
    }

    //Rating system
    private void showRatingDialog() {
        String ratingStr = JOptionPane.showInputDialog(this, "Rate the game (1-5 stars):");
        if (ratingStr != null && !ratingStr.trim().isEmpty()) {
            try {
                int rating = Integer.parseInt(ratingStr);
                if (rating >= 1 && rating <= 5) {
                    saveRatingToFile(rating);
                    rateGameButton.setVisible(false); // Hide the rate button after rating
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid rating between 1 and 5.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        } else {
            rateGameButton.setVisible(false); // Also hide if the user cancels the rating
        }
    }  

    //Saves rating under game_rating.txt
private void saveRatingToFile(int rating) {
    try {
        FileWriter writer = new FileWriter("game_rating.txt", true); // Append mode
        writer.write("Game rating: " + rating + " stars\n");
        writer.close();
        JOptionPane.showMessageDialog(this, "Thank you for rating!");
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}

    //Checks if key is pressed
    @Override
    public void keyTyped(KeyEvent e) {}
    //Checks if key is released
    @Override
    public void keyReleased(KeyEvent e) {}

    //Runs program
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
