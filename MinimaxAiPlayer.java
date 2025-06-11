import java.util.ArrayList;
import java.util.List;
import java.util.Random; // For random choice among best moves

// Assuming AiPlayer, Board, Move, Pos2D, Const,
// BreakthroughError, InvalidPositionError, EmptyHistoryError are available.

/**
 * An AI player that implements the Minimax algorithm to choose the best move.
 */
public class MinimaxAiPlayer extends AiPlayer {

    // The depth of the search tree for the Minimax algorithm.
    // This is a static constant as it's a characteristic of the algorithm, not an instance.
    public static final int DEPTH = 3;

    private final Random random; // Used for breaking ties between equally good moves.

    /**
     * Constructs a new MinimaxAiPlayer.
     * @param playerId The identifier for this player.
     * @param board The game board.
     */
    public MinimaxAiPlayer(int playerId, Board board) {
        super(playerId, board);
        this.random = new Random();
    }

    /**
     * Chooses the best move using the Minimax algorithm.
     * @return The chosen Move object.
     */
    @Override
    protected Move _play() {
        // Call the minimax algorithm to get the best move and its associated score.
        // We start with maximizing=true because it's our player's turn to make a move.
        // The return type is a custom pair or simple array, as Java doesn't have direct tuples.
        Object[] result = minimax(DEPTH, true); // result[0] is Move, result[1] is Integer score.

        Move chosenMove = (Move) result[0];
        // If chosenMove is null, it means there are no possible moves or a winning state.
        // The play() method in Player will handle null gracefully.
        return chosenMove;
    }

    /**
     * Implements the Minimax algorithm.
     * This recursive method explores the game tree to find the optimal move.
     *
     * @param depth The remaining number of "layers" to explore in the game tree.
     * @param maximizing True if the current step is for the maximizing player (our AI),
     * false if it's for the minimizing player (opponent).
     * @return An Object array where index 0 is the best Move (or null if no moves/final state)
     * and index 1 is the Integer score associated with that move.
     */
    private Object[] minimax(int depth, boolean maximizing) {
        // --- Base Case 1: Game Over (Win/Loss) ---
        Integer winner = board.getWinner(); // Check if the current board state is a terminal state.
        if (winner != null) {
            int score;
            if (winner == playerId) { // If our AI wins
                score = Const.WIN + depth; // Reward for winning, higher depth means faster win
            } else { // If opponent wins
                score = Const.LOSS - depth; // Penalty for losing, lower depth means slower loss
            }
            return new Object[]{null, score}; // Return null move as game is over, and the score.
        }

        // --- Base Case 2: Max Depth Reached ---
        if (depth == 0) {
            // If we've reached the maximum search depth, return a draw score for now.
            // In more complex AIs, this would be an evaluation function.
            return new Object[]{null, Const.DRAW};
        }

        // Determine who the current player is for this specific minimax call.
        int currentPlayer = maximizing ? playerId : (Const.PLAYER1 + Const.PLAYER2) - playerId;
        int otherPlayer = (Const.PLAYER1 + Const.PLAYER2) - currentPlayer; // Opponent for this turn.

        // Initialize best_moves list and best_reward based on maximizing/minimizing.
        List<Move> bestMoves = new ArrayList<>();
        // Use Integer.MIN_VALUE/MAX_VALUE for initial reward.
        int bestReward = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Get all possible moves for the current player at this state.
        List<Move> possibleMoves = board.possibleMoves(currentPlayer);

        // --- Handle No Possible Moves (Forfeit) ---
        // If there are no possible moves for the current player, it's considered a forfeit.
        if (possibleMoves.isEmpty()) {
            int score;
            if (currentPlayer == playerId) { // If *our* player has no moves, it's a loss for us.
                score = Const.LOSS - depth;
            } else { // If *opponent* player has no moves, it's a win for us.
                score = Const.WIN + depth;
            }
            return new Object[]{null, score};
        }

        // --- Explore Possible Moves ---
        for (Move move : possibleMoves) {
            // Apply the move to the board (simulating the move).
            // This modifies the board object, so it must be undone later.
            board.move(move);

            // Recursively call minimax for the next depth, switching roles (maximizing becomes minimizing).
            Object[] result = minimax(depth - 1, !maximizing);
            int reward = (Integer) result[1]; // Get the score from the recursive call.

            // Undo the move to revert the board to its state before this simulation.
            // This is crucial for correctly exploring different branches of the game tree.
            board.undo();

            // --- Update Best Moves and Reward ---
            if (reward == bestReward) {
                // If the current move yields a reward equal to the best found so far, add it to bestMoves.
                bestMoves.add(move);
            } else if ((maximizing && (reward > bestReward)) ||
                       (!maximizing && (reward < bestReward))) {
                // If this move yields a *better* reward (for maximizing) or a *worse* reward (for minimizing),
                // it becomes the new best. Clear previous best moves and add this one.
                bestMoves.clear(); // Clear previous best moves.
                bestMoves.add(move);
                bestReward = reward; // Update the best reward.
            }
        }

        // If multiple best moves were found (ties), randomly choose one.
        // This prevents the AI from always picking the same move in tied situations.
        Move chosenMove = null;
        if (!bestMoves.isEmpty()) {
            chosenMove = bestMoves.get(random.nextInt(bestMoves.size()));
        }

        return new Object[]{chosenMove, bestReward};
    }

    /**
     * Main method for demonstrating the MinimaxAiPlayer.
     * This requires a functional Board and Const setup.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating MinimaxAiPlayer ---");

        // Setup a small board for quicker minimax calculation during demo
        Board board = new Board(4, 4); // Smaller board
        // Add some initial pegs
        board.addWhitePeg(new Pos2D(0, 1)); // P1
        board.addWhitePeg(new Pos2D(1, 0)); // P1
        board.addBlackPeg(new Pos2D(3, 1)); // P2
        board.addBlackPeg(new Pos2D(2, 0)); // P2

        System.out.println("\nInitial Board Setup:");
        board.print();

        // Create a Minimax AI Player for PLAYER1
        MinimaxAiPlayer aiPlayer = new MinimaxAiPlayer(Const.PLAYER1, board);

        // Simulate a turn for the Minimax AI
        System.out.println("\n--- AI Player " + aiPlayer.playerId + " (Minimax) Turn ---");
        aiPlayer.play(); // Let the AI make a move
        System.out.println("Board after AI move:");
        board.print();
        if (board.getWinner() != null) {
            System.out.println("Game over! Winner: Player " + board.getWinner());
        }

        // You can add more complex scenarios or multiple turns to fully test Minimax.
        // Be cautious with board size and DEPTH, as minimax computational cost grows exponentially.
        System.out.println("\nMinimax AI Demonstration finished.");
    }
}
