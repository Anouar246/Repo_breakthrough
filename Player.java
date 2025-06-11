import java.util.Random; // For RandomAiPlayer later

// Assuming Board and Move classes are available.

/**
 * Abstract base class representing a generic player in the Breakthrough game.
 * It cannot be instantiated directly.
 */
public abstract class Player {
    // Protected fields allow access by subclasses.
    protected final int playerId; // Identifier for the player (e.g., GameConfig.PLAYER1 or GameConfig.PLAYER2).
    protected final Board board;    // The game board on which the player will play.

    /**
     * Constructs a new Player object.
     * @param playerId The identifier for this player (GameConfig.PLAYER1 or GameConfig.PLAYER2).
     * @param board The game board instance.
     */
    public Player(int playerId, Board board) {
        this.playerId = playerId;
        this.board = board;
    }

    /**
     * Orchestrates the player's turn: chooses a move and executes it on the board.
     * This method calls the abstract _play() method to get the chosen move.
     * @throws AssertionError if the chosen move is null.
     */
    public void play() {
        Move move = _play(); // Delegate to the concrete subclass's _play implementation.
        // Assert that a valid move is returned. In production, this might be a more robust check or exception.
        if (move == null) {
            throw new AssertionError("Player _play() method returned a null move.");
        }
        // Execute the chosen move on the board.
        this.board.move(move);
    }

    /**
     * Abstract method that must be implemented by subclasses to choose a move.
     * @return The Move object that the player intends to make.
     */
    protected abstract Move _play();

    // No main method needed for an abstract class.
}
