import java.util.List;
import java.util.Random; // For generating random numbers

// Assuming AiPlayer, Board, and Move classes are available.
// Assuming Const class is available for player IDs.

/**
 * An AI player that chooses moves randomly from all available valid moves.
 */
public class RandomAiPlayer extends AiPlayer {

    private final Random random; // Random number generator instance.

    /**
     * Constructs a new RandomAiPlayer.
     * @param playerId The identifier for this player.
     * @param board The game board.
     */
    public RandomAiPlayer(int playerId, Board board) {
        super(playerId, board);
        this.random = new Random(); // Initialize Random.
    }

    /**
     * Chooses a random move from all possible moves for this player.
     * @return A randomly selected Move object.
     */
    @Override
    protected Move _play() {
        // Get all possible moves for the current player.
        List<Move> possibleMoves = board.possibleMoves(playerId);

        // If there are no possible moves, return null (indicating no move can be made).
        if (possibleMoves.isEmpty()) {
            return null;
        }

        // Choose a random move from the list.
        return possibleMoves.get(random.nextInt(possibleMoves.size()));
    }

    /**
     * Main method for demonstrating the RandomAiPlayer.
     * This requires a functional Board and Const setup.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating RandomAiPlayer ---");

        // Setup a basic board for demonstration
        Board board = new Board(Const.DEFAULT_SIZE, Const.DEFAULT_SIZE);
        // Add some initial pegs
        for (int c = 0; c < Const.DEFAULT_SIZE; c++) {
            board.addWhitePeg(new Pos2D(1, c)); // Player 1 (W) pieces
            board.addBlackPeg(new Pos2D(Const.DEFAULT_SIZE - 2, c)); // Player 2 (B) pieces
        }
        System.out.println("\nInitial Board Setup:");
        board.print();

        // Create a Random AI Player for PLAYER1
        RandomAiPlayer aiPlayer = new RandomAiPlayer(Const.PLAYER1, board);

        // Simulate a few turns
        for (int i = 0; i < 3; i++) {
            System.out.println("\n--- AI Player " + aiPlayer.playerId + "'s Turn " + (i + 1) + " ---");
            aiPlayer.play(); // Let the AI make a move
            System.out.println("Board after AI move:");
            board.print();
            if (board.getWinner() != null) {
                System.out.println("Game over! Winner: Player " + board.getWinner());
                break;
            }
        }
        System.out.println("\nDemonstration finished.");
    }
}
