-- 중복 삽입 방지를 위한 조건부 삽입

-- 카테고리 삽입 (중복 방지)
INSERT INTO category (category_id, category_name, product_count)
SELECT 1, '전자기기', 0
WHERE NOT EXISTS (SELECT 1 FROM category WHERE category_id = 1);

-- 제품 삽입 (기존 데이터 삭제 후 삽입 - 선택사항)
-- 기존 테스트 데이터가 있다면 삭제
DELETE FROM products WHERE product_title LIKE '테스트 상품%';

-- 제품 8개 삽입
INSERT INTO products (
  product_title,
  product_description,
  product_price,
  product_location,
  product_created_at,
  category_id,
  seller_id
) VALUES
('테스트 상품 1', '테스트 설명 1입니다.', 1000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 2', '테스트 설명 2입니다.', 2000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 3', '테스트 설명 3입니다.', 3000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 4', '테스트 설명 4입니다.', 4000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 5', '테스트 설명 5입니다.', 5000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 6', '테스트 설명 6입니다.', 6000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 7', '테스트 설명 7입니다.', 7000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 8', '테스트 설명 8입니다.', 8000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 9', '테스트 설명 1입니다.', 1000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 10', '테스트 설명 2입니다.', 2000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 11', '테스트 설명 3입니다.', 3000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 12', '테스트 설명 4입니다.', 4000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 13', '테스트 설명 5입니다.', 5000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 14', '테스트 설명 6입니다.', 6000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 15', '테스트 설명 7입니다.', 7000, '서울 강남구', NOW(), 1, 1),
('테스트 상품 16', '테스트 설명 8입니다.', 8000, '서울 강남구', NOW(), 1, 1);

-- chatroom 테이블 생성
CREATE TABLE chatroom (
                          chatroom_id SERIAL PRIMARY KEY,
                          product_id INTEGER NOT NULL,
                          buyer_id INTEGER NOT NULL
);

-- chat 테이블 생성
CREATE TABLE chat (
                      chat_id SERIAL PRIMARY KEY,
                      chatroom_id INTEGER NOT NULL,
                      user_id VARCHAR(50),
                      assistant_id VARCHAR(50),
                      content TEXT NOT NULL,
                      is_read BOOLEAN NOT NULL,
                      created_at TIMESTAMP NOT NULL
);


-- chatroom 외래 키
ALTER TABLE chatroom
    ADD CONSTRAINT fk_chatroom_product
        FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE;

ALTER TABLE chatroom
    ADD CONSTRAINT fk_chatroom_buyer
        FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE;

-- chat 외래 키
ALTER TABLE chat
    ADD CONSTRAINT fk_chatroom_in_chat
        FOREIGN KEY (chatroom_id) REFERENCES chatroom(chatroom_id) ON DELETE CASCADE;