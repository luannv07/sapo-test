CREATE TABLE products
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price BIGINT NOT NULL,
    stock INT NOT NULL
);

INSERT INTO products (id, name, price, stock)
VALUES (1, 'Sản phẩm mẫu', 10, 500);
CREATE TABLE user_purchases
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_userpurchase_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_user_product UNIQUE (user_id, product_id)
);