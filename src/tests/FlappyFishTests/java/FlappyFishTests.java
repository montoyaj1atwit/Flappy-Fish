package src.tests.FlappyFishTests.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.main.FlappyFish.java.FlappyFish;
import src.main.FlappyFish.java.FlappyFish.Fish;
import src.main.FlappyFish.java.FlappyFish.Pipe;
import src.main.FlappyFish.java.FlappyFish.PowerUp;

import static org.junit.jupiter.api.Assertions.*;

public class FlappyFishTests {
    private FlappyFish game;
    private Fish testFish;
    private PowerUp testPowerUp;
    private Pipe testPipe;

    @BeforeEach
    public void setUp() {
        game = new FlappyFish();
        // You would need to adjust access modifiers in FlappyFish to make this work
        testFish = game.new Fish(null); // Assuming null can be passed for the image for testing
        testPipe = game.new Pipe(null); // Assuming null can be passed for the image for testing
        testPowerUp = game.new PowerUp(null, 100, 100, 30, 30); // Assuming null can be passed for the image for testing
    }

    @Test
    public void testInitialGameState() {
        assertFalse(game.gameOver, "Game should not be over initially");
        assertFalse(game.gameStarted, "Game should not start initially");
        assertEquals(0, game.score, "Initial score should be 0");
    }

    @Test
    public void testCollisionWithPipe() {
        // Set positions to simulate a collision
        testFish.x = testPipe.x - testFish.width;
        testFish.y = testPipe.y - testFish.height;
        testPipe.width = 100;
        testPipe.height = 100;

        assertTrue(game.collision(testFish, testPipe), "There should be a collision with the pipe.");
    }

    @Test
    public void testNoCollisionWithPipe() {
        // Set positions to ensure no collision
        testFish.x = testPipe.x + testPipe.width + 5;
        testFish.y = testPipe.y + testPipe.height + 5;

        assertFalse(game.collision(testFish, testPipe), "There should be no collision with the pipe.");
    }

    @Test
    public void testCollisionWithPowerUp() {
        // Set positions to simulate a collision
        testFish.x = testPowerUp.x - testFish.width;
        testFish.y = testPowerUp.y - testFish.height;

        assertTrue(game.collision(testFish, testPowerUp), "There should be a collision with the power-up.");
    }

    @Test
    public void testNoCollisionWithPowerUp() {
        // Set positions to ensure no collision
        testFish.x = testPowerUp.x + testPowerUp.width + 5;
        testFish.y = testPowerUp.y + testPowerUp.height + 5;

        assertFalse(game.collision(testFish, testPowerUp), "There should be no collision with the power-up.");
    }

    // More tests can be added to cover other aspects of the game
}
