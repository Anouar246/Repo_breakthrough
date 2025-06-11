import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections; // For Collections.sort
import java.util.List;  // For custom sorting

// Assuming Pos2D class is defined with row, col, x, y accessors, equals, hashCode, and distTo methods.

/**
 * Internal representation for human player's piece selection I/O.
 * It helps a human player navigate and select a piece on the board.
 */
public class PegPicker {

    // List of lists, where each inner list contains pegs in a specific row, sorted by column.
    // This structure helps with 'up' and 'down' navigation across rows.
    private final List<List<Pos2D>> pegsByRow;
    private int currentRowIdx; // Index of the currently selected row in pegsByRow.
    private int currentPegIdx; // Index of the currently selected peg within the current row.

    /**
     * Constructs a PegPicker for a human player.
     * It organizes the player's pegs by row for easier selection.
     * @param boardHeight The total number of rows on the board (used for array sizing).
     * @param playerPegs An Iterable of Pos2D objects representing the player's pieces.
     * This typically comes from a filtered list of valid moves.
     * @throws IllegalArgumentException if no selectable pegs are provided.
     */
    public PegPicker(int boardHeight, Iterable<Pos2D> playerPegs) {
        // 1. Create a temporary list of lists to build the structure.
        List<List<Pos2D>> tempPegsByRow = new ArrayList<>(boardHeight);
        for (int i = 0; i < boardHeight; i++) {
            tempPegsByRow.add(new ArrayList<>());
        }

        // 2. Distribute the player's pegs into the correct row lists within tempPegsByRow.
        for (Pos2D peg : playerPegs) {
            if (peg.row >= 0 && peg.row < boardHeight) { // Basic sanity check for valid row index
                tempPegsByRow.get(peg.row).add(peg);
            }
        }

        // 3. Sort the pegs within each row by their column index (x-coordinate).
        // This relies on Pos2D implementing Comparable or providing a custom Comparator.
        // Assuming Pos2D.compareTo() sorts by row then by col (x), this is correct.
        for (List<Pos2D> rowList : tempPegsByRow) {
            Collections.sort(rowList);
        }

        // 4. Filter out empty rows and create the final list of lists with only active rows.
        List<List<Pos2D>> finalFilteredPegsByRow = new ArrayList<>();
        for (List<Pos2D> rowList : tempPegsByRow) {
            if (!rowList.isEmpty()) {
                finalFilteredPegsByRow.add(rowList);
            }
        }

        // 5. Check if any selectable pegs were found. If not, this is an invalid state.
        if (finalFilteredPegsByRow.isEmpty()) {
            throw new IllegalArgumentException("PegPicker initialized with no selectable pegs.");
        }

        // 6. This is the ONLY assignment to the final 'pegsByRow' field.
        // The reference to the List of Lists is now permanently set.
        this.pegsByRow = finalFilteredPegsByRow;


        // Initialize current selection to the first peg in the first available row.
        this.currentRowIdx = 0;
        this.currentPegIdx = 0;
    }

    /**
     * Returns the currently selected peg's position.
     * @return The Pos2D object of the current selection.
     */
    public Pos2D getCurrent() {
        // Ensure there are pegs to select before attempting to get the current one.
        // This scenario should ideally be prevented by the constructor's IllegalArgumentException.
        if (pegsByRow.isEmpty()) {
            throw new IllegalStateException("PegPicker has no active pegs to select from.");
        }
        return this.pegsByRow.get(currentRowIdx).get(currentPegIdx);
    }

    /**
     * Moves the selection to the first available peg to the left within the current row.
     * Cycles to the rightmost peg if moving past the leftmost.
     */
    public void left() {
        List<Pos2D> currentRow = pegsByRow.get(currentRowIdx);
        // Ensure the current row is not empty before attempting modulo.
        if (currentRow.isEmpty()) {
            this.currentPegIdx = 0; // Fallback to 0 if somehow empty, though should be filtered.
            return;
        }
        this.currentPegIdx = (this.currentPegIdx - 1 + currentRow.size()) % currentRow.size();
    }

    /**
     * Moves the selection to the first available peg to the right within the current row.
     * Cycles to the leftmost peg if moving past the rightmost.
     */
    public void right() {
        List<Pos2D> currentRow = pegsByRow.get(currentRowIdx);
        // Ensure the current row is not empty before attempting modulo.
        if (currentRow.isEmpty()) {
            this.currentPegIdx = 0; // Fallback to 0 if somehow empty, though should be filtered.
            return;
        }
        this.currentPegIdx = (this.currentPegIdx + 1) % currentRow.size();
    }

    /**
     * Moves the selection to a peg in the row above the current row.
     * It finds the peg in the new row that is closest (Manhattan distance) to the previously selected peg.
     * Cycles to the bottom-most row if moving past the top-most.
     */
    public void up() {
        Pos2D previousPeg = getCurrent(); // Store the current peg before changing row.
        this.currentRowIdx = (this.currentRowIdx - 1 + pegsByRow.size()) % pegsByRow.size();
        _findClosest(previousPeg); // Find the closest peg in the new row.
    }

    /**
     * Moves the selection to a peg in the row below the current row.
     * It finds the peg in the new row that is closest (Manhattan distance) to the previously selected peg.
     * Cycles to the top-most row if moving past the bottom-most.
     */
    public void down() {
        Pos2D previousPeg = getCurrent(); // Store the current peg before changing row.
        this.currentRowIdx = (this.currentRowIdx + 1) % pegsByRow.size();
        _findClosest(previousPeg); // Find the closest peg in the new row.
    }

    /**
     * Determines the peg in the currently selected row that is closest to a given target position.
     * Updates `currentPegIdx` to point to this closest peg.
     * @param target The Pos2D position to compare distances against.
     */
    private void _findClosest(Pos2D target) {
        List<Pos2D> currentRow = pegsByRow.get(currentRowIdx);
        // This check should prevent issues if the list was somehow empty,
        // though constructor aims to prevent this.
        if (currentRow.isEmpty()) {
            this.currentPegIdx = 0;
            return;
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int closestIdx = 0;

        for (int i = 0; i < currentRow.size(); i++) {
            Pos2D peg = currentRow.get(i);
            // Calculate Manhattan distance. Assumes Pos2D.distTo(other, manhattan=true) exists.
            double currentDistance = peg.distTo(target, true);

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestIdx = i;
            }
        }
        this.currentPegIdx = closestIdx;
    }

    /**
     * Static helper method to find the index of the minimum value in a list of numbers.
     * Corresponds to the 'argmin' function in Python.
     * @param array The list of numerical values.
     * @return The index `i` such that `array.get(i)` is the minimum value.
     * @throws IllegalArgumentException if the input list is empty.
     */
    public static int argmin(List<? extends Number> array) {
        if (array == null || array.isEmpty()) {
            throw new IllegalArgumentException("Input array for argmin cannot be empty or null.");
        }

        int idx = 0;
        double min_value = array.get(0).doubleValue(); // Get initial min value from first element

        for (int i = 1; i < array.size(); i++) {
            double currentValue = array.get(i).doubleValue();
            if (currentValue < min_value) {
                min_value = currentValue;
                idx = i;
            }
        }
        return idx;
    }

    /**
     * Main method for demonstrating the PegPicker class.
     * This requires Pos2D to be available.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Demonstrating PegPicker ---");

        // Create some dummy pegs for a player (e.g., PLAYER1)
        List<Pos2D> dummyPegs = new ArrayList<>();
        dummyPegs.add(new Pos2D(1, 1)); // row 1, col 1
        dummyPegs.add(new Pos2D(1, 3)); // row 1, col 3
        dummyPegs.add(new Pos2D(3, 0)); // row 3, col 0
        dummyPegs.add(new Pos2D(3, 4)); // row 3, col 4
        dummyPegs.add(new Pos2D(0, 2)); // row 0, col 2

        // Create a PegPicker (assuming board height of 6)
        PegPicker picker = new PegPicker(6, dummyPegs);

        System.out.println("Initial selection: " + picker.getCurrent());
        // Expected initial selection depends on Pos2D's compareTo and constructor.
        // Given Pos2D(row,col) where col is x and row is y:
        // (0,2) is (x=2, y=0)
        // (1,1) is (x=1, y=1)
        // (1,3) is (x=3, y=1)
        // (3,0) is (x=0, y=3)
        // (3,4) is (x=4, y=3)
        // Sorted by row then col: (0,2), (1,1), (1,3), (3,0), (3,4)
        // Initial selection should be (2,0) if Pos2D prints (col, row), or (0,2) if it prints (row, col)

        // Test left/right
        picker.right();
        System.out.println("After right: " + picker.getCurrent());
        picker.left();
        System.out.println("After left: " + picker.getCurrent());

        // Test up/down
        picker.down();
        System.out.println("After down: " + picker.getCurrent());
        picker.up();
        System.out.println("After up: " + picker.getCurrent());

        // Test argmin
        List<Double> distances = Arrays.asList(5.0, 1.0, 8.0, 2.0);
        System.out.println("Argmin of " + distances + ": " + PegPicker.argmin(distances)); // Expected: 1
        List<Integer> counts = Arrays.asList(10, 3, 20, 5);
        System.out.println("Argmin of " + counts + ": " + PegPicker.argmin(counts)); // Expected: 1
    }
}
