-- =========================
-- guests
-- =========================
INSERT INTO guest (id, name, kana_name, gender, age, region, email, phone, deleted, user_id)
VALUES
  ('11111111-1111-1111-1111-111111111111', '佐藤花子', 'サトウハナコ', '女性', 28, '東京', 'hanako@example.com', '08098765432', 0, 'testuser01'),
  ('22222222-2222-2222-2222-222222222222', '田中太郎', 'タナカタロウ', '男性', 35, '大阪', 'taro@example.com', '08011112222', 1, 'testuser01'),
  ('22222222-3333-3333-3333-222222222222', '山田毅', 'ヤマダタケシ', '男性', 40, '宮城', 'takeshi@example.com', '08033332222', 0, 'testuser01'),
  ('33333333-3333-3333-3333-333333333333', '中村葵',   'ナカムラアオイ', '女性', 26, '愛知', 'aoi@example.com',   '08044445555', 0, 'testuser01'),
  ('44444444-4444-4444-4444-444444444444', '鈴木健',   'スズキケン',     '男性', 31, '北海道', 'ken@example.com',   '08066667777', 0, 'testuser01'),
  ('55555555-5555-5555-5555-555555555555', '高橋桜',   'タカハシサクラ', '女性', 29, '福岡', 'sakura@example.com', '08088889999', 0, 'testuser01'),
  ('66666666-6666-6666-6666-666666666666', '小林翔',   'コバヤシショウ', '男性', 37, '京都', 'sho@example.com',    '08000001111', 0, 'testuser01'),
  ('77777777-7777-7777-7777-777777777777', '渡辺美咲', 'ワタナベミサキ', '女性', 33, '神奈川', 'misaki@example.com','08022223333', 0, 'testuser01'),
  ('88888888-8888-8888-8888-888888888888', '松本大輝', 'マツモトダイキ', '男性', 42, '広島', 'daiki@example.com', '08055556666', 0, 'testuser01'),
  ('99999999-9999-9999-9999-999999999999', '石井舞',   'イシイマイ',     '女性', 24, '千葉', 'mai@example.com',   '08012121212', 0, 'testuser01'),
  ('aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '加藤亮',   'カトウリョウ',   '男性', 50, '新潟', 'ryo@example.com',   '08034343434', 0, 'testuser01'),
  ('bbbb2222-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '佐々木優子','ササキユウコ',  '女性', 27, '群馬', 'yuko@example.com',  '08056565656', 0, 'testuser01'),
  ('cccc3333-cccc-cccc-cccc-cccccccccccc', '森田悠斗', 'モリタユウト',   '男性', 30, '福島', 'yuto@example.com',  '08078787878', 0, 'testuser01');

-- =========================
-- bookings
-- =========================
INSERT INTO booking (id, name, description, price, is_available, user_id)
VALUES
  ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '朝食付きプラン', '和洋朝食が選べるプラン', 10000.00, 0, 'testuser01'),
  ('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', '素泊まりプラン', '食事なし・シンプルステイ', 7000.00, 1, 'testuser01');

-- =========================
-- reservations（booking_id は上の UUID）
-- =========================
INSERT INTO reservation (
  id, guest_id, booking_id, check_in_date, check_out_date,
  stay_days, total_price, status, memo, created_at, user_id
) VALUES
  ('11111111-aaaa-4bbb-8ccc-111111111111',
   '11111111-1111-1111-1111-111111111111',
   'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
   '2025-07-23', '2025-07-27', 4, 20000.00,
   'CHECKED_IN', '観光で利用', '2025-07-01 10:00:00', 'testuser01'),

  ('22222222-bbbb-4ccc-8ddd-222222222222',
   '22222222-2222-2222-2222-222222222222',
   'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
   '2025-07-24', '2025-07-25', 1, 7000.00,
   'NOT_CHECKED_IN', '出張で利用', '2025-07-02 15:30:00', 'testuser01'),

  ('33333333-bbbb-4ccc-8ddd-333333333333',
   '22222222-3333-3333-3333-222222222222',
   'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
   '2025-07-20', '2025-07-30', 10, 7000.00,
   'NOT_CHECKED_IN', '出張で利用', '2025-07-02 15:30:00', 'testuser01');

-- 11) 本日チェックイン予定（夕方IN）
INSERT INTO reservation VALUES
('aaaa0001-aaaa-4aaa-8aaa-aaaa00000001',
 '88888888-8888-8888-8888-888888888888',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-22','2025-09-25',3,30000.00,
 'NOT_CHECKED_IN','仕事仲間と利用','2025-09-21 18:00:00','testuser01');

-- 12) 本日チェックイン（深夜IN予定）
INSERT INTO reservation VALUES
('aaaa0002-aaaa-4aaa-8aaa-aaaa00000002',
 '99999999-9999-9999-9999-999999999999',
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-09-22','2025-09-23',1,7000.00,
 'NOT_CHECKED_IN','ライブ帰りで深夜IN','2025-09-21 23:10:00','testuser01');

-- 13)
INSERT INTO reservation VALUES
('aaaa0003-aaaa-4aaa-8aaa-aaaa00000003',
 'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-19','2025-09-22',3,30000.00,
 'CHECKED_IN','連泊の最終日','2025-09-17 12:00:00','testuser01');

-- 14)
INSERT INTO reservation VALUES
('aaaa0004-aaaa-4aaa-8aaa-aaaa00000004',
 'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-09-21','2025-09-24',3,21000.00,
 'CHECKED_IN','観光旅行2日目','2025-09-19 09:30:00','testuser01');

-- 15)
INSERT INTO reservation VALUES
('aaaa0005-aaaa-4aaa-8aaa-aaaa00000005',
 'cccc3333-cccc-cccc-cccc-cccccccccccc',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-20','2025-09-26',6,60000.00,
 'CHECKED_IN','大型連休滞在','2025-09-15 10:20:00','testuser01');

-- 16)
INSERT INTO reservation VALUES
('aaaa0006-aaaa-4aaa-8aaa-aaaa00000006',
 '33333333-3333-3333-3333-333333333333',
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-09-21','2025-09-22',1,7000.00,
 'CHECKED_OUT','早朝退室','2025-09-20 08:00:00','testuser01');

-- 17)
INSERT INTO reservation VALUES
('aaaa0007-aaaa-4aaa-8aaa-aaaa00000007',
 '44444444-4444-4444-4444-444444444444',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-22','2025-09-23',1,10000.00,
 'CANCELLED','体調不良で前日キャンセル','2025-09-21 19:00:00','testuser01');

-- 18)
INSERT INTO reservation VALUES
('aaaa0008-aaaa-4aaa-8aaa-aaaa00000008',
 '55555555-5555-5555-5555-555555555555',
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-09-21','2025-09-22',1,7000.00,
 'NOT_CHECKED_IN','未到着（ノーショー）','2025-09-20 15:00:00','testuser01');

-- 19)
INSERT INTO reservation VALUES
('aaaa0009-aaaa-4aaa-8aaa-aaaa00000009',
 '66666666-6666-6666-6666-666666666666',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-25','2025-09-27',2,20000.00,
 'NOT_CHECKED_IN','来週の出張予約','2025-09-10 09:00:00','testuser01');

-- 20)
INSERT INTO reservation VALUES
('aaaa0010-aaaa-4aaa-8aaa-aaaa00000010',
 '77777777-7777-7777-7777-777777777777',
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-10-05','2025-10-08',3,21000.00,
 'NOT_CHECKED_IN','秋旅行','2025-09-15 17:00:00','testuser01');

-- 21)
INSERT INTO reservation VALUES
('aaaa0011-aaaa-4aaa-8aaa-aaaa00000011',
 '11111111-1111-1111-1111-111111111111',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-08-15','2025-08-17',2,20000.00,
 'CHECKED_OUT','夏休み利用','2025-08-01 12:00:00','testuser01');

-- 22)
INSERT INTO reservation VALUES
('aaaa0012-aaaa-4aaa-8aaa-aaaa00000012',
 '88888888-8888-8888-8888-888888888888',
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-09-22','2025-09-23',1,10000.00,
 'NOT_CHECKED_IN','団体旅行（代表者）','2025-09-10 18:00:00','testuser01');

-- =========================
-- users
-- =========================
INSERT INTO users (id, password) VALUES
  ('testuser01','testpass123'),
  ('TEST','TEST'),
  ('status-test','status-test'),
  ('not-exist','not-exist');