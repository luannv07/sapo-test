package vn.id.luannv.sapotest.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.luannv.sapotest.dto.request.UserPurchaseRequest;
import vn.id.luannv.sapotest.dto.response.BaseResponse;
import vn.id.luannv.sapotest.services.UserPurchaseService;

@RestController
@RequestMapping("/flash-sale")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderController {
    UserPurchaseService userPurchaseService;

    @PostMapping("/order")
    public ResponseEntity<BaseResponse<Void>> placeOrder(@RequestBody @Valid UserPurchaseRequest request) {
        userPurchaseService.order(request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .success(true)
                .message("Order placed successfully")
                .build());
    }
}
