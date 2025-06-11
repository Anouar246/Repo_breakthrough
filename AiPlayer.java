// Assuming Player class is available.

/**
 * Abstract base class representing an Artificial Intelligence (AI) player.
 * It extends the generic Player class and provides a common base for different AI strategies.
 */
public abstract class AiPlayer extends Player {

    /**
     * Constructs a new AiPlayer object.
     * @param playerId The identifier for this AI player.
     * @param board The game board instance.
     */
    public AiPlayer(int playerId, Board board) {
        super(playerId, board); // Call the constructor of the parent Player class.
    }

    // No additional abstract methods are introduced here, as _play is already abstract from Player.
    // Concrete AI players will implement _play directly.

    // No main method needed for an abstract class.
}
