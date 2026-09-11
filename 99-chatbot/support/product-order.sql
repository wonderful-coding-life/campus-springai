CREATE TABLE product_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    product_name VARCHAR(255),
    shipping_address VARCHAR(255),
    shipping_status VARCHAR(255),
    member_name VARCHAR(255),
    PRIMARY KEY (id)
);

INSERT INTO product_order (
    order_number,
    product_name,
    shipping_address,
    shipping_status,
    member_name
) VALUES (
    'H001',
    '맥북에어',
    '서울시 강남구 역삼동',
    '상품준비중',
    'seojun'
), (
    'H002',
    '아이폰',
    '서울시 영등포구 여의도동',
    '배송중',
    'seojun'
);