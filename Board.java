import java.util.ArrayList; // For dynamic list functionality
import java.util.Iterator;  // For implementing Iterable
import java.util.List;      // For the List interface
import java.util.Arrays;    // For utility methods like Arrays.asList (if needed, not used here directly)
import java.util.NoSuchElementException; // For custom Iterator
import java.util.HashSet;   // For hasIntersectionWith
import java.util.Set;       // For hasIntersectionWith
import java.util.stream.Collectors; // For Stream API usage

// Assumed: All dependent classes (Pos2D, Move, PegsList, Matrix, Const,
// BreakthroughError, InvalidPositionError, EmptyHistoryError)
// are available in the same package or correctly imported.
// Make sure Pos2D.equals(), Pos2D.hashCode(), and Pos2D.subtract() are correctly implemented.
// Make sure Move.equals(), Move.hashCode(), Move.reverse(), and Move.getDelta() are correctly implemented.
// Make sure PegsList.remove(), PegsList.add(), PegsList.hasIntersectionWith(), PegsList.size(), and PegsList.move() work as expected.
// Make sure Matrix.get(), Matrix.set(), Matrix.isValidPos(), Matrix.size(), and Matrix.count() work as expected.

/**
 * Represents the game board for Breakthrough.
 * It manages the game grid, tracks player's pieces, records move history,
 * and provides methods for game logic such as move validation, execution, and undo.
 */
public class Board {

    // --- Inner Class: HistoryEntry ---
    /**
     * Represents an entry in the game's move history.
     * Contains the move that was made and whether it resulted in a capture.
     * This class is immutable.
     */
    static class HistoryEntry {
        public final Move move;     // The move that was executed.
        public final boolean captured; // True if an opponent's piece was captured by this move.

        /**
         * Constructs a new HistoryEntry.
         * @param move The move associated with this history entry. Must not be null.
         * @param captured True if this move captured an opponent's piece.
         * @throws IllegalArgumentException if the provided move is null.
         */
        public HistoryEntry(Move move, boolean captured) {
            if (move == null) {
                throw new IllegalArgumentException("Move cannot be null for HistoryEntry.");
            }
            this.move = move;
            this.captured = captured;
        }

        /**
         * Returns the move associated with this history entry.
         * @return The Move object.
         */
        public Move getMove() {
            return move;
        }

        /**
         * Checks if this history entry indicates a captured piece.
         * @return True if a piece was captured, false otherwise.
         */
        public boolean isCaptured() {
            return captured;
        }

        /**
         * Provides a string representation of the HistoryEntry.
         * @return A string detailing the move and capture status.
         */
        @Override
        public String toString() {
            return "HistoryEntry[move=" + move + ", captured=" + captured + "]";
        }
    }
    // --- End HistoryEntry Class ---


    // --- Board Attributes ---
    private final Matrix matrix;            // Matrix representation of the game board (stores piece IDs).
    private final PegsList whitePegs;       // List of positions for Player 1 (White) pieces.
    private final PegsList blackPegs;       // List of positions for Player 2 (Black) pieces.
    private final PegsList[] pegs;          // Array holding references to whitePegs and blackPegs
                                            // Indexed as pegs[PLAYER1 - 1] and pegs[PLAYER2 - 1].
    private final List<HistoryEntry> history; // Chronological list of moves made on the board.
    // --- End Board Attributes ---


    /**
     * Constructs a new game board with specified dimensions.
     * Initializes the matrix with all cells as EMPTY and sets up the peg lists for both players.
     * @param rows The number of rows for the board.
     * @param cols The number of columns for the board.
     */
    public Board(int rows, int cols) {
        // Initialize the game matrix with default EMPTY values.
        this.matrix = new Matrix(rows, cols, Const.EMPTY);
        this.whitePegs = new PegsList();
        this.blackPegs = new PegsList();

        // Initialize the 'pegs' array for easy access using player IDs.
        // Const.PLAYER1 is 1, so Const.PLAYER1 - 1 is 0.
        // Const.PLAYER2 is 2, so Const.PLAYER2 - 1 is 1.
        this.pegs = new PegsList[2];
        this.pegs[Const.PLAYER1 - 1] = this.whitePegs;
        this.pegs[Const.PLAYER2 - 1] = this.blackPegs;

        this.history = new ArrayList<>(); // Initialize empty move history.
    }

    // --- Properties (Getter Methods) ---
    /**
     * Returns the number of rows on the board.
     * Corresponds to the 'm' property in Python.
     * @return The row count.
     */
    public int getM() {
        return matrix.getM();
    }

    /**
     * Returns the number of columns on the board.
     * Corresponds to the 'n' property in Python.
     * @return The column count.
     */
    public int getN() {
        return matrix.getN();
    }

    /**
     * Returns the total number of pegs currently on the board, across both players.
     * Corresponds to the 'nb_pegs' property in Python.
     * @return The total count of pegs.
     */
    public int getNbPegs() {
        // Sums the sizes of the white and black pegs lists.
        return whitePegs.size() + blackPegs.size();
    }

    /**
     * Returns the array containing the PegsList for each player.
     * Corresponds to the 'pegs' property in Python.
     * @return An array where index 0 is PegsList for PLAYER1 and index 1 for PLAYER2.
     */
    public PegsList[] getPegs() {
        // Return a defensive copy to prevent external modification of the internal array reference.
        return Arrays.copyOf(pegs, pegs.length);
    }

    /**
     * Returns the last move recorded in the game history.
     * Corresponds to the 'last_move' property in Python.
     * @return The last HistoryEntry's move, or null if history is empty.
     */
    public Move getLastMove() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1).getMove();
    }

    /**
     * Determines the winner of the game.
     * A player wins if their piece reaches the opponent's starting row, or
     * if the opponent has no more pieces left.
     * Corresponds to the 'winner' property in Python.
     * @return Const.PLAYER1 if Player 1 wins, Const.PLAYER2 if Player 2 wins,
     * or null if the game is not yet finished.
     */
    public Integer getWinner() {
        Move lastPlayedMove = getLastMove();
        // If no moves have been played, there's no winner yet.
        if (lastPlayedMove == null) {
            // Check for initial win condition if a player starts with no pegs (unlikely in Breakthrough, but good for robustness)
             if (whitePegs.size() == 0) {
                return Const.PLAYER2;
            }
            if (blackPegs.size() == 0) {
                return Const.PLAYER1;
            }
            return null; // No winner yet if no moves and both have pegs.
        }

        int lastPlayer = lastPlayedMove.player;
        Pos2D lastDest = lastPlayedMove.dest;

        // Win condition 1: A player reaches the opponent's back row.
        // Player 1 (W) reaches row 0.
        if (lastPlayer == Const.PLAYER1 && lastDest.row == 0) {
            return Const.PLAYER1;
        }
        // Player 2 (B) reaches row m-1.
        if (lastPlayer == Const.PLAYER2 && lastDest.row == getM() - 1) {
            return Const.PLAYER2;
        }

        // Win condition 2: Opponent has no pieces left.
        if (whitePegs.size() == 0) {
            return Const.PLAYER2; // Player 2 wins if Player 1 has no pegs.
        }
        if (blackPegs.size() == 0) {
            return Const.PLAYER1; // Player 1 wins if Player 2 has no pegs.
        }

        // No winner yet.
        return null;
    }

    // --- Piece Management Methods ---
    /**
     * Adds a piece for Player 1 (White) at the given position.
     * @param pos The Pos2D object representing the position to add the piece.
     * @throws InvalidPositionError If the position is already occupied.
     */
    public void addWhitePeg(Pos2D pos) {
        // Calls the private helper method to add the piece for PLAYER1.
        this.addPeg(pos, Const.PLAYER1);
    }

    /**
     * Adds a piece for Player 2 (Black) at the given position.
     * @param pos The Pos2D object representing the position to add the piece.
     * @throws InvalidPositionError If the position is already occupied.
     */
    public void addBlackPeg(Pos2D pos) {
        // Calls the private helper method to add the piece for PLAYER2.
        this.addPeg(pos, Const.PLAYER2);
    }

    /**
     * Private helper method to add a piece for a given player at a specified position.
     * @param pos The Pos2D object where the piece should be added.
     * @param player The integer ID of the player (Const.PLAYER1 or Const.PLAYER2).
     * @throws InvalidPositionError If the position is already occupied by another piece.
     */
    private void addPeg(Pos2D pos, int player) {
        // Check if the position is already occupied by any piece.
        // matrix.get(pos) returns the piece ID at that position.
        if (matrix.get(pos) != Const.EMPTY) {
            throw new InvalidPositionError(
                "Case " + pos + " already occupied by: " + matrix.get(pos)
            );
        }
        // Set the piece on the matrix.
        matrix.set(pos, player);
        // Add the position to the player's specific PegsList.
        // Adjust player ID to 0-based index for the 'pegs' array (PLAYER1-1 or PLAYER2-1).
        this.pegs[player - 1].add(pos);
    }

    /**
     * Checks the internal consistency of the board state.
     * This includes verifying:
     * (i) no square is occupied by pieces of both players simultaneously.
     * (ii) the matrix representation matches the `PegsList` for each player.
     * (iii) the count of empty squares in the matrix matches the calculated empty count.
     * @return true if the board's integrity is valid, false otherwise.
     */
    public boolean checkIntegrity() {
        // (i) Check for intersection between white and black pegs.
        if (whitePegs.hasIntersectionWith(blackPegs)) {
            System.err.println("Integrity Check Failed: Intersection between white and black pegs.");
            return false;
        }

        // (ii) Verify that positions in whitePegs match PLAYER1 in the matrix.
        for (Pos2D pos : whitePegs) {
            // matrix.get(pos) should return Const.PLAYER1 for all positions in whitePegs.
            if (matrix.get(pos) != Const.PLAYER1) {
                System.err.println("Integrity Check Failed: White peg at " + pos + " does not match matrix.");
                return false;
            }
        }

        // (ii) Verify that positions in blackPegs match PLAYER2 in the matrix.
        for (Pos2D pos : blackPegs) {
            // matrix.get(pos) should return Const.PLAYER2 for all positions in blackPegs.
            if (matrix.get(pos) != Const.PLAYER2) {
                System.err.println("Integrity Check Failed: Black peg at " + pos + " does not match matrix.");
                return false;
            }
        }

        // (iii) Verify that the count of EMPTY squares in the matrix is consistent with total pegs.
        int expectedEmpty = matrix.size() - getNbPegs();
        if (matrix.count(Const.EMPTY) != expectedEmpty) {
            System.err.println("Integrity Check Failed: Empty count mismatch. Expected " + expectedEmpty + ", got " + matrix.count(Const.EMPTY));
            return false;
        }

        // If all checks pass, the board's integrity is maintained.
        return true;
    }

    /**
     * Prints the game board to the console, with an option to highlight a special position.
     * Corresponds to the 'print' method in Python.
     * Uses constants from Const for display characters and alphabet.
     * @param specialPosition The Pos2D position to highlight (can be null for no highlighting).
     * @param specialChar The character to use for highlighting.
     */
    public void print(Pos2D specialPosition, char specialChar) {
        int m = matrix.getM();
        int n = matrix.getN();

        // Print top column labels (a b c ...)
        System.out.print("  ");
        for (int j = 0; j < n; j++) {
            System.out.print(Const.ALPHABET.charAt(j) + " ");
        }
        System.out.println(); // New line after column labels

        // Print rows (with row numbers)
        for (int i = 0; i < m; i++) {
            System.out.printf("%-2d", i + 1); // Left-align row number (e.g., "1 ")
            for (int j = 0; j < n; j++) {
                Pos2D currentPos = new Pos2D(i, j); // Create Pos2D for current cell

                // Check if current position is the special position
                if (currentPos.equals(specialPosition)) {
                    System.out.print(specialChar + " "); // Print special character
                } else {
                    // Get piece from matrix and use Const.CHARS for display
                    System.out.print(Const.CHARS[(int) matrix.get(currentPos)] + " ");
                }
            }
            System.out.printf("%-2d", i + 1); // Right-align row number (e.g., " 1")
            System.out.println(); // New line after each row
        }

        // Print bottom column labels (a b c ...)
        System.out.print("  ");
        for (int j = 0; j < n; j++) {
            System.out.print(Const.ALPHABET.charAt(j) + " ");
        }
        System.out.println(); // Final new line
    }

    /**
     * Prints the game board to the console without any highlighted positions.
     * This is a convenience method, calling the main print method with null special position.
     */
    public void print() {
        print(null, ' '); // No special position, default char is space (won't be used)
    }

    /**
     * Generates all possible moves for a given piece of a specific player.
     * Corresponds to the 'possible_moves_from_source' method in Python.
     * @param src The starting position of the piece.
     * @param player The ID of the player (Const.PLAYER1 or Const.PLAYER2).
     * @return An Iterable (specifically a List) of valid Move objects for the piece.
     */
    public List<Move> possibleMovesFromSource(Pos2D src, int player) {
        List<Move> moves = new ArrayList<>();
        // Iterate through valid delta movements for the given player.
        // VALID_MOVES[player-1] gives the array of Pos2D offsets for that player.
        for (Pos2D delta : Const.VALID_MOVES[player - 1]) {
            // Calculate the destination position by adding the delta to the source.
            Pos2D dest = src.add(delta);
            Move move = new Move(src, dest, player);

            // Check if the destination is within board bounds and the move direction is valid.
            if (matrix.isValidPos(dest) && isValidDirection(move)) {
                moves.add(move); // Add the valid move to the list.
            }
        }
        return moves;
    }

    /**
     * Determines if a move is valid according to Breakthrough rules.
     * Corresponds to the 'is_valid_direction' method in Python.
     * @param move The Move object to validate.
     * @return True if the move is valid, false otherwise.
     */
    public boolean isValidDirection(Move move) {
        Pos2D delta = move.getDelta(); // Get the displacement vector (dest - src)
        int player = move.player;      // Get the player making the move
        int destinationPiece = (int) matrix.get(move.dest); // Get what's at the destination

        // Check if the delta is one of the valid movement patterns for the player.
        boolean isValidDelta = false;
        for (Pos2D validDelta : Const.VALID_MOVES[player - 1]) {
            if (validDelta.equals(delta)) {
                isValidDelta = true;
                break;
            }
        }
        if (!isValidDelta) {
            return false;
        }

        // Check capture rules:
        // - If moving straight (delta.col == 0), destination must be EMPTY.
        // - If moving diagonally (delta.col != 0), destination must NOT be the same player's piece.
        //   (It can be EMPTY or an opponent's piece for a capture).
        return (delta.col == 0 && destinationPiece == Const.EMPTY) ||
               (delta.col != 0 && destinationPiece != player);
    }

    /**
     * Generates all possible moves that a given player can make.
     * Corresponds to the '_possible_moves' method in Python (which was a generator).
     * This version collects all moves into a List.
     * @param player The ID of the player (Const.PLAYER1 or Const.PLAYER2).
     * @return A List of all valid Move objects for the player.
     */
    private List<Move> _possibleMoves(int player) {
        List<Move> allPossibleMoves = new ArrayList<>();
        // Iterate through each peg the player owns.
        for (Pos2D src : this.pegs[player - 1]) {
            // For each peg, find all possible moves from its source position.
            allPossibleMoves.addAll(possibleMovesFromSource(src, player));
        }
        return allPossibleMoves;
    }

    /**
     * Generates all possible moves that a given player can make.
     * This method directly calls the internal _possibleMoves and returns the list.
     * Corresponds to the 'possible_moves' method in Python.
     * @param player The ID of the player (Const.PLAYER1 or Const.PLAYER2).
     * @return A List of all valid Move objects for the player.
     */
    public List<Move> possibleMoves(int player) {
        return _possibleMoves(player);
    }

    /**
     * Determines if there is at least one valid move for a piece at the given position.
     * Corresponds to the 'can_move_from' method in Python.
     * @param pos The Pos2D position to check.
     * @return True if a piece exists at `pos` and has at least one valid move, false otherwise.
     */
    public boolean canMoveFrom(Pos2D pos) {
        // Get the piece at the given position.
        int player = (int) matrix.get(pos); // matrix.get returns double, cast to int.

        // If the position is empty, no piece can move from there.
        if (player == Const.EMPTY) {
            return false;
        }

        // In Python, it used 'next(generator)' to check if any move exists.
        // Here, we can directly call possibleMovesFromSource and check if the list is empty.
        return !possibleMovesFromSource(pos, player).isEmpty();
    }

    /**
     * Executes the requested move on the board.
     * Updates the matrix, player's peg lists, and records the move in history.
     * Corresponds to the 'move' method in Python.
     * @param move The Move object to execute.
     * @throws InvalidPositionError if the move's source position doesn't contain the expected piece,
     * or destination is invalid for the move's rules.
     */
    public void move(Move move) {
        int player = move.player;
        // Determine the opponent's player ID. (PLAYER1 + PLAYER2) is 3, so 3 - player gives the other player.
        int otherPlayer = (Const.PLAYER1 + Const.PLAYER2) - player;

        // Verify that the piece at the source is indeed the player's piece.
        if (matrix.get(move.src) != player) {
             throw new InvalidPositionError("Piece at source " + move.src + " is not player " + player + "'s piece.");
        }
        // Verify that the move itself is valid according to game rules before executing.
        if (!isValidDirection(move)) {
            throw new InvalidPositionError("Move " + move + " is not a valid direction.");
        }

        // Determine if a capture occurs.
        boolean captured = (matrix.get(move.dest) == otherPlayer);
        HistoryEntry entry = new HistoryEntry(move, captured);

        // If a capture occurs, remove the captured piece from the opponent's peg list.
        if (captured) {
            this.pegs[otherPlayer - 1].remove(move.dest);
        }

        // Update the matrix: clear the source position, set the destination position.
        matrix.set(move.src, Const.EMPTY);
        matrix.set(move.dest, player);

        // Update the moving player's peg list (change source position to destination).
        this.pegs[player - 1].move(move);

        // Add the move entry to the history.
        this.history.add(entry);
    }

    /**
     * Undoes the last move played on the board.
     * Reverts the board state, updates peg lists, and removes the move from history.
     * Corresponds to the 'undo' method in Python.
     * @throws EmptyHistoryError if the history is empty and there are no moves to undo.
     */
    public void undo() {
        if (history.isEmpty()) {
            throw new EmptyHistoryError("No moves to undo. History is empty.");
        }

        // Retrieve and remove the last history entry.
        HistoryEntry lastEntry = history.remove(history.size() - 1);
        Move lastMove = lastEntry.move;

        int player = lastMove.player;
        // Determine the opponent's player ID for potential recapture.
        int otherPlayer = (Const.PLAYER1 + Const.PLAYER2) - player;

        // Revert the piece on the matrix: set the source position back to the player's piece.
        matrix.set(lastMove.src, player);
        // Move the piece in the player's peg list back from destination to source.
        // This uses the reversed move: dest -> src.
        this.pegs[player - 1].move(lastMove.reverse());

        // If the last move resulted in a capture, restore the captured piece.
        if (lastEntry.captured) {
            matrix.set(lastMove.dest, otherPlayer); // Place opponent's piece back at destination.
            this.pegs[otherPlayer - 1].add(lastMove.dest); // Add it back to opponent's peg list.
        } else {
            // If no capture, the destination square was empty, so set it back to EMPTY.
            matrix.set(lastMove.dest, Const.EMPTY);
        }
    }


    /**
     * Main method for demonstrating the complete Board class.
     * This method assumes that Pos2D, Move, PegsList, Matrix, Const,
     * BreakthroughError, InvalidPositionError, and EmptyHistoryError are all
     * correctly defined and available in the same package or imported.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating Complete Board Class ---");

        // Create a board of default size (e.g., 6x6)
        Board board = new Board(Const.DEFAULT_SIZE, Const.DEFAULT_SIZE);
        System.out.println("\nInitial Board:");
        board.print();

        // Add some white and black pegs
        Pos2D w1 = new Pos2D(0, 0); // row 0, col 0
        Pos2D w2 = new Pos2D(0, 1); // row 0, col 1
        Pos2D b1 = new Pos2D(5, 0); // row 5, col 0
        Pos2D b2 = new Pos2D(5, 1); // row 5, col 1
        Pos2D b_capture_target = new Pos2D(1,0); // A position to potentially capture

        board.addWhitePeg(w1);
        board.addWhitePeg(w2);
        board.addBlackPeg(b1);
        board.addBlackPeg(b2);
        board.addWhitePeg(b_capture_target); // Place a white piece for capture demonstration

        System.out.println("\nBoard after initial setup (including a piece for capture):");
        board.print();

        System.out.println("\n--- Testing Getters ---");
        System.out.println("Board M (rows): " + board.getM());
        System.out.println("Board N (cols): " + board.getN());
        System.out.println("Total Pegs: " + board.getNbPegs()); // Expected: 5
        System.out.println("Last Move: " + board.getLastMove()); // Expected: null
        System.out.println("Winner: " + board.getWinner()); // Expected: null

        System.out.println("\n--- Testing addPeg error ---");
        try {
            board.addWhitePeg(w1); // Try to add to an already occupied spot
        } catch (InvalidPositionError e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        System.out.println("\n--- Testing Integrity Check ---");
        System.out.println("Board Integrity (should be true): " + board.checkIntegrity());

        // --- New Functionality Tests ---

        System.out.println("\n--- Testing possibleMovesFromSource ---");
        Pos2D p1_src = new Pos2D(0,0);
        System.out.println("Possible moves from " + p1_src + " for PLAYER1: " + board.possibleMovesFromSource(p1_src, Const.PLAYER1));
        // Expected moves: (0,0)->(0,1) (straight, empty), (0,0)->(1,0) (diag, capture)
        // Note: Pos2D (0,0) is row 0, col 0.
        // Valid moves for W: (-1,-1), (-1,0), (-1,1) in (row, col) delta
        // From (0,0):
        // (-1,-1) -> (-1,-1) invalid
        // (-1,0) -> (-1,0) invalid
        // (-1,1) -> (-1,1) invalid
        // Wait, my example piece at (0,0) won't have valid moves from there. Let's move w1 to (1,1)
        board.matrix.set(w1, Const.EMPTY); // Clear old position
        board.pegs[Const.PLAYER1-1].remove(w1);
        w1 = new Pos2D(1,1);
        board.addWhitePeg(w1); // New position for w1
        System.out.println("\nBoard after adjusting w1 for move tests:");
        board.print();

        System.out.println("\nPossible moves from " + w1 + " for PLAYER1 (piece at (1,1)):");
        List<Move> p1_moves = board.possibleMovesFromSource(w1, Const.PLAYER1);
        for (Move m : p1_moves) {
            System.out.println(m);
        }
        // Example with capture: w1 at (1,1), b_capture_target at (1,0)
        // delta for W are (-1,-1), (-1,0), (-1,1)
        // (1,1) + (-1,-1) = (0,0) -> dest (0,0) has P1 (should be capture/empty for diag) - invalid destination for diag move
        // (1,1) + (-1,0) = (0,1) -> dest (0,1) empty - valid
        // (1,1) + (-1,1) = (0,2) -> dest (0,2) empty - valid
        // Let's add a black piece at (0,0) for capture test.
        board.addBlackPeg(new Pos2D(0,0));
        board.print();
        System.out.println("\nPossible moves from " + w1 + " for PLAYER1 (with black at (0,0)):");
        p1_moves = board.possibleMovesFromSource(w1, Const.PLAYER1);
        for (Move m : p1_moves) {
            System.out.println(m);
        } // Should include (1,1)->(0,0) as capture if (0,0) is black

        System.out.println("\n--- Testing possibleMoves (all for Player 2) ---");
        List<Move> p2_all_moves = board.possibleMoves(Const.PLAYER2);
        for (Move m : p2_all_moves) {
            System.out.println(m);
        }

        System.out.println("\n--- Testing canMoveFrom ---");
        System.out.println("Can move from " + w1 + "? " + board.canMoveFrom(w1)); // Expected: true
        System.out.println("Can move from (2,2) (empty)? " + board.canMoveFrom(new Pos2D(2,2))); // Expected: false

        System.out.println("\n--- Testing move and undo ---");
        Move actualMove = null;
        if (!p1_moves.isEmpty()) {
            actualMove = p1_moves.get(0); // Take the first possible move
            System.out.println("Executing move: " + actualMove);
            board.move(actualMove);
            System.out.println("\nBoard after move:");
            board.print();
            System.out.println("Last Move: " + board.getLastMove());
            System.out.println("Winner: " + board.getWinner()); // Check if winning condition met
        } else {
             System.out.println("No valid moves for PLAYER1 to demonstrate move/undo.");
        }


        if (actualMove != null) {
            System.out.println("\nUndoing last move...");
            board.undo();
            System.out.println("\nBoard after undo:");
            board.print();
            System.out.println("Last Move: " + board.getLastMove()); // Should be null or previous if more moves
            System.out.println("Winner: " + board.getWinner()); // Should revert winner
        }

        System.out.println("\n--- Testing undo on empty history (should throw) ---");
        // Clear history for this test
        while (!board.history.isEmpty()) {
            board.history.remove(0);
        }
        try {
            board.undo();
        } catch (EmptyHistoryError e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

    }
}