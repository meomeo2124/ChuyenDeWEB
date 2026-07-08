package exception;

/**
 * Exception khi user không có quyền truy cập
 */
public class UnauthorizedException extends RuntimeException {

    private String userId;
    private String resource;

    public UnauthorizedException(String userId, String resource) {
        super(String.format("User '%s' is not authorized to access '%s'", userId, resource));
        this.userId = userId;
        this.resource = resource;
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public String getUserId() {
        return userId;
    }

    public String getResource() {
        return resource;
    }
}