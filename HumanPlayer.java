import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Assuming Player, Board, Pos2D, Move, PegPicker, and Const are available.
// Assumed: All custom error classes (BreakthroughError, InvalidPositionError, EmptyHistoryError) are available.

/**
 * Represents a human player that determines moves through user input (I/O).
 */
public class HumanPlayer extends Player {

    private final Scanner scanner; // Scanner for reading user input from the console.

    /**
     * Constructs a new HumanPlayer.
     * @param playerId The identifier for this player.
     * @param board The game board.
     */
    public HumanPlayer(int playerId, Board board) {
        super(playerId, board);
        this.scanner = new Scanner(System.in); // Initialize Scanner to read from standard input.
    }

    /**
     * Chooses a move by guiding the human player through piece selection and destination selection.
     * @return The chosen Move object.
     */
    @Override
    protected Move _play() {
        // Step 1: Select the peg (source position).
        Pos2D selectedPeg = selectPeg();

        // Step 2: Select the destination for the chosen peg.
        return selectMove(selectedPeg);
    }

    /**
     * Guides the human player to select a peg (piece) to move.
     * The player uses keyboard commands to navigate and confirm the selection.
     * @return The Pos2D of the selected peg.
     */
    private Pos2D selectPeg() {
        // Filter the player's pegs to only include those that have at least one valid move.
        List<Pos2D> selectablePegs = new ArrayList<>();
        // Use the public getter getPegs() from the board object to access player's pegs.
        for (Pos2D peg : board.getPegs()[playerId - 1]) {
            if (board.canMoveFrom(peg)) {
                selectablePegs.add(peg);
            }
        }

        // If no selectable pegs, this indicates an unexpected game state.
        if (selectablePegs.isEmpty()) {
            throw new IllegalStateException("No selectable pegs for Player " + playerId + ". Game might be over or logic error.");
        }

        // Create a PegPicker instance with the selectable pegs.
        PegPicker pegPicker = new PegPicker(board.getM(), selectablePegs); // board.getM() is board height

        String choice = "";
        Pos2D currentSelection = null; // To store the selected peg once 'y' is pressed.

        while (!choice.equals(String.valueOf(Const.YES))) {
            currentSelection = pegPicker.getCurrent(); // Get the current highlighted peg.
            board.print(currentSelection, '#'); // Print board highlighting the current peg.

            System.out.print("Select this piece?\n" +
                             "'" + Const.YES + "': yes, " +
                             "'" + Const.LEFT + "': <, " +
                             "'" + Const.RIGHT + "': >, " +
                             "'" + Const.UP + "': ^, " +
                             "'" + Const.DOWN + "': v    ");
            System.out.flush(); // Ensure the prompt is immediately displayed.

            // Adding a small sleep can sometimes help with console input issues in certain environments.
            try {
                Thread.sleep(50); // Sleep for 50 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.err.println("Thread sleep interrupted.");
            }

            choice = scanner.nextLine().toLowerCase(); // Read user input and convert to lowercase.

            // Process user input for navigation.
            if (choice.equals(String.valueOf(Const.LEFT))) {
                pegPicker.left();
            } else if (choice.equals(String.valueOf(Const.RIGHT))) {
                pegPicker.right();
            } else if (choice.equals(String.valueOf(Const.UP))) {
                pegPicker.up();
            } else if (choice.equals(String.valueOf(Const.DOWN))) {
                pegPicker.down();
            } else if (!choice.equals(String.valueOf(Const.YES))) {
                System.out.println("Invalid input. Please use 'y', 'j', 'l', 'i', or 'k'.");
            }
        }
        return currentSelection; // Return the confirmed selected peg.
    }

    /**
     * Guides the human player to select a destination for the chosen peg.
     * The player uses keyboard commands to navigate through possible destinations and confirm the selection.
     * @param peg The source (starting) position of the peg to move.
     * @return The chosen Move object (peg -> destination).
     */
    private Move selectMove(Pos2D peg) {
        // Get all possible moves from the selected source peg for this player.
        List<Move> possibleMovesFromSource = board.possibleMovesFromSource(peg, playerId);

        // If no possible destinations (unlikely if selectPeg filtered correctly, but as a safeguard).
        if (possibleMovesFromSource.isEmpty()) {
            throw new IllegalStateException("Selected peg at " + peg + " has no valid moves.");
        }

        int currentMoveIdx = 0; // Index of the currently highlighted possible destination.
        String choice = "";
        Move selectedMove = null; // To store the selected move once 'y' is pressed.

        while (!choice.equals(String.valueOf(Const.YES))) {
            selectedMove = possibleMovesFromSource.get(currentMoveIdx); // Get the current highlighted move.
            board.print(selectedMove.dest, '*'); // Print board highlighting the destination with '*'.

            System.out.print("Move piece here?\n" +
                             "'" + Const.YES + "': yes, " +
                             "'" + Const.LEFT + "': <, " +
                             "'" + Const.RIGHT + "': >    ");
            System.out.flush(); // Ensure the prompt is immediately displayed.

            // Adding a small sleep can sometimes help with console input issues.
            try {
                Thread.sleep(50); // Sleep for 50 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.err.println("Thread sleep interrupted.");
            }

            choice = scanner.nextLine().toLowerCase();

            // Process user input for navigation.
            if (choice.equals(String.valueOf(Const.LEFT))) {
                currentMoveIdx = (currentMoveIdx - 1 + possibleMovesFromSource.size()) % possibleMovesFromSource.size();
            } else if (choice.equals(String.valueOf(Const.RIGHT))) {
                currentMoveIdx = (currentMoveIdx + 1) % possibleMovesFromSource.size();
            } else if (!choice.equals(String.valueOf(Const.YES))) {
                System.out.println("Invalid input. Please use 'y', 'j', or 'l'.");
            }
        }
        return selectedMove; // Return the confirmed selected move.
    }

    /**
     * Main method for demonstrating the HumanPlayer.
     * Requires a functional Board, Pos2D, Move, PegPicker, and Const setup.
     * This will involve console interaction.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating HumanPlayer ---");

        Board board = new Board(Const.DEFAULT_SIZE, Const.DEFAULT_SIZE);
        // Setup a simple board with some pieces for the human player
        board.addWhitePeg(new Pos2D(1, 1));
        board.addWhitePeg(new Pos2D(1, 3));
        board.addWhitePeg(new Pos2D(2, 2)); // A piece in the middle
        board.addBlackPeg(new Pos2D(4, 1));
        board.addBlackPeg(new Pos2D(4, 3));

        System.out.println("\nInitial Board for Human Player:");
        board.print();

        HumanPlayer humanPlayer = new HumanPlayer(Const.PLAYER1, board);
        Scanner mainScanner = humanPlayer.scanner; // Get the scanner reference to close it

        System.out.println("\nPlayer 1, it's your turn!");
        try {
            humanPlayer.play(); // This will initiate the interactive selection process.
            System.out.println("\nBoard after Human Player move:");
            board.print();
            if (board.getWinner() != null) {
                System.out.println("Game over! Winner: Player " + board.getWinner());
            }
        } catch (Exception e) {
            System.err.println("An error occurred during human player turn: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure the scanner is always closed, even if an exception occurs
            if (mainScanner != null) {
                mainScanner.close();
            }
        }
        System.out.println("\nHuman Player Demonstration finished.");
    }
}

