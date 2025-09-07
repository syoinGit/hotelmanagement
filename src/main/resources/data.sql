-- guest（宿泊者）テーブル
INSERT INTO guest (id, name, kana_name, gender, age, region, email, phone, deleted, user_id)
VALUES
  ('11111111-1111-1111-1111-111111111111', '佐藤花子', 'サトウハナコ', '女性', 28, '東京', 'hanako@example.com', '08098765432', 0, 'testuser01'),
  ('22222222-2222-2222-2222-222222222222', '田中太郎', 'タナカタロウ', '男性', 35, '大阪', 'taro@example.com', '08011112222', 1 , 'testuser01'),
  ('22222222-3333-3333-3333-222222222222', '山田毅', 'ヤマダタケシ', '男性', 40, '宮城', 'takeshi@example.com', '08033332222', 0 , 'testuser01');


INSERT INTO booking (id, name, description, price, is_available, user_id)
VALUES
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '朝食付きプラン', '和洋朝食が選べるプラン', 10000.00, 0, 'testuser01'),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '素泊まりプラン', '食事なし・シンプルステイ', 7000.00, 1, 'testuser01');

INSERT INTO reservation (
  id, guest_id, booking_id, check_in_date, check_out_date,
  stay_days, total_price, status, memo, created_at, user_id)
  VALUES
  ('rsv00001-aaaa-bbbb-cccc-000000000001', '11111111-1111-1111-1111-111111111111',
   'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-07-23', '2025-07-27', 4, 20000.00,
   'CHECKED_IN', '観光で利用', '2025-07-01 10:00:00', 'testuser01'),

  ('rsv00002-bbbb-cccc-dddd-000000000002', '22222222-2222-2222-2222-222222222222',
   'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-07-24', '2025-07-25', 1, 7000.00,
   'NOT_CHECKED_IN', '出張で利用', '2025-07-02 15:30:00', 'testuser01'),

    ('rsv00002-bbbb-cccc-dddd-000000000003', '22222222-3333-3333-3333-222222222222',
      'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-07-20', '2025-07-30', 10, 7000.00,
      'NOT_CHECKED_IN', '出張で利用', '2025-07-02 15:30:00', 'testuser01');

   INSERT INTO users (id,password) VALUES
   ('testuser01','testpass123'),
   ('TEST','TEST'),
   ('status-test','status-test'),
   ('not-exist','not-exist');
