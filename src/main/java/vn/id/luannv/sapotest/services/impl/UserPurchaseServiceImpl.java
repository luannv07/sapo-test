package vn.id.luannv.sapotest.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.luannv.sapotest.dto.request.UserPurchaseRequest;
import vn.id.luannv.sapotest.exceptions.AppException;
import vn.id.luannv.sapotest.repository.ProductRepository;
import vn.id.luannv.sapotest.services.UserPurchaseService;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPurchaseServiceImpl implements UserPurchaseService {
    JdbcTemplate jdbcTemplate;
    ProductRepository productRepository;

    @NonFinal
    @Value("${flash-sale.order.limit.count}")
    int limitCount;

    @Override
    @Transactional
    public void order(UserPurchaseRequest request) {
        String userId = request.getUserId();
        Long productId = request.getProductId();
        Integer quantity = request.getQuantity();
        // check & return luôn nếu ko tìm thấy productId by Id
        if (!productRepository.existsById(productId))
            throw new AppException(
                    "Product with id " + productId + " not found",
                    "PRODUCT_NOT_FOUND"
            );
        // check & return luôn nếu quantity ko hợp lệ
        if (quantity > limitCount) {
            throw new AppException(
                    "Quantity " + quantity + " exceeds purchase limit " + limitCount,
                    "QUANTITY_EXCEEDS_LIMIT"
            );
        }
        /*
        - truy vấn atmoci dạng UPSERT
        + nếu đã tồn tại (user_id, product_id) trong bảng và
            tổng sản phẩm trong bảng S.quantity + tổng sản phẩm cần mua T.quantity <= limitCount
            => cho phép order
        + nếu chưa tồn tại (user_id, product_id) trong bảng thì sẽ insert mới
            với quantity <= limitCount (check ở trên)
        + còn lại sẽ throw lỗi liên quan đến Không cho phép đặt hàng do giới hạn.
         */
        String atomicSql = """
                MERGE INTO user_purchases AS T
                USING (VALUES (?, ?, ?)) AS S(user_id, product_id, quantity)
                    ON (T.user_id = S.user_id AND T.product_id = S.product_id)
                
                WHEN MATCHED AND T.quantity + S.quantity <= ? THEN
                    UPDATE SET T.quantity = T.quantity + S.quantity
                
                WHEN NOT MATCHED THEN
                    INSERT (user_id, product_id, quantity)
                    VALUES (S.user_id, S.product_id, S.quantity)
                """;

        int rowsAffected = jdbcTemplate.update(
                atomicSql,
                userId,
                productId,
                quantity,
                limitCount
        );

        if (rowsAffected == 0) {
            throw new AppException(
                    "User " + userId + " exceeded purchase limit",
                    "PURCHASE_LIMIT_EXCEEDED"
            );
        }
        /*
        Update sản phẩm sau khi đã đặt hàng thành công (rowsAffected != 0)
         */
        String updateStockWhenOrderSuccessSql = """
                update products p
                set p.stock = p.stock - ?
                where p.id = ? and p.stock >= ?
                """;
        int updateStockResult = jdbcTemplate.update(
                updateStockWhenOrderSuccessSql,
                quantity,
                productId,
                quantity
        );
        if (updateStockResult == 0) {
            throw new AppException(
                    "Product " + productId + " is out of stock",
                    "OUT_OF_STOCK"
            );
        }
        log.info(
                "Order success: userId={}, productId={}, quantity={}",
                userId,
                productId,
                quantity
        );
    }
}