package sit.integrated.exceptions;

public class SaleItemNotFound extends RuntimeException {
    public SaleItemNotFound(Integer id) {
        super("SaleItem with ID " + id + " not found.");
    }


}
