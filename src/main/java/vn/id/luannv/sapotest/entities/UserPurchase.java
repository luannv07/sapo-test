package vn.id.luannv.sapotest.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_purchases",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String userId;
    Long productId;
    Integer quantity;
    Instant createdAt;
}
