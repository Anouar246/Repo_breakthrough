
// Assuming Pos2D class is already defined and available,
// as it's used in the VALID_MOVES constant.
// Ensure your Pos2D class has correctly implemented equals(), hashCode(), and subtract() methods.

/**
 * Const class holds all the constant configurations for the game.
 * All fields are public static final, making them accessible globally without
 * needing an instance of the class, and their values cannot be changed.
 */
public class Const {

    public static final int DEFAULT_SIZE = 6;

    // Board Encoding
    public static final int EMPTY = 0;
    public static final int PLAYER1 = 1; // Represents Player 1
    public static final int PLAYER2 = 2; // Represents Player 2

    // For print_board / display characters
    // ALPHABET: Generates a string "abcdef...z" similar to Python's ''.join(map(chr, range(a, z+1)))
    public static final String ALPHABET;
    static {
        // A static initializer block is used to initialize static final fields that require more complex logic.
        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append(c);
        }
        ALPHABET = sb.toString();
    }

    // ANSI Escape Codes for console coloring.
    // NOTE: These codes are platform-dependent and may not work on all terminal emulators
    // (e.g., standard Windows Command Prompt). They generally work well on Linux/macOS
    // terminals and modern terminals like VS Code's integrated terminal.
    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_GREEN = "\033[32m";
    public static final String ANSI_WHITE_TEXT = "\033[37m"; // Default white foreground, for contrast.

    // CHARS: Array of strings representing characters for display, potentially with ANSI colors.
    public static final String[] CHARS;
    static {
        // Checks if the operating system name contains "linux" (case-insensitive)
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            CHARS = new String[]{
                ANSI_BOLD + "." + ANSI_RESET,                         // EMPTY (bold dot)
                ANSI_BOLD + ANSI_GREEN + "W" + ANSI_WHITE_TEXT + ANSI_RESET, // PLAYER1 (bold green 'W')
                ANSI_BOLD + ANSI_RED + "B" + ANSI_WHITE_TEXT + ANSI_RESET    // PLAYER2 (bold red 'B')
            };
        } else {
            // Fallback for non-Linux platforms or terminals that don't support ANSI codes.
            CHARS = new String[]{".", "W", "B"};
        }
    }

    // For is_valid_direction
    // VALID_MOVES: A 2D array of Pos2D objects representing valid move offsets for each player.
    // The first dimension corresponds to player (index 0 for player 1, index 1 for player 2).
    // The second dimension holds the specific Pos2D offsets for moves.
    public static final Pos2D[][] VALID_MOVES = {
        // Player 1 (W) moves (equivalent to ((-1, -1), (-1, 0), (-1, 1))) - generally moves "up" the board
        {new Pos2D(-1, -1), new Pos2D(-1, 0), new Pos2D(-1, 1)},
        // Player 2 (B) moves (equivalent to ((1, -1), (1, 0), (1, 1))) - generally moves "down" the board
        {new Pos2D(1, -1), new Pos2D(1, 0), new Pos2D(1, 1)}
    };

    // Infinity constants using Java's Double class
    public static final double INF = Double.POSITIVE_INFINITY;
    public static final double POS_INF = Double.POSITIVE_INFINITY;
    public static final double NEG_INF = Double.NEGATIVE_INFINITY;

    // Minimax scores
    public static final int DRAW = 0;
    public static final int WIN = 100;
    public static final int LOSS = -100;

    // User input constants (using char for single characters)
    public static final char YES = 'y';
    public static final char LEFT = 'j';
    public static final char RIGHT = 'l';
    public static final char UP = 'i';
    public static final char DOWN = 'k';

    /**
     * Main method to demonstrate the usage of these constants.
     * This method is for testing purposes and can be removed in a production application.
     * @param args Command line arguments (not used here).
     */
    public static void main(String[] args) {
        System.out.println("Default Size: " + Const.DEFAULT_SIZE);
        System.out.println("Player 1 Identifier: " + Const.PLAYER1);
        System.out.println("Alphabet String: " + Const.ALPHABET);
        System.out.println("Character for EMPTY: " + Const.CHARS[Const.EMPTY]);
        System.out.println("Character for PLAYER1: " + Const.CHARS[Const.PLAYER1]);
        System.out.println("Character for PLAYER2: " + Const.CHARS[Const.PLAYER2]);
        System.out.println("Player 1's first valid move offset: " + Const.VALID_MOVES[0][0]);
        System.out.println("Positive Infinity: " + Const.POS_INF);
        System.out.println("Win Score: " + Const.WIN);
        System.out.println("Key for 'UP': " + Const.UP);
    }
}
