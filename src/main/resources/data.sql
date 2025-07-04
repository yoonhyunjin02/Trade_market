INSERT INTO users (user_id, user_name, user_password, user_location, provider, provider_id)
VALUES
  ('user1', '홍길동', 'password123', '서울 강남구', NULL, NULL),
  ('user2', '김철수', 'qwerty456', '서울 마포구', NULL, NULL),
  ('user3', '이영희', 'hello789', '서울 송파구', 'kakao', 'kakao_12345');


INSERT INTO products (
  seller_id, product_title, product_description, product_price, product_location,
  product_view_count, product_chat_count, product_sold_or_not, product_created_at
)
VALUES
  (1, '아이폰 12 팝니다', '생활기스 있음. 정상 작동.', 500000, '서울 강남구', 24, 3, false, '2024-06-01'),
  (2, '책상 판매해요', '조립식 120cm 책상입니다.', 30000, '서울 마포구', 12, 1, true, '2024-06-15'),
  (1, '에어팟 프로 2세대', '미개봉 새제품입니다.', 250000, '서울 강남구', 55, 7, false, '2024-07-01');


INSERT INTO image (product_id, product_image)
VALUES
  (1, 'https://example.com/images/iphone.jpg'),
  (2, 'https://example.com/images/desk.jpg'),
  (3, 'https://example.com/images/airpods.jpg'),
  (3, 'https://example.com/images/airpods2.jpg');  -- 상품 하나에 이미지 2장도 가능