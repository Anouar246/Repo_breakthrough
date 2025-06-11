// Assuming Pos2D class is defined and available with overridden equals() and hashCode().

/**
 * Represents a numerical matrix of size m x n.
 * It stores elements in a 2D array of doubles.
 */
public class Matrix {

    // Private and final fields to encapsulate the matrix's dimensions and buffer.
    private final int m;        // Number of rows
    private final int n;        // Number of columns
    private final double[][] buffer; // The 2D array storing the matrix elements.

    /**
     * Constructs a new Matrix object with specified dimensions and an initial default value.
     * @param m The number of rows.
     * @param n The number of columns.
     * @param init The default value to initialize all matrix entries with.
     */
    public Matrix(int m, int n, double init) {
        if (m <= 0 || n <= 0) {
            throw new IllegalArgumentException("Matrix dimensions (m, n) must be positive.");
        }
        this.m = m;
        this.n = n;
        this.buffer = new double[m][n]; // Initialize the 2D array.

        // Fill the buffer with the initial value.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                this.buffer[i][j] = init;
            }
        }
    }

    /**
     * Constructs a new Matrix object with specified dimensions, initializing all entries to 0.0.
     * This is a convenience constructor, mimicking the default 'init' value in Python.
     * @param m The number of rows.
     * @param n The number of columns.
     */
    public Matrix(int m, int n) {
        this(m, n, 0.0); // Calls the main constructor with a default init value of 0.0.
    }

    /**
     * Returns the number of rows in the matrix.
     * Corresponds to the 'm' property in Python.
     * @return The number of rows.
     */
    public int getM() {
        return m;
    }

    /**
     * Returns the number of columns in the matrix.
     * Corresponds to the 'n' property in Python.
     * @return The number of columns.
     */
    public int getN() {
        return n;
    }

    /**
     * Returns the shape of the matrix as an array [m, n].
     * Corresponds to the 'shape' property in Python.
     * @return An array of two integers: [number of rows, number of columns].
     */
    public int[] getShape() {
        return new int[]{m, n};
    }

    /**
     * Returns the total number of elements in the matrix (m * n).
     * Corresponds to the 'size' property in Python.
     * @return The total number of elements.
     */
    public int size() {
        return m * n;
    }

    /**
     * Checks if a given position is valid for indexing this matrix.
     * @param pos The Pos2D object representing the coordinates.
     * @return true if the position is within the matrix bounds, false otherwise.
     */
    public boolean isValidPos(Pos2D pos) {
        return pos.row >= 0 && pos.row < m &&
               pos.col >= 0 && pos.col < n;
    }

    /**
     * Retrieves the element at a specific position (row, col) in the matrix.
     * Corresponds to the '__getitem__' method in Python.
     * @param pos The Pos2D object representing the coordinates (row, col).
     * @return The value at the specified position.
     * @throws IndexOutOfBoundsException if the position is not valid.
     */
    public double get(Pos2D pos) {
        // Validate the position before accessing the buffer.
        if (!isValidPos(pos)) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid position for matrix of shape (%d, %d): %s", m, n, pos)
            );
        }
        // Access using row and col.
        return buffer[pos.row][pos.col];
    }

    /**
     * Replaces the element at a specific position (row, col) in the matrix.
     * Corresponds to the '__setitem__' method in Python.
     * @param pos The Pos2D object representing the coordinates (row, col).
     * @param value The new value to set at the specified position.
     * @throws IndexOutOfBoundsException if the position is not valid.
     */
    public void set(Pos2D pos, double value) {
        // Validate the position before modifying the buffer.
        if (!isValidPos(pos)) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid position for matrix of shape (%d, %d): %s", m, n, pos)
            );
        }
        // Set the value using row and col.
        this.buffer[pos.row][pos.col] = value;
    }

    /**
     * Counts the number of occurrences of a given value in the matrix.
     * @param value The value to count.
     * @return The number of occurrences of 'value' in the matrix.
     */
    public int count(double value) {
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // Using a small epsilon for floating-point comparisons if exact equality is not expected.
                // For 'int' values, direct equality (buffer[i][j] == value) is fine.
                if (buffer[i][j] == value) { // For exact equality, as in the Python 'count' for int/float
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Provides a string representation of the matrix.
     * @return A multi-line string representing the matrix content.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Matrix (").append(m).append("x").append(n).append("):\n");
        for (int i = 0; i < m; i++) {
            sb.append("[");
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%7.2f", buffer[i][j])); // Format to 2 decimal places, width 7
                if (j < n - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * Main method for demonstrating the Matrix class.
     * This example assumes that the Pos2D class is correctly defined and accessible.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a 3x4 matrix initialized with 0.0
        Matrix matrix1 = new Matrix(3, 4);
        System.out.println("--- Matrix 1 (3x4, default init 0.0) ---");
        System.out.println(matrix1);
        System.out.println("Shape: " + matrix1.getShape()[0] + "x" + matrix1.getShape()[1]); // Expected: 3x4
        System.out.println("Size: " + matrix1.size()); // Expected: 12
        System.out.println("Count of 0.0: " + matrix1.count(0.0)); // Expected: 12

        // Create a 2x2 matrix initialized with 5.5
        Matrix matrix2 = new Matrix(2, 2, 5.5);
        System.out.println("\n--- Matrix 2 (2x2, init 5.5) ---");
        System.out.println(matrix2);

        // Test set() and get()
        Pos2D p1 = new Pos2D(0, 0); // Row 0, Col 0
        Pos2D p2 = new Pos2D(1, 1); // Row 1, Col 1
        Pos2D p3 = new Pos2D(0, 1); // Row 0, Col 1

        matrix2.set(p1, 10.0);
        matrix2.set(p2, 20.0);
        System.out.println("Matrix 2 after setting (0,0) to 10.0 and (1,1) to 20.0:\n" + matrix2);
        System.out.println("Value at (0,0): " + matrix2.get(p1)); // Expected: 10.0
        System.out.println("Value at (0,1): " + matrix2.get(p3)); // Expected: 5.5 (original init value)

        // Test isValidPos
        System.out.println("\n--- Testing isValidPos ---");
        System.out.println("Is (1,0) valid for matrix2? " + matrix2.isValidPos(new Pos2D(1, 0))); // Expected: true
        System.out.println("Is (2,0) valid for matrix2? " + matrix2.isValidPos(new Pos2D(2, 0))); // Expected: false (out of bounds row)

        // Test invalid position access (should throw exception)
        System.out.println("\n--- Testing invalid access ---");
        try {
            matrix2.get(new Pos2D(2, 0));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Test count() after changes
        System.out.println("\n--- Testing count() ---");
        System.out.println("Count of 5.5 in matrix2: " + matrix2.count(5.5)); // Expected: 2 (since two values were changed)
        System.out.println("Count of 10.0 in matrix2: " + matrix2.count(10.0)); // Expected: 1
    }
}
