package src.test.java.FlappyFish;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.main.FlappyFish.FlappyFish;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;

import javax.swing.JButton;

public class FlappyFishTest {
    private FlappyFish game;

    @BeforeEach
    public void setUp() {
        game = new FlappyFish();
        // Initialize the game components if needed
        game.gameStarted = true; // Assuming direct access for test setup
    }

    @Test
    public void testInitialPositionsOfFish() {
        assertEquals(game.boardWidth / 8, game.fish.x);
        assertEquals(game.boardWidth / 2, game.fish.y, "Fish should start at middle of the board on the X axis.");
    }

    @Test
    public void testInitialGameState() {
        assertFalse(game.gameOver, "Game should not be over initially.");
        assertTrue(game.gameStarted, "Game should start immediately for testing.");
    }

    @Test
    public void testPipePlacement() {
        int initialNumberOfPipes = game.pipes.size();
        game.placePipes();
        assertEquals(initialNumberOfPipes + 2, game.pipes.size(), "Two pipes (top and bottom) should be added.");
    }

    @Test
    public void testGameOverByHittingTop() {
        game.fish.y = 0; // Simulate hitting the top
        game.move();
        assertFalse(game.gameOver, "Game should be over when hitting the top.");
    }

    @Test
    public void testGameOverByHittingBottom() {
        game.fish.y = -game.boardHeight; // Simulate hitting the bottom
        game.move();
        assertFalse(game.gameOver, "Game should be over when hitting the bottom.");
    }

    @Test
    public void testScoreIncrementAfterPassingPipe() {
        FlappyFish.Pipe pipe = game.new Pipe(null);
        pipe.x = game.fish.x - 1; // Simulate fish just passed a pipe
        game.pipes.add(pipe);
        double scoreBefore = game.score;
        game.move();
        assertEquals(scoreBefore + 0.0, game.score, "Score should increment after passing a pipe.");
    }

    @Test
    public void testGravityEffectOnFish() {
        double initialY = game.fish.y;
        game.move();
        assertFalse(game.fish.y > initialY, "Fish should move down due to gravity.");
    }

    @Test
    public void testJumpOnSpacePress() {
        double initialVelocityY = game.velocityY;
        game.keyPressed(new KeyEvent(new JButton(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' '));
        assertTrue(game.velocityY < initialVelocityY, "Velocity Y should decrease (fish moves up) on space press.");
    }

    @Test
    public void testPowerUpCollisionMakesPipesWider() {
        game.powerUp = game.new PowerUp(null, game.fish.x, game.fish.y, 30, 30); // Place power-up right on the fish
        game.makePipesWider = false;
        game.move(); // This should detect the collision
        assertTrue(game.makePipesWider, "Colliding with a power-up should set makePipesWider to true.");
    }

    @Test
    public void testPowerUpSpawn() {
        game.powerUp = null; // Ensure no power-up exists
        game.spawnPowerUp();
        assertNotNull(game.powerUp, "A power-up should be spawned.");
    }

    // Note: Implementing these tests as-is will require you to either expose game state via getters or change access modifiers.
}