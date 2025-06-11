import java.util.Objects; // Required for Objects.hash()
import java.lang.Math; 

/**
 * Represents a 2-dimensional position (row, col).
 * This class is immutable, meaning its state cannot be changed after creation.
 * It also implements Comparable for natural ordering, useful for sorting or
 * using in sorted collections.
 */
public class Pos2D implements Comparable<Pos2D> {

    // Public final fields for immutable objects are a common pattern in Java
    // They are set once in the constructor and cannot be changed thereafter.
    public final int row; // row/vertical position
    public final int col; // col/horizontal position

    /**
     * Constructs a new Pos2D object with the given row and column.
     * In the Python original, the constructor was Pos2D(row, col)
     * and internally mapped to x_=col, y_=row.
     * Here, we directly use row and col as the primary coordinates.
     * @param row The vertical position.
     * @param col The horizontal position.
     */
    public Pos2D(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Provides the horizontal component (column) of the position.
     * Corresponds to the 'x' property in the original Python code.
     * @return The horizontal position (column).
     */
    public int getX() {
        return this.col;
    }

    /**
     * Provides the vertical component (row) of the position.
     * Corresponds to the 'y' property in the original Python code.
     * @return The vertical position (row).
     */
    public int getY() {
        return this.row;
    }

    /**
     * Calculates the distance to another Pos2D object.
     * @param other The other Pos2D object.
     * @param manhattan If true, calculates Manhattan distance; otherwise, Euclidean distance.
     * @return The calculated distance.
     */
    public double distTo(Pos2D other, boolean manhattan) {
        // Calculate the delta (difference) between this position and the other.
        Pos2D delta = this.subtract(other);

        // Return Manhattan distance if requested, otherwise Euclidean distance.
        if (manhattan) {
            return Math.abs(delta.row) + Math.abs(delta.col);
        } else {
            return Math.hypot(delta.col, delta.row); // Math.hypot(x, y)
        }
    }

    /**
     * Calculates the Euclidean distance to another Pos2D object.
     * Overload for convenience, assuming Euclidean distance by default.
     * @param other The other Pos2D object.
     * @return The Euclidean distance.
     */
    public double distTo(Pos2D other) {
        return distTo(other, false); // Calls the other distTo method with manhattan=false
    }

    /**
     * Returns a new Pos2D object representing the difference between this position and another.
     * Corresponds to the '__sub__' operator in Python.
     * @param other The other Pos2D object to subtract.
     * @return A new Pos2D object (this.row - other.row, this.col - other.col).
     *
     * Note: The original Python code handled tuple input. In Java, for type safety,
     * this method expects another Pos2D. If you need to subtract by raw (col, row)
     * integer pairs, you would create a separate method or convert the tuple to Pos2D first.
     */
    public Pos2D subtract(Pos2D other) {
        return new Pos2D(this.row - other.row, this.col - other.col);
    }

    /**
     * Returns a new Pos2D object representing the sum of this position and another.
     * Corresponds to the '__add__' operator in Python.
     * @param other The other Pos2D object to add.
     * @return A new Pos2D object (this.row + other.row, this.col + other.col).
     *
     * Note: Similar to subtract, this method expects another Pos2D for type safety.
     */
    public Pos2D add(Pos2D other) {
        return new Pos2D(this.row + other.row, this.col + other.col);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Overrides the default equals() method to compare Pos2D objects based on their
     * row and col values. Essential for correct behavior in collections.
     * @param o The object to compare with.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // If the object is the same instance, they are equal.
        if (this == o) return true;
        // If the object is null or not of the same class, they are not equal.
        if (o == null || getClass() != o.getClass()) return false;
        // Cast the object to Pos2D and compare row and col.
        Pos2D pos2D = (Pos2D) o;
        return row == pos2D.row && col == pos2D.col;
    }

    /**
     * Returns a hash code value for the object.
     * Overrides the default hashCode() method. This method must be overridden
     * whenever equals() is overridden to maintain the contract between them.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col); // Generates a hash based on row and col.
    }

    /**
     * Compares this object with the specified object for order.
     * Implements the Comparable interface, allowing Pos2D objects to be naturally ordered.
     * The comparison prioritizes 'row' then 'col', matching the Python '__lt__' behavior.
     * @param other The Pos2D object to be compared.
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Pos2D other) {
        if (this.row != other.row) {
            return Integer.compare(this.row, other.row);
        }
        return Integer.compare(this.col, other.col);
    }

    /**
     * Returns a string representation of the Pos2D object.
     * Corresponds to the '__str__' method in Python.
     * @return A string in the format "(col, row)".
     */
    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }

    /**
     * Main method for demonstrating the Pos2D class.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Pos2D p1 = new Pos2D(1, 2); // row 1, col 2
        Pos2D p2 = new Pos2D(4, 5); // row 4, col 5
        Pos2D p3 = new Pos2D(1, 2); // Another object with same coordinates as p1
        Pos2D p4 = new Pos2D(1, 3); // Different column

        System.out.println("p1: " + p1); // (2, 1) - because x is col, y is row
        System.out.println("p2: " + p2); // (5, 4)

        // Test equality
        System.out.println("p1 == p3: " + p1.equals(p3)); // true
        System.out.println("p1 == p2: " + p1.equals(p2)); // false

        // Test subtraction
        Pos2D diff = p2.subtract(p1);
        System.out.println("p2 - p1: " + diff); // (3, 3) --> (col_diff, row_diff)

        // Test addition
        Pos2D sum = p1.add(p2);
        System.out.println("p1 + p2: " + sum); // (7, 5) --> (col_sum, row_sum)

        // Test distance
        System.out.println("Euclidean distance from p1 to p2: " + p1.distTo(p2)); // sqrt(3^2 + 3^2) = sqrt(18) = ~4.24
        System.out.println("Manhattan distance from p1 to p2: " + p1.distTo(p2, true)); // 3 + 3 = 6

        // Test comparison (compareTo)
        System.out.println("p1 < p2: " + (p1.compareTo(p2) < 0)); // true
        System.out.println("p2 < p1: " + (p2.compareTo(p1) < 0)); // false
        System.out.println("p1 < p4: " + (p1.compareTo(p4) < 0)); // true (same row, p1.col < p4.col)
    }
}
