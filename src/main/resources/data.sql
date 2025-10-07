-- =========================
-- guests（そのまま）
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
-- bookings（そのまま）
-- =========================
INSERT INTO booking (id, name, description, price, is_available, user_id)
VALUES
  ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '朝食付きプラン', '和洋朝食が選べるプラン', 10000.00, 0, 'testuser01'),
  ('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', '素泊まりプラン', '食事なし・シンプルステイ', 7000.00, 1, 'testuser01');

-- =========================
-- reservations（2025-10-06 用）
-- =========================
DELETE FROM reservation;

-- ▼ チェックイン中（5件）: 10/6 を滞在日に含む
INSERT INTO reservation VALUES
('res3001-aaaa-4aaa-8aaa-aaaa00000001',
 'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaaaaaa',          -- 加藤亮
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',          -- 朝食付き
 '2025-10-04','2025-10-07',3,30000.00,
 'CHECKED_IN','出張で滞在中（延泊）','2025-10-04 12:10:00','testuser01');

INSERT INTO reservation VALUES
('res3002-aaaa-4aaa-8aaa-aaaa00000002',
 'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbbbbbb',          -- 佐々木優子
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-10-05','2025-10-07',2,20000.00,
 'CHECKED_IN','観光で滞在中','2025-10-05 09:00:00','testuser01');

INSERT INTO reservation VALUES
('res3003-aaaa-4aaa-8aaa-aaaa00000003',
 'cccc3333-cccc-cccc-cccc-cccccccccccc',          -- 森田悠斗
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',          -- 素泊まり
 '2025-10-03','2025-10-07',4,28000.00,
 'CHECKED_IN','ワーケーション中','2025-10-03 18:30:00','testuser01');

INSERT INTO reservation VALUES
('res3004-aaaa-4aaa-8aaa-aaaa00000004',
 '55555555-5555-5555-5555-555555555555',          -- 高橋桜
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-10-06','2025-10-08',2,20000.00,
 'CHECKED_IN','本日午前にチェックイン','2025-10-06 10:05:00','testuser01');

INSERT INTO reservation VALUES
('res3005-aaaa-4aaa-8aaa-aaaa00000005',
 '66666666-6666-6666-6666-666666666666',          -- 小林翔
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-10-04','2025-10-08',4,40000.00,
 'CHECKED_IN','長期滞在中','2025-10-04 11:20:00','testuser01');

-- ▼ 本日チェックアウト予定（2件）: check_out_date = 2025-10-06
INSERT INTO reservation VALUES
('res3006-aaaa-4aaa-8aaa-aaaa00000006',
 '33333333-3333-3333-3333-333333333333',          -- 中村葵
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-10-04','2025-10-06',2,14000.00,
 'CHECKED_IN','本日チェックアウト予定','2025-10-04 14:00:00','testuser01');

INSERT INTO reservation VALUES
('res3007-aaaa-4aaa-8aaa-aaaa00000007',
 '44444444-4444-4444-4444-444444444444',          -- 鈴木健
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-10-05','2025-10-06',1,7000.00,
 'CHECKED_IN','本日チェックアウト予定（1泊）','2025-10-05 21:15:00','testuser01');

-- ▼ 本日チェックイン予定（未到着 3件）: check_in_date = 2025-10-06, status = NOT_CHECKED_IN
INSERT INTO reservation VALUES
('res3008-aaaa-4aaa-8aaa-aaaa00000008',
 '88888888-8888-8888-8888-888888888888',          -- 渡辺美咲
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-10-06','2025-10-08',2,20000.00,
 'NOT_CHECKED_IN','夕方到着予定','2025-10-05 18:00:00','testuser01');

INSERT INTO reservation VALUES
('res3009-aaaa-4aaa-8aaa-aaaa00000009',
 '99999999-9999-9999-9999-999999999999',          -- 石井舞
 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
 '2025-10-06','2025-10-07',1,7000.00,
 'NOT_CHECKED_IN','ライブ後に深夜IN予定','2025-10-05 23:10:00','testuser01');

INSERT INTO reservation VALUES
('res3010-aaaa-4aaa-8aaa-aaaa00000010',
 '11111111-1111-1111-1111-111111111111',          -- 佐藤花子
 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
 '2025-10-06','2025-10-09',3,30000.00,
 'NOT_CHECKED_IN','出張で来館予定','2025-10-05 09:45:00','testuser01');

-- =========================
-- users（そのまま）
-- =========================
INSERT INTO users (id, password) VALUES
  ('testuser01','testpass123'),
  ('TEST','TEST'),
  ('status-test','status-test'),
  ('not-exist','not-exist');