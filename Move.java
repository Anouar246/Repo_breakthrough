import java.util.Objects; // For Objects.hash()

// Assuming Pos2D class is defined and available.
// It is crucial that Pos2D has correctly overridden equals() and hashCode() methods,
// and has a subtract method, as implied by this Move class.

/**
 * Represents a move of a piece on the board.
 * This class is immutable, meaning its state (source, destination, player)
 * cannot be changed after creation.
 */
public class Move {

    // Final fields are used to ensure immutability, similar to how properties work
    // in Python when values are assigned only during initialization.
    public final Pos2D src;    // Starting position of the moved piece.
    public final Pos2D dest;   // Ending position of the moved piece.
    public final int player; // Identifier for the player making the move (e.g., PLAYER1, PLAYER2).

    /**
     * Constructs a new Move object.
     * @param src The starting position of the piece.
     * @param dest The ending position of the piece.
     * @param player The identifier of the player making this move.
     */
    public Move(Pos2D src, Pos2D dest, int player) {
        // Basic validation for positions
        if (src == null || dest == null) {
            throw new IllegalArgumentException("Source and destination positions cannot be null.");
        }
        this.src = src;
        this.dest = dest;
        this.player = player;
    }

    /**
     * Returns the source position of the move.
     * Corresponds to the 'src' property in the Python code.
     * @return The starting position.
     */
    public Pos2D getSrc() {
        return src;
    }

    /**
     * Returns the destination position of the move.
     * Corresponds to the 'dest' property in the Python code.
     * @return The ending position.
     */
    public Pos2D getDest() {
        return dest;
    }

    /**
     * Returns the player identifier for this move.
     * Corresponds to the 'player' property in the Python code.
     * @return The player identifier.
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Calculates and returns the delta (displacement vector) from source to destination.
     * Corresponds to the 'delta' property in the Python code.
     * Assumes Pos2D has a 'subtract' method (e.g., `dest.subtract(src)`).
     * @return A Pos2D object representing the change in position.
     */
    public Pos2D getDelta() {
        // This relies on the Pos2D class having a 'subtract' method.
        // It should be `dest.subtract(src)` to get dest - src.
        return dest.subtract(src);
    }

    /**
     * Returns a new Move object representing the reverse of this move.
     * The source and destination are swapped, but the player remains the same.
     * Corresponds to the '__reversed__' method in Python.
     * @return A new Move object from destination to source.
     */
    public Move reverse() {
        return new Move(this.dest, this.src, this.player);
    }

    /**
     * Provides a string representation of the Move object.
     * Corresponds to the '__str__' method in Python.
     * @return A formatted string like "<player moves from (col, row) to (col, row)>".
     */
    @Override
    public String toString() {
        return "<" + player + " moves from " + src + " to " + dest + ">";
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two Move objects are considered equal if their source, destination, and player
     * attributes are all equal. This relies on Pos2D.equals() for position comparison.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return player == move.player &&
               src.equals(move.src) &&
               dest.equals(move.dest);
    }

    /**
     * Returns a hash code value for the object.
     * This method must be overridden along with equals() to ensure proper behavior
     * when Move objects are used in hash-based collections (like HashMap or HashSet).
     * The hash code is generated based on the hash codes of its src, dest, and player attributes.
     * @return The hash code for this Move object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(src, dest, player);
    }

    /**
     * Main method for demonstrating the Move class.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // For demonstration, we need a simple Pos2D class.
        // In a real project, you would ensure your Pos2D is available.
        class SimplePos2D {
            public final int row;
            public final int col;

            public SimplePos2D(int row, int col) {
                this.row = row;
                this.col = col;
            }

            public SimplePos2D subtract(SimplePos2D other) {
                return new SimplePos2D(this.row - other.row, this.col - other.col);
            }

            @Override
            public String toString() {
                return "(" + col + ", " + row + ")";
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SimplePos2D that = (SimplePos2D) o;
                return row == that.row && col == that.col;
            }

            @Override
            public int hashCode() {
                return Objects.hash(row, col);
            }
        }

        // Example usage (using SimplePos2D for demonstration purposes)
        SimplePos2D p_src = new SimplePos2D(0, 0); // row 0, col 0
        SimplePos2D p_dest = new SimplePos2D(2, 1); // row 2, col 1
        int player_id = 1; // Assuming 1 represents PLAYER1

        // Create a Move object
        Move move1 = new Move(new Pos2D(p_src.row, p_src.col), new Pos2D(p_dest.row, p_dest.col), player_id);
        System.out.println("Move 1: " + move1); // Expected: <1 moves from (0, 0) to (1, 2)> (Note: Pos2D prints (col, row))

        // Test the reverse move
        Move reversedMove = move1.reverse();
        System.out.println("Reversed Move 1: " + reversedMove); // Expected: <1 moves from (1, 2) to (0, 0)>

        // Test delta
        System.out.println("Delta of Move 1: " + move1.getDelta()); // Expected: (1, 2) (col_diff, row_diff)

        // Test equality
        Move move2 = new Move(new Pos2D(0, 0), new Pos2D(2, 1), 1);
        System.out.println("Move 1 equals Move 2? " + move1.equals(move2)); // Expected: true

        Move move3 = new Move(new Pos2D(0, 0), new Pos2D(2, 1), 2); // Different player
        System.out.println("Move 1 equals Move 3? " + move1.equals(move3)); // Expected: false
    }
}