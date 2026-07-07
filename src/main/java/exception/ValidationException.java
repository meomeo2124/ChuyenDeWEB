package exception;

/**
 * Exception khi dữ liệu validate không hợp lệ
 */
public class ValidationException extends RuntimeException {

    private String fieldName;
    private Object rejectedValue;

    public ValidationException(String fieldName, Object rejectedValue, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", fieldName, rejectedValue, message));
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }

    public ValidationException(String message) {
        super(message);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}