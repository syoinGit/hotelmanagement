-- guests テーブル
CREATE TABLE guest (
  id CHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  kana_name VARCHAR(100) NOT NULL,
  gender VARCHAR(10) NOT NULL,
  age INT NOT NULL,
  region VARCHAR(100),
  email VARCHAR(100),
  phone VARCHAR(20),
  deleted BOOLEAN DEFAULT FALSE,
  user_id CHAR(36) NOT NULL
);

-- bookings テーブル
CREATE TABLE booking (
  id CHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  is_available BOOLEAN DEFAULT TRUE,
  user_id CHAR(36) NOT NULL
);

-- reservations テーブル
CREATE TABLE reservation (
  id CHAR(36) NOT NULL PRIMARY KEY,
  guest_id CHAR(36) NOT NULL,
  booking_id CHAR(36) NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE,
  stay_days INT NOT NULL,
  total_price DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  memo TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_id CHAR(36) NOT NULL,

  -- 外部キー制約（任意）
  CONSTRAINT fk_guest FOREIGN KEY (guest_id) REFERENCES guest(id),
  CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) PRIMARY KEY,
  password VARCHAR(255) NOT NULL
);