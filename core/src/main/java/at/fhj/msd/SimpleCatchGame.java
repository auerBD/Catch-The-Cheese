package at.fhj.msd;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter; // Base class for LibGDX applications
import com.badlogic.gdx.Gdx; // Access to graphics, input, files, etc.
import com.badlogic.gdx.Input; // Keyboard input handling
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture; // OpenGL functions (for clearing screen)
import com.badlogic.gdx.graphics.g2d.BitmapFont; // For loading images
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // For drawing text
import com.badlogic.gdx.math.Circle; // Efficient drawing of textures
import com.badlogic.gdx.math.Rectangle; // Circle shape (used for falling drops)
import com.badlogic.gdx.utils.Array; // Rectangle shape (used for player + collision)
import com.badlogic.gdx.utils.TimeUtils; // LibGDX dynamic array

/**
 * Main game class
 */
public class SimpleCatchGame extends ApplicationAdapter {

    Rectangle player; // Player hitbox (position + size)
    Array<Circle> drops; // List of falling objects (cheese)

    long lastDropTime; // Time when last drop was spawned
    int score; // Player score
    int lives = 3; // Player lives
    boolean gameOver = false; // Game state flag
    boolean gameOverPlayed = false;

    SpriteBatch batch; // Used to draw textures
    BitmapFont font; // Used to draw text
    Texture mouseTexture; // Player image
    Texture cheeseTexture; // Drop image
    Texture background; // Background image
    Sound catchSound;
    Sound gameoversound;

    @Override
    public void create() {
        // Create drawing tools
        batch = new SpriteBatch();
        font = new BitmapFont();

        // Load textures from assets folder
        background = new Texture(Gdx.files.internal("kitchen.jpg"));
        mouseTexture = new Texture(Gdx.files.internal("mouse.png"));
        cheeseTexture = new Texture(Gdx.files.internal("cheese.png"));
        catchSound = Gdx.audio.newSound(Gdx.files.internal("catch.ogg"));
        gameoversound = Gdx.audio.newSound(Gdx.files.internal("gameover.wav"));

        // Create player rectangle (hitbox)
        player = new Rectangle();

        // Set player size FIRST (important!)
        player.width = 128;
        player.height = 128;

        // Place player horizontally centered
        // screenWidth / 2 = middle of screen
        // minus half player width -> centers the sprite
        player.x = Gdx.graphics.getWidth() / 2f - player.width / 2f;

        // Fixed vertical position near bottom
        player.y = 20;

        // Initialize drop list
        drops = new Array<>();

        // Spawn first drop
        spawnDrop();
    }

    @Override
    public void render() {
        // Only update game logic if game is not over
        if (!gameOver) {
            input(); // Handle keyboard input
            update(); // Update game state
        } else {
            // Restart game when R is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resetGame();
            }
        }

        draw(); // Always draw (even game over screen)
    }

    /**
     * Handles player input (movement)
     */
    private void input() {

        // Time between frames (used for smooth movement)
        float delta = Gdx.graphics.getDeltaTime();

        // Move player left when LEFT key is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            player.x -= 300 * delta; // speed = 300 pixels per second

        // Move player right when RIGHT key is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            player.x += 300 * delta;

        // Get actual screen width
        float screenWidth = Gdx.graphics.getWidth();

        // Keep player inside screen bounds
        // Math.max prevents going below 0
        // Math.min prevents going beyond right edge
        player.x = Math.max(0, Math.min(player.x, screenWidth - player.width));
    }

    /**
     * Updates game logic (movement, collisions, spawning)
     */
    private void update() {

        // Spawn a new drop every 1 second (1,000,000,000 nanoseconds)
        if (TimeUtils.nanoTime() - lastDropTime > 1_000_000_000) {
            spawnDrop();
        }

        // Iterate through all drops
        Iterator<Circle> iter = drops.iterator();
        while (iter.hasNext()) {
            Circle drop = iter.next();

            // Move drop downward over time
            drop.y -= 300 * Gdx.graphics.getDeltaTime();

            // Create rectangular hitbox around the circle
            Rectangle dropHitbox = new Rectangle(
                    drop.x - drop.radius, // left edge
                    drop.y - drop.radius, // bottom edge
                    drop.radius * 2, // width (diameter)
                    drop.radius * 2 // height (diameter)
            );

            // Check collision between drop and player
            if (dropHitbox.overlaps(player)) {
                score++; // increase score
                catchSound.play(); // play sound
                iter.remove(); // remove drop safely from list
            }
            // If drop falls below screen
            else if (dropHitbox.y + dropHitbox.height < 0) {
                iter.remove(); // remove drop
                lives--; // lose a life

                if (lives <= 0 && !gameOver) {
                    gameOver = true;

                    if (!gameOverPlayed) {
                        gameoversound.play(); // 🎵 play once
                        gameOverPlayed = true;
                    }
                }
            }
        }
    }

    /**
     * Draws everything on screen
     */
    private void draw() {

        // Start drawing
        batch.begin();

        // draw background FIRST so everything appears on top of it
        batch.draw(background, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        // Draw player (mouse)
        // player.x and player.y = bottom-left corner
        batch.draw(mouseTexture, player.x, player.y, player.width, player.height);

        // Draw all drops (cheese)
        for (Circle drop : drops) {
            batch.draw(
                    cheeseTexture,
                    drop.x - drop.radius, // center → left edge
                    drop.y - drop.radius, // center → bottom edge
                    drop.radius * 2, // width
                    drop.radius * 2 // height
            );
        }

        // Draw UI text
        if (gameOver) {
            font.draw(batch, "GAME OVER", 340, 260);
            font.draw(batch, "Final Score: " + score, 330, 235);
            font.draw(batch, "Press R to Restart", 300, 200);
        } else {
            font.draw(batch, "Score: " + score, 10, 475);
            font.draw(batch, "Lives: " + lives, 10, 455);
        }

        // Finish drawing
        batch.end();
    }

    /**
     * Creates a new falling drop
     */
    private void spawnDrop() {
        Circle drop = new Circle();

        drop.radius = 32; // size of drop

        // Get screen width to spawn within bounds
        float screenWidth = Gdx.graphics.getWidth();

        // Random X position within screen
        // Ensures drop stays fully inside screen
        drop.x = drop.radius + (float) Math.random() * (screenWidth - drop.radius * 2);

        // Start at top of screen
        drop.y = Gdx.graphics.getHeight();

        // Add drop to list
        drops.add(drop);

        // Save spawn time
        lastDropTime = TimeUtils.nanoTime();
    }

    private void resetGame() {
        score = 0; // reset score
        lives = 3; // reset lives
        gameOver = false; // resume game
        gameOverPlayed = false;

        drops.clear(); // remove all existing drops

        // reset player position to center
        player.x = Gdx.graphics.getWidth() / 2f - player.width / 2f;

        spawnDrop(); // start with a new drop
    }

    @Override
    public void dispose() {
        // Free memory (important in LibGDX!)
        batch.dispose();
        font.dispose();
        mouseTexture.dispose();
        cheeseTexture.dispose();
        catchSound.dispose();
    }
}