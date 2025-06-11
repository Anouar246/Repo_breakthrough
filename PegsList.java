import java.util.ArrayList; // For dynamic list functionality
import java.util.HashSet;  // For implementing Iterable
import java.util.Iterator;      // For the List interface
import java.util.List;       // For set operations in hasIntersectionWith
import java.util.Set;   // For the HashSet implementation

// Assumed: Pos2D class is defined and available, with correctly overridden equals() and hashCode().
// Assumed: Move class is defined and available, with correctly overridden equals() and hashCode(),
// and having 'src' and 'dest' (Pos2D) fields.

/**
 * Manages the list of positions for a player's pegs (pieces).
 * This class effectively acts as a container for all active pieces belonging to one player.
 * It implements the Iterable interface, allowing its instances to be used in enhanced for-loops.
 */
public class PegsList implements Iterable<Pos2D> {

    // A private List (specifically an ArrayList) is used to store the Pos2D objects.
    // This is the direct Java equivalent to Python's dynamic list behavior for storage.
    private List<Pos2D> positions;

    /**
     * Constructs a new, empty PegsList.
     * Initializes the internal ArrayList to store the positions of the pegs.
     */
    public PegsList() {
        this.positions = new ArrayList<>();
    }

    /**
     * Adds a peg's position to this list.
     * @param pos The Pos2D object representing the position of the peg to add.
     * This position should typically represent a piece newly placed or existing.
     */
    public void add(Pos2D pos) {
        // Simple addition to the internal ArrayList.
        this.positions.add(pos);
    }

    /**
     * Removes a peg's position from this list.
     * This method relies on Pos2D.equals() to find and remove the correct object.
     * If the position is not found, a warning is printed to the standard error stream.
     * @param pos The Pos2D object representing the position of the peg to remove.
     */
    public void remove(Pos2D pos) {
        // List.remove(Object) returns true if the element was found and removed, false otherwise.
        if (!this.positions.remove(pos)) {
            // In Python, list.remove() raises a ValueError if the item is not found.
            // In Java, we can choose to throw an exception, return a boolean, or log a warning.
            // A warning is chosen here for less disruptive behavior in a game context,
            // but an IllegalArgumentException could also be considered for stricter error handling.
            System.err.println("Warning: Attempted to remove position " + pos + " but it was not found in the list.");
        }
    }

    /**
     * Modifies the position of an existing peg within this list based on a player's move.
     * It finds the peg at `move.src` and updates its position to `move.dest`.
     * @param move The Move object containing the source and destination positions.
     * @throws IllegalArgumentException if the source position of the move is not found among the player's pegs.
     */
    public void move(Move move) {
        // Find the index of the source position (move.src) within the list.
        // This uses the overridden Pos2D.equals() method for comparison.
        int idx = this.positions.indexOf(move.src);

        // If indexOf returns -1, the source position was not found.
        if (idx == -1) {
            throw new IllegalArgumentException("Source position " + move.src + " not found among the player's pegs.");
        }

        // Update the element at the found index to the new destination position.
        // This replaces the old Pos2D object with the new one.
        this.positions.set(idx, move.dest);
    }

    /**
     * Determines if this PegsList has any positions in common with another iterable of Pos2D objects.
     * This method efficiently checks for intersections by converting the current list's positions
     * into a HashSet, which provides average O(1) lookup time for `contains()` operations.
     * @param other An Iterable of Pos2D objects (e.g., another PegsList, or any collection of Pos2D).
     * @return true if there is at least one common position, false otherwise.
     */
    public boolean hasIntersectionWith(Iterable<Pos2D> other) {
        // Create a HashSet from this list's positions.
        // This allows for very fast lookups (contains operation).
        Set<Pos2D> thisPositionsSet = new HashSet<>(this.positions);

        // Iterate through each position in the 'other' iterable.
        for (Pos2D pos : other) {
            // Check if this position exists in our HashSet.
            if (thisPositionsSet.contains(pos)) {
                return true; // Found a common position, so return true immediately.
            }
        }
        return false; // No common positions were found after checking all 'other' positions.
    }

    /**
     * Returns an iterator over the positions in this list.
     * This method is a requirement of the `Iterable<Pos2D>` interface,
     * which enables the use of the enhanced for-loop (for-each loop) in Java.
     * @return An Iterator for Pos2D objects.
     */
    @Override
    public Iterator<Pos2D> iterator() {
        return positions.iterator();
    }

    /**
     * Returns the number of pegs (positions) currently in this list.
     * Corresponds to the `__len__` method in Python.
     * @return The current number of pegs.
     */
    public int size() {
        return this.positions.size();
    }

    /**
     * Returns a string representation of the PegsList.
     * Useful for debugging, showing the contents of the list.
     * @return A string representation including all contained Pos2D objects.
     */
    @Override
    public String toString() {
        return "PegsList: " + positions.toString();
    }

    /**
     * Main method to demonstrate the functionality of the PegsList class.
     * This demonstration assumes that the Pos2D and Move classes are correctly
     * defined and accessible in the same project or imported.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // --- Demonstration Setup ---
        // (Assuming Pos2D and Move are properly defined and available)
        // If not, you would typically define dummy versions here for this main method to run standalone,
        // as done in previous examples. For a full project, they'd be separate files.

        // Dummy/Placeholder for player constants if GameConfig isn't fully available
        final int PLAYER1_ID = 1;
        final int PLAYER2_ID = 2;


        // --- Test PegsList Functionality ---
        PegsList player1Pegs = new PegsList();

        // 1. Test add()
        System.out.println("--- Testing add() ---");
        player1Pegs.add(new Pos2D(0, 0)); // Add (0,0)
        player1Pegs.add(new Pos2D(0, 1)); // Add (0,1)
        player1Pegs.add(new Pos2D(1, 1)); // Add (1,1)
        System.out.println("Player 1 Pegs after adding: " + player1Pegs);
        System.out.println("Size: " + player1Pegs.size()); // Expected: 3

        // 2. Test remove()
        System.out.println("\n--- Testing remove() ---");
        Pos2D posToRemove = new Pos2D(0, 1);
        player1Pegs.remove(posToRemove); // Remove (0,1)
        System.out.println("Player 1 Pegs after removing " + posToRemove + ": " + player1Pegs);
        System.out.println("Size: " + player1Pegs.size()); // Expected: 2
        player1Pegs.remove(new Pos2D(99, 99)); // Attempt to remove a non-existent position
        System.out.println("Player 1 Pegs after trying to remove non-existent: " + player1Pegs);

        // Add a new piece for move testing
        player1Pegs.add(new Pos2D(2, 2));
        System.out.println("Player 1 Pegs before move: " + player1Pegs);

        // 3. Test move()
        System.out.println("\n--- Testing move() ---");
        Pos2D srcForMove = new Pos2D(1, 1); // Source (col, row)
        Pos2D destForMove = new Pos2D(1, 2); // Destination (col, row)
        Move testMove = new Move(srcForMove, destForMove, PLAYER1_ID);

        player1Pegs.move(testMove); // Move peg from (1,1) to (1,2)
        System.out.println("Player 1 Pegs after moving " + testMove + ": " + player1Pegs); // Expected: [(0,0), (1,2), (2,2)]

        // Test move() with non-existent source (should throw exception)
        System.out.println("\n--- Testing move() with non-existent source ---");
        try {
            player1Pegs.move(new Move(new Pos2D(9, 9), new Pos2D(10, 10), PLAYER1_ID));
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // 4. Test hasIntersectionWith()
        System.out.println("\n--- Testing hasIntersectionWith() ---");
        PegsList player2Pegs = new PegsList();
        player2Pegs.add(new Pos2D(1, 2)); // Common position with player1Pegs
        player2Pegs.add(new Pos2D(3, 3));
        System.out.println("Player 2 Pegs: " + player2Pegs);
        System.out.println("Intersection between player1 and player2 pegs? " + player1Pegs.hasIntersectionWith(player2Pegs)); // Expected: true

        PegsList noIntersectionPegs = new PegsList();
        noIntersectionPegs.add(new Pos2D(4, 4));
        noIntersectionPegs.add(new Pos2D(5, 5));
        System.out.println("No Intersection Pegs: " + noIntersectionPegs);
        System.out.println("Intersection between player1 and noIntersection pegs? " + player1Pegs.hasIntersectionWith(noIntersectionPegs)); // Expected: false

        // 5. Test iteration (due to Iterable implementation)
        System.out.println("\n--- Testing iteration ---");
        System.out.print("Iterating through player1Pegs: ");
        for (Pos2D pos : player1Pegs) {
            System.out.print(pos + " ");
        }
        System.out.println();
    }
}
