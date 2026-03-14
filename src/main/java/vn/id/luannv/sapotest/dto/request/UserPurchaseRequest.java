package vn.id.luannv.sapotest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPurchaseRequest {
    @NotNull(message = "Product ID is required")
    Long productId;
    @NotBlank(message = "User ID is required")
    String userId;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity;
}