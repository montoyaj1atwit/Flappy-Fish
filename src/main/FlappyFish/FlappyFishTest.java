package src.main.FlappyFish;

import src.main.FlappyFish.FlappyFish;
import src.main.FlappyFish.FlappyFish.Fish;
import src.main.FlappyFish.FlappyFish.Pipe;
import src.main.FlappyFish.FlappyFish.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class FlappyFishTest {
    private FlappyFish game;

    @BeforeEach
    public void setUp() {
        game = new FlappyFish();
        // Initialize the game components if needed
    }

    @Test
    public void testFishCreation() {
        assertNotNull(game.fish, "Fish should be created");
    }

    @Test
    public void testInitialGameState() {
        assertFalse(game.gameOver, "Game should not be over initially");
        assertFalse(game.gameStarted, "Game should not be started initially");
        assertEquals(0, game.score, "Initial score should be zero");
    }

    @Test
    public void testPipePlacement() {
        // Call game.placePipes() directly if it's accessible
        game.placePipes();
        assertFalse(game.pipes.isEmpty(), "Pipes should be placed after calling placePipes");
    }

    @Test
    public void testCollisionWithPipe() {
        game.fish.x = game.pipeX;
        game.fish.y = game.pipeY;

        Pipe testPipe = game.new Pipe(null); // Pass null for the image, since we are testing logic
        testPipe.x = game.fish.x;
        testPipe.y = game.fish.y;

        assertTrue(game.collision(game.fish, testPipe), "Collision should be detected");
    }

    @Test
    public void testPowerUpCollision() {
        game.powerUp = game.new PowerUp(null, game.fish.x, game.fish.y, game.fishWidth, game.fishHeight); // Pass null for the image

        assertTrue(game.collision(game.fish, game.powerUp), "Collision with powerUp should be detected");
    }

    @Test
    public void testScoreIncrementAfterPassingPipe() {
        Pipe testPipe = game.new Pipe(null);
        testPipe.passed = false;
        game.pipes.add(testPipe);
        game.fish.x = testPipe.x + testPipe.width + 1; // Simulate fish has passed the pipe

        game.move(); // Trigger the move logic which should update the score

        assertEquals(0.5, game.score, "Score should be incremented after passing a pipe");
    }

    // Add more tests to cover different scenarios...

    @AfterEach
    public void tearDown() {
        game = null;
    }
}
