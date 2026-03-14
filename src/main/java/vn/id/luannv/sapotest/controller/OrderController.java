package vn.id.luannv.sapotest.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import vn.id.luannv.sapotest.dto.request.UserPurchaseRequest;
import vn.id.luannv.sapotest.dto.response.BaseResponse;
import vn.id.luannv.sapotest.services.UserPurchaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flash-sale")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CrossOrigin("*")
public class OrderController {
    UserPurchaseService userPurchaseService;
    // ### CHÚ Ý: 1 số api bổ trợ để test, phục vụ hiển thị của bài test nên sẽ viết gộp toàn bộ ở đây
    JdbcTemplate jdbcTemplate;
    @Value("${flash-sale.order.limit.count}")
    @NonFinal
    int limitCount;

    @PostMapping("/order")
    public ResponseEntity<BaseResponse<Void>> placeOrder(@RequestBody @Valid UserPurchaseRequest request) {
        userPurchaseService.order(request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .success(true)
                .message("Order placed successfully")
                .build());
    }

    @GetMapping("/product")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getProduct() {

        String sql = """
                select id, name, price, stock
                from products
                where id = 1
                """;

        Map<String, Object> product = jdbcTemplate.queryForMap(sql);

        return ResponseEntity.ok(
                BaseResponse.<Map<String, Object>>builder()
                        .success(true)
                        .data(product)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<Integer>> getUserPurchase(
            @PathVariable String userId
    ) {

        String sql = """
                select quantity
                from user_purchases
                where user_id = ? and product_id = 1
                """;

        Integer quantity = jdbcTemplate.query(
                sql,
                rs -> rs.next() ? rs.getInt("quantity") : 0,
                userId
        );

        return ResponseEntity.ok(
                BaseResponse.<Integer>builder()
                        .success(true)
                        .data(quantity)
                        .build()
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<Map<String, Object>>>> getOrders() {

        String sql = """
                select user_id, product_id, quantity, created_at
                from user_purchases
                order by created_at desc
                """;

        List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql);

        return ResponseEntity.ok(
                BaseResponse.<List<Map<String, Object>>>builder()
                        .success(true)
                        .data(orders)
                        .build()
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<BaseResponse<Void>> reset() {

        jdbcTemplate.update("delete from user_purchases");

        jdbcTemplate.update("""
                    update products
                    set stock = 500
                    where id = 1
                """);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .success(true)
                        .message("Reset success")
                        .build()
        );
    }

    @GetMapping("/limit")
    public ResponseEntity<BaseResponse<Integer>> getLimit() {

        return ResponseEntity.ok(
                BaseResponse.<Integer>builder()
                        .success(true)
                        .data(limitCount)
                        .build()
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getStats() {

        Map<String, Object> stats = new HashMap<>();

        // stock còn lại
        Integer stock = jdbcTemplate.queryForObject(
                "select stock from products where id = 1",
                Integer.class
        );

        // tổng số lượng đã bán
        Integer totalSold = jdbcTemplate.queryForObject(
                "select coalesce(sum(quantity),0) from user_purchases where product_id = 1",
                Integer.class
        );

        // tổng số user đã mua
        Integer totalUsers = jdbcTemplate.queryForObject(
                "select count(*) from user_purchases where product_id = 1",
                Integer.class
        );

        stats.put("stockRemaining", stock);
        stats.put("totalSold", totalSold);
        stats.put("totalUsersPurchased", totalUsers);

        return ResponseEntity.ok(
                BaseResponse.<Map<String, Object>>builder()
                        .success(true)
                        .data(stats)
                        .build()
        );
    }
}
