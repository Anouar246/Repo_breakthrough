import java.io.BufferedReader; // For reading text from input streams
import java.io.FileReader;    // For reading from files
import java.io.IOException;   // For handling I/O exceptions
import java.util.ArrayList;   // For dynamic lists
import java.util.List;        // For list interface

// Assuming all necessary classes are available:
// - Board (with its internal HistoryEntry class)
// - Const (with DEFAULT_SIZE, EMPTY, PLAYER1, PLAYER2, CHARS constants)
// - Player (abstract base class)
// - HumanPlayer
// - MinimaxAiPlayer (and potentially RandomAiPlayer, GreedyAiPlayer if needed)
// - Pos2D (with add, subtract, equals, hashCode)
// - BreakthroughError, BadFormatError, InvalidPositionError, EmptyHistoryError (custom exceptions)

/**
 * The main class for the Breakthrough game.
 * It manages the game board, players (human or AI), and the overall game flow.
 */
public class Breakthrough {

    private Board board;          // The game board instance.
    private List<Player> players; // List of players, where index 0 is Player1 and index 1 is Player2.

    /**
     * Constructs a new Breakthrough game instance.
     * Initializes the game board and sets up the players.
     *
     * @param path Optional: Path to a file to load the board configuration from. If null, a default board is created.
     * @param player2IsAi If true, Player 2 will be an AI (MinimaxAiPlayer). Otherwise, Player 2 will be HumanPlayer.
     * @throws BadFormatError If the board file is malformed.
     * @throws IOException If there's an issue reading the board file.
     * @throws InvalidPositionError If initial peg positions in the file are invalid.
     */
    public Breakthrough(String path, boolean player2IsAi) throws BadFormatError, IOException, InvalidPositionError {
        // Initialize the board based on whether a path is provided or if it's a default setup.
        if (path == null) {
            this.makeDefaultBoard();
        } else {
            this.makeBoardFromFile(path);
        }

        // Initialize players. Player 1 is always Human. Player 2 can be Human or AI.
        // We'll use the specific classes directly here.
        Player player1 = new HumanPlayer(Const.PLAYER1, this.board);
        Player player2;

        if (player2IsAi) {
            player2 = new MinimaxAiPlayer(Const.PLAYER2, this.board);
        } else {
            player2 = new HumanPlayer(Const.PLAYER2, this.board);
        }

        this.players = new ArrayList<>();
        this.players.add(player1); // Add Player 1 (index 0)
        this.players.add(player2); // Add Player 2 (index 1)
    }

    /**
     * Private helper method to construct the game board from a file.
     *
     * @param path The path to the board configuration file.
     * @throws BadFormatError If the file's content is incorrect or malformed.
     * @throws IOException If an I/O error occurs during file reading.
     * @throws InvalidPositionError If positions read from the file are invalid for adding pegs.
     */
    private void makeBoardFromFile(String path) throws BadFormatError, IOException, InvalidPositionError {
        // Use try-with-resources to ensure the BufferedReader is closed automatically.
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // Read board dimensions (rows, cols) from the first line.
            String dimensionsLine = reader.readLine();
            if (dimensionsLine == null) {
                throw new BadFormatError("File is empty or malformed: missing dimensions.");
            }
            String[] dimensions = dimensionsLine.trim().split(" ");
            if (dimensions.length != 2) {
                throw new BadFormatError("Invalid dimensions format: expected 'rows cols'.");
            }
            int rows, cols;
            try {
                rows = Integer.parseInt(dimensions[0]);
                cols = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                throw new BadFormatError("Invalid dimension numbers: " + e.getMessage());
            }

            this.board = new Board(rows, cols); // Create the board.

            // Read white pegs positions from the second line.
            String whitePegsLine = reader.readLine();
            if (whitePegsLine == null) {
                throw new BadFormatError("File is malformed: missing white pegs line.");
            }
            for (Pos2D position : findPositionsFromLine(whitePegsLine.trim())) {
                this.board.addWhitePeg(position);
            }

            // Read black pegs positions from the third line.
            String blackPegsLine = reader.readLine();
            if (blackPegsLine == null) {
                throw new BadFormatError("File is malformed: missing black pegs line.");
            }
            for (Pos2D position : findPositionsFromLine(blackPegsLine.trim())) {
                this.board.addBlackPeg(position);
            }
        }

        // After populating the board, check its integrity.
        if (!this.board.checkIntegrity()) {
            throw new BadFormatError("Board integrity check failed for file: " + path);
        }
    }

    /**
     * Private helper method to parse positions from a single line of the file.
     * Expects format: "<col><row>(,<col><row>)*" (e.g., "a1,b2,c3").
     *
     * @param line The string line to parse.
     * @return A List of Pos2D objects parsed from the line.
     * @throws BadFormatError If a position string is malformed.
     */
    private List<Pos2D> findPositionsFromLine(String line) throws BadFormatError {
        List<Pos2D> positions = new ArrayList<>();
        if (line.isEmpty()) { // Handle empty lines gracefully (no pegs)
            return positions;
        }

        String[] posStrings = line.split(",");
        for (String posStr : posStrings) {
            if (posStr.length() < 2) {
                throw new BadFormatError("Malformed position string: " + posStr + ". Expected format like 'a1'.");
            }
            char colChar = posStr.charAt(0);
            String rowNumStr = posStr.substring(1);

            int col = colChar - 'a'; // Convert 'a' -> 0, 'b' -> 1, etc.
            int row;
            try {
                // Convert 1-based row number from file to 0-based matrix row index.
                // Python: self.board_.m - int(pos[1:])
                // Example: if board height is 6 (m=6) and file has "1" (row 1),
                // it's the last row of the board: 6 - 1 = 5.
                row = this.board.getM() - Integer.parseInt(rowNumStr);
            } catch (NumberFormatException e) {
                throw new BadFormatError("Invalid row number in position: " + posStr + ". " + e.getMessage());
            }

            positions.add(new Pos2D(row, col));
        }
        return positions;
    }

    /**
     * Private helper method to construct a default square board (DEFAULT_SIZE x DEFAULT_SIZE).
     * Populates the first two rows (from top) with black pegs and the last two rows (from bottom) with white pegs.
     */
    private void makeDefaultBoard() {
        this.board = new Board(Const.DEFAULT_SIZE, Const.DEFAULT_SIZE);
        // Player 1 (White) pegs: placed on the bottom two rows (row indices DEFAULT_SIZE-1 and DEFAULT_SIZE-2).
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < Const.DEFAULT_SIZE; j++) {
                this.board.addWhitePeg(new Pos2D(Const.DEFAULT_SIZE - 1 - i, j));
            }
        }
        // Player 2 (Black) pegs: placed on the top two rows (row indices 0 and 1).
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < Const.DEFAULT_SIZE; j++) {
                this.board.addBlackPeg(new Pos2D(i, j));
            }
        }
    }

    /**
     * Plays the Breakthrough game.
     * The game loop continues until a winner is determined.
     */
    public void play() {
        this.board.print(); // Print initial board state.
        int currentPlayerIndex = 1; // Start with Player 2 to match Python's `current = 1-current` which switches to 0 (P1)

        while (this.getWinner() == null) { // Loop while there is no winner.
            // Switch current player (0 for Player 1, 1 for Player 2).
            // Python's `current = 1 - current` effectively toggles between 0 and 1.
            currentPlayerIndex = 1 - currentPlayerIndex;

            // Get the current player object and ask them to make a move.
            this.players.get(currentPlayerIndex).play();

            // Print the board after the move.
            this.board.print();
            System.out.println(""); // Add an empty line for spacing.
        }
    }

    /**
     * Returns the winner of the game.
     * This is a convenient alias to the board's winner property.
     * @return The ID of the winning player (Const.PLAYER1 or Const.PLAYER2), or null if no winner yet.
     */
    public Integer getWinner() {
        return this.board.getWinner();
    }

    /**
     * Main method to run the Breakthrough game.
     * This method handles game setup and calls the play method.
     *
     * @param args Command line arguments.
     * - args[0] (optional): Path to a board configuration file.
     * - args[1] (optional): "ai" if Player 2 should be an AI, otherwise treated as human.
     */
    public static void main(String[] args) {
        String boardFilePath = null;
        boolean player2IsAi = false;

        // Parse command line arguments.
        // Check for file path: The Python script checks if argv[1] is a file.
        // In Java, we'll check if args.length > 0 and assume args[0] is path if it exists.
        // The file existence check will happen when FileReader tries to open it.
        if (args.length > 0) {
            // In Python, isfile is checked. In Java, FileReader will throw IOException if not found.
            // So we just assign the path if provided.
            boardFilePath = args[0];
        }
        // Check for '-ai' flag: Python's '-ai' in argv.
        // In Java, check if any argument is "ai" (case-insensitive).
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-ai")) {
                player2IsAi = true;
                break; // Found the flag, no need to check further.
            }
        }


        try {
            // Create a new Breakthrough game instance.
            Breakthrough game = new Breakthrough(boardFilePath, player2IsAi);
            System.out.println("--- Breakthrough Game Started ---");
            game.play(); // Start the game loop.

            // Announce the winner using Const.CHARS
            Integer winner = game.getWinner();
            if (winner != null) {
                System.out.println("Player " + Const.CHARS[winner] + " won!");
            } else {
                System.out.println("The game ended without a clear winner."); // Should not happen if game loop exits on winner.
            }

        } catch (BadFormatError e) {
            System.err.println("Game initialization error: " + e.getMessage());
            // No stack trace needed for expected format errors
        } catch (IOException e) {
            System.err.println("File I/O error: Could not read board file '" + boardFilePath + "'. " + e.getMessage());
            e.printStackTrace(); // Print stack trace for I/O issues.
        } catch (InvalidPositionError e) {
            System.err.println("Game setup error: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for invalid positions during setup.
        } catch (IllegalStateException e) {
            // This might catch errors from HumanPlayer if no selectable pegs/moves.
            System.err.println("Game logic error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Catch any other unexpected errors during gameplay.
            System.err.println("An unexpected error occurred during the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}