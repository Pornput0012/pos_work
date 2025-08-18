package sit.backend.exception;

public class BrandNotFound extends RuntimeException {
    public BrandNotFound(Integer id) {
        super("Brand not found with id: " + id);
    }
}
