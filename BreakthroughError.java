/**
 * Base exception class for all custom errors specific to the Breakthrough game.
 * Extends RuntimeException, making it an unchecked exception.
 * This means methods throwing this exception (or its subclasses) are not
 * required to declare it in their 'throws' clause.
 */
public class BreakthroughError extends RuntimeException {
    /**
     * Constructs a new BreakthroughError with the specified detail message.
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public BreakthroughError(String message) {
        super(message); // Call the constructor of the parent class (RuntimeException)
    }
}

/**
 * Exception thrown when an operation involves an invalid position (e.g., out of bounds).
 * This is a specific type of BreakthroughError.
 */
class InvalidPositionError extends BreakthroughError {
    /**
     * Constructs a new InvalidPositionError with the specified detail message.
     * @param message The detail message.
     */
    public InvalidPositionError(String message) {
        super(message); // Call the constructor of the parent class (BreakthroughError)
    }
}

/**
 * Exception thrown when an operation requires history but the history is empty.
 * This is a specific type of BreakthroughError.
 */
class EmptyHistoryError extends BreakthroughError {
    /**
     * Constructs a new EmptyHistoryError with the specified detail message.
     * @param message The detail message.
     */
    public EmptyHistoryError(String message) {
        super(message); // Call the constructor of the parent class (BreakthroughError)
    }
}

/**
 * Exception thrown when input data (e.g., from a file or user) is in an unexpected or bad format.
 * This is a specific type of BreakthroughError.
 */
class BadFormatError extends BreakthroughError {
    /**
     * Constructs a new BadFormatError with the specified detail message.
     * @param message The detail message.
     */
    public BadFormatError(String message) {
        super(message); // Call the constructor of the parent class (BreakthroughError)
    }
}

/**
 * Main method for demonstrating the usage of these custom exception classes.
 * This is for testing purposes.
 *
class ErrorDemo {
    public static void main(String[] args) {
        System.out.println("--- Demonstrating BreakthroughError and its subclasses ---");

        // Example of throwing and catching BreakthroughError
        try {
            throw new BreakthroughError("Something generic went wrong in Breakthrough.");
        } catch (BreakthroughError e) {
            System.out.println("Caught BreakthroughError: " + e.getMessage());
        }

        // Example of throwing and catching InvalidPositionError
        try {
            throw new InvalidPositionError("Piece cannot move to (X,Y) - invalid coordinates.");
        } catch (InvalidPositionError e) {
            System.out.println("Caught InvalidPositionError: " + e.getMessage());
        }

        // Example of throwing and catching EmptyHistoryError
        try {
            throw new EmptyHistoryError("Cannot undo move: history is empty.");
        } catch (EmptyHistoryError e) {
            System.out.println("Caught EmptyHistoryError: " + e.getMessage());
        }

        // Example of throwing and catching BadFormatError
        try {
            throw new BadFormatError("The input file has an unrecognized format.");
        } catch (BadFormatError e) {
            System.out.println("Caught BadFormatError: " + e.getMessage());
        }

        // You can also catch specific exceptions first, then the more general ones.
        // Or just catch the base BreakthroughError if you don't need to differentiate.
        System.out.println("\n--- Demonstrating catching base exception ---");
        try {
            // This could be any of the specific errors
            if (Math.random() > 0.5) {
                throw new InvalidPositionError("Random invalid position error.");
            } else {
                throw new EmptyHistoryError("Random empty history error.");
            }
        } catch (BreakthroughError e) {
            System.out.println("Caught a general BreakthroughError: " + e.getMessage());
            System.out.println("Type of error: " + e.getClass().getSimpleName());
        }
    }
}
**/