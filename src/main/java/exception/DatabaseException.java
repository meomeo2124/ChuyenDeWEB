package exception;

/**
 * Exception khi có lỗi database
 */
public class DatabaseException extends RuntimeException {

    private String operation; // INSERT, UPDATE, DELETE, SELECT
    private String tableName;

    public DatabaseException(String operation, String tableName, String message, Throwable cause) {
        super(String.format("Database %s failed on table '%s': %s", operation, tableName, message), cause);
        this.operation = operation;
        this.tableName = tableName;
    }

    public DatabaseException(String message) {
        super(message);
    }

    public String getOperation() {
        return operation;
    }

    public String getTableName() {
        return tableName;
    }
}