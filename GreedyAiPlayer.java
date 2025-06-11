import java.util.List;
import java.util.Random; // For random choice among best moves

// Assuming AiPlayer, Board, Move, Pos2D, and Const are available.

/**
 * A greedy AI player that always tries to choose a move that allows
 * it to reach the winning row in the fewest possible moves.
 */
public class GreedyAiPlayer extends AiPlayer {

    private final int winningRow; // The row index this player needs to reach to win.
    private final Random random; // For choosing randomly among equally good moves.

    /**
     * Constructs a new GreedyAiPlayer.
     * @param playerId The identifier for this player.
     * @param board The game board.
     */
    public GreedyAiPlayer(int playerId, Board board) {
        super(playerId, board);
        // Determine the winning row based on the player ID.
        // Player 1 (W) aims for row 0. Player 2 (B) aims for the last row (board.m - 1).
        this.winningRow = (playerId == Const.PLAYER1) ? 0 : board.getM() - 1;
        this.random = new Random();
    }

    /**
     * Chooses the move by first finding the best peg (closest to winning row)
     * and then selecting a random move from that peg's possible moves.
     * @return The chosen Move object.
     */
    @Override
    protected Move _play() {
        Pos2D source = findBestPeg(); // Find the peg that is strategically best to move.

        // If no best peg is found (e.g., player has no pegs), return null.
        if (source == null) {
            return null;
        }

        // Get all possible moves from the chosen best source peg.
        List<Move> possibleMovesFromSource = board.possibleMovesFromSource(source, playerId);

        // If there are no moves from the best peg, return null.
        if (possibleMovesFromSource.isEmpty()) {
            return null;
        }

        // Choose a random move from the possible moves of the best peg.
        return possibleMovesFromSource.get(random.nextInt(possibleMovesFromSource.size()));
    }

    /**
     * Determines the best peg (piece) for the player to move.
     * The "best" peg is defined as the one closest to the player's winning row.
     * If multiple pegs are equidistant, it prioritizes the one with the smallest column index.
     * @return The Pos2D of the best peg to move, or null if the player has no pegs.
     */
    private Pos2D findBestPeg() {
        PegsList playersPegs = board.getPegs()[playerId - 1]; // Get the list of pegs for this player.

        Pos2D bestPeg = null;
        double bestDistance = Const.POS_INF; // Initialize with positive infinity.

        // Iterate through all of the player's pegs.
        for (Pos2D peg : playersPegs) {
            // Calculate the Manhattan distance (absolute difference in rows) to the winning row.
            // Using Manhattan distance (abs difference) as it's typically how "closest row" is measured.
            double currentDistance = Math.abs(peg.row - this.winningRow);

            // Compare distances to find the best peg.
            if (bestPeg == null || currentDistance < bestDistance) {
                // Found a new best peg (either first one or a closer one).
                bestPeg = peg;
                bestDistance = currentDistance;
            } else if (currentDistance == bestDistance) {
                // If distances are equal, prioritize the one with a smaller column index (x-coordinate).
                // This corresponds to `peg.x < best_peg.x` in Python.
                if (peg.col < bestPeg.col) {
                    bestPeg = peg;
                }
            }
        }
        return bestPeg; // Returns the best found peg, or null if no pegs exist.
    }

    /**
     * Main method for demonstrating the GreedyAiPlayer.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating GreedyAiPlayer ---");

        // Setup a basic board
        Board board = new Board(6, 6);
        // Add some initial pegs strategically for testing greedy AI
        board.addWhitePeg(new Pos2D(3, 2)); // P1 peg in the middle
        board.addWhitePeg(new Pos2D(2, 3)); // P1 peg closer to winning row (0)
        board.addBlackPeg(new Pos2D(2, 2)); // P2 peg in the middle
        board.addBlackPeg(new Pos2D(3, 3)); // P2 peg closer to winning row (5)

        System.out.println("\nInitial Board Setup:");
        board.print();

        // Create a Greedy AI Player for PLAYER1
        GreedyAiPlayer aiPlayer1 = new GreedyAiPlayer(Const.PLAYER1, board);
        // Create a Greedy AI Player for PLAYER2
        GreedyAiPlayer aiPlayer2 = new GreedyAiPlayer(Const.PLAYER2, board);

        // Simulate a few turns
        for (int i = 0; i < 4; i++) {
            System.out.println("\n--- Player 1 (Greedy) Turn " + (i + 1) + " ---");
            aiPlayer1.play();
            System.out.println("Board after Player 1 move:");
            board.print();
            if (board.getWinner() != null) {
                System.out.println("Game over! Winner: Player " + board.getWinner());
                break;
            }

            System.out.println("\n--- Player 2 (Greedy) Turn " + (i + 1) + " ---");
            aiPlayer2.play();
            System.out.println("Board after Player 2 move:");
            board.print();
            if (board.getWinner() != null) {
                System.out.println("Game over! Winner: Player " + board.getWinner());
                break;
            }
        }
        System.out.println("\nGreedy AI Demonstration finished.");
    }
}
