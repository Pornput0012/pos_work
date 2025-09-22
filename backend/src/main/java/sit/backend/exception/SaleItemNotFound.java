package sit.backend.exception;

public class SaleItemNotFound extends RuntimeException {
    public SaleItemNotFound(Integer id) {
        super("SaleItem not found for this id ::" + " " + id);
    }


}