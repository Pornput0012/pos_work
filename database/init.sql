
-- 0. Test
-- SELECT * FROM brand;
-- SELECT * FROM sale_item;
-- DROP TABLE brand;
-- DROP TABLE sale_item;

-- 1. Set Timezone
SET time_zone = '+07:00';

-- 2. Create Database
CREATE DATABASE IF NOT EXISTS ITBMS;

-- 3. Use Database
USE ITBMS;

-- drop database ITBMS;

-- 4. Create Brand Table
CREATE TABLE brand (
  id INT PRIMARY KEY auto_increment,
  name VARCHAR(30) NOT NULL,
  website_url VARCHAR(40),
  is_active BOOLEAN DEFAULT TRUE,
  country_of_origin VARCHAR(80),
  created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_brand_name_not_empty CHECK (TRIM(name) <> ''),
  CONSTRAINT chk_website_url_not_empty CHECK (website_url IS NULL OR TRIM(website_url) <> ''),
  CONSTRAINT chk_country_of_origin_not_empty CHECK (country_of_origin IS NULL OR TRIM(country_of_origin) <> '')
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. Create Sale Item Table
CREATE TABLE sale_item (
  id INT PRIMARY KEY auto_increment,
  model VARCHAR(60) NOT NULL,
  brand_id INT NOT NULL,
  description TEXT NOT NULL,
  price INT NOT NULL,
  ram_gb INT,
  screen_size_inch DECIMAL(4,2),
  storage_gb INT,
  color VARCHAR(50),
  quantity INT NOT NULL DEFAULT 1,
  created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_sale_item_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
  CONSTRAINT chk_model_not_empty CHECK (TRIM(model) <> ''),
  CONSTRAINT chk_description_not_empty CHECK (TRIM(description) <> ''),
  CONSTRAINT chk_color_not_empty CHECK (color IS NULL OR TRIM(color) <> '')
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. Insert BRAND Table
INSERT INTO brand (id, name, country_of_origin, website_url, is_active, created_on, updated_on)
VALUES
(1, 'Samsung', 'South Korea', 'https://www.samsung.com', 1, NOW(), NOW()),
(2,'Apple', 'United States', 'https://www.apple.com', 1, NOW(), NOW()),
(3, 'Xiaomi', 'China', 'https://www.mi.com', 1, NOW(), NOW()),
(4, 'Huawei', 'China', 'https://www.huawei.com', 1, NOW(), NOW()),
(5,'OnePlus', 'China', 'https://www.oneplus.com', 1, NOW(), NOW()),
(6,'Sony', 'Japan', 'https://www.sony.com', 1, NOW(), NOW()),
(7,'LG', 'South Korea', 'https://www.lg.com', 1, NOW(), NOW()),
(8,'Nokia', 'Finland', 'https://www.nokia.com', 0, NOW(), NOW()),
(9,'Motorola', 'United States', 'https://www.motorola.com', 0, NOW(), NOW()),
(10,'OPPO', 'China', 'https://www.oppo.com', 1, NOW(), NOW()),
(11,'Vivo', 'China', 'https://www.vivo.com', 1, NOW(), NOW()),
(12,'ASUS', 'Taiwan', 'https://www.asus.com', 1, NOW(), NOW()),
(13, 'Google', 'United States', 'https://store.google.com', 1, NOW(), NOW()),
(14, 'Realme', 'China', 'https://www.realme.com', 1, NOW(), NOW()),
(15, 'BlackBerry', 'Canada', 'https://www.blackberry.com', 1, NOW(), NOW()),
(16, 'HTC', 'Taiwan', 'https://www.htc.com', 1, NOW(), NOW()),
(17, 'ZTE', 'China', 'https://www.zte.com', 1, NOW(), NOW()),
(18, 'Lenovo', 'China', 'https://www.lenovo.com', 1, NOW(), NOW()),
(19, 'Honor', 'China', 'https://www.hihonor.com', 1, NOW(), NOW()),
(20, 'Nothing', 'United Kingdom', 'https://nothing.tech', 1, NOW(), NOW());

-- 7. Insert SALE ITEM Table
INSERT INTO sale_item (id, brand_id, model, description, quantity, price, screen_size_inch, ram_gb, storage_gb, color, created_on, updated_on)
VALUES
(1, 2, 'iPhone 14 Pro Max', 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900, 6.7, 6, 512, 'Space Black', '2020-09-15 10:30:00', '2024-01-10 14:25:00'),
(2, 2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700, 6.1, 6, 256, 'Midnight', '2020-09-16 11:15:00', '2024-01-05 09:40:00'),
(3, 2, 'iPhone 13 Pro', 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 3, 33000, 6.1, 6, 256, 'Sierra Blue', '2020-09-20 14:20:00', '2023-12-15 16:30:00'),
(4, 2, 'iPhone 13', 'Previous gen base model', 10, 23100, 6.1, 4, 128, 'Pink', '2020-09-25 09:45:00', '2023-11-20 11:15:00'),
(5, 2, 'iPhone 12 Pro Max', '2020 flagship model', 4, 29700, 6.7, 6, 256, 'Pacific Blue', '2020-10-05 13:10:00', '2023-10-05 10:20:00'),
(6, 2, 'iPhone 12', '2020 base model', 6, 19800, 6.1, 4, 128, 'Purple', '2020-10-10 10:30:00', '2023-09-15 14:45:00'),
(7, 2, 'iPhone SE 2022', 'Budget-friendly model', 15, 14190, 4.7, 4, 64, 'Starlight', '2020-10-15 08:20:00', '2023-12-10 15:10:00'),
(8, 2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight เครื่องศูนย์ไทย โมเดล TH แบต 100% มีกล่องครบ ประกันศูนย์ถึง พ.ย. 68 ส่งฟรี', 7, 29700, 6.7, 6, 256, 'Blue', '2020-10-20 12:40:00', '2024-01-08 13:50:00'),
(9, 2, 'iPhone 13 mini', 'Compact previous gen', 5, 19800, 5.4, 4, 128, 'Green', '2020-10-25 15:25:00', '2023-11-25 09:30:00'),
(10, 2, 'iPhone 12 mini', 'Compact 2020 model', 4, 16500, 5.4, 4, 64, 'Red', '2020-10-30 11:50:00', '2023-10-20 14:15:00'),
(16, 1, 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra 512GB สีดำปีศาจ สภาพนางฟ้า 99% ไร้รอย แถมเคสแท้ แบตอึดสุดๆ รองรับปากกา S-Pen อุปกรณ์ครบกล่อง ประกันศูนย์เหลือ 6 เดือน ส่งฟรี', 6, 39600, 6.8, NULL, 512, NULL, '2021-01-10 10:15:00', '2024-01-15 16:20:00'),
(17, 1, 'Galaxy S23+', 'Premium flagship model', 8, 33000, 6.6, 8, 256, 'Cream', '2021-01-15 09:30:00', '2024-01-12 11:45:00'),
(18, 1, 'Galaxy Z Fold4', 'สมาร์ทโฟนพับได้สุดล้ำ จอใหญ่เท่าแท็บเล็ต ทำงานได้หลากหลาย', 3, 59400, 7.6, 12, 256, 'Phantom Green', '2021-01-20 14:40:00', '2023-12-18 10:30:00'),
(19, 1, 'Galaxy Z Flip4', 'Compact foldable', 5, 33000, 6.7, 8, 128, 'Bora Purple', '2021-01-25 11:25:00', '2023-12-10 14:15:00'),
(20, 1, 'Galaxy A53 5G', 'มือถือ 5G สเปคดี กล้องเทพ แบตอึด คุ้มค่าน่าใช้', 12, 14850, 6.5, 6, 128, 'Awesome Blue', '2021-02-01 10:10:00', '2023-11-20 09:40:00'),
(21, 1, 'Galaxy A33 5G', 'Budget 5G phone', 15, 11550, 6.4, 6, 128, 'Awesome White', '2021-02-05 13:20:00', '2023-11-15 16:25:00'),
(22, 1, 'Galaxy S22', 'เรือธงตัวท็อปจาก Samsung พร้อม S Pen ในตัว กล้อง 200MP ซูมไกลสุด 100x', 7, 26400, 6.1, 8, 128, 'Pink Gold', '2021-02-10 09:45:00', '2023-10-25 14:10:00'),
(23, 1, 'Galaxy M53', 'Mid-range performance', 9, 14850, 6.7, 6, 128, 'Green', '2021-02-15 11:30:00', '2023-11-30 10:20:00'),
(24, 1, 'Galaxy A73 5G', 'Premium mid-range', 6, 16500, 6.7, 8, 256, 'Gray', '2021-02-20 14:15:00', '2023-12-05 13:45:00'),
(25, 1, 'Galaxy S21 FE', 'Fan Edition model', 8, 19800, 6.4, 6, 128, 'Olive', '2021-02-25 10:50:00', '2023-10-15 15:30:00'),
(31, 3, '13 Pro', 'เรือธงสเปคแรงจาก Xiaomi กล้องไลก้า ชาร์จไว 120W', 8, 33000, 6.73, 12, 256, 'Black', '2021-03-05 11:20:00', '2024-01-08 14:40:00'),
(32, 3, '13T Pro', 'Xiaomi 13T Pro 12/512GB สี Meadow Green ชิป Dimensity 9200+ เร็วแรง กล้อง Leica ถ่ายรูปสวยขั้นเทพ มีที่ชาร์จ 120W ครบกล่อง จัดส่งฟรีทั่วประเทศ', 6, 23100, NULL, 12, NULL, 'Alpine Blue', '2021-03-10 13:45:00', '2024-01-12 10:15:00'),
(33, 3, 'POCO F5', 'มือถือสเปคเทพ เน้นเล่นเกม จอ 120Hz ราคาคุ้มค่า', 10, 13200, 6.67, 8, 256, 'Carbon Black', '2021-03-15 10:30:00', '2023-12-20 16:25:00'),
(34, 3, 'Redmi Note 12 Pro', 'กล้องคมชัด 108MP แบตอึด ชาร์จเร็ว ราคาโดนใจ', 15, 9900, 6.67, 8, 128, 'Sky Blue', '2021-03-20 09:15:00', '2023-11-25 11:40:00'),
(35, 3, '12T Pro', 'Previous flagship', 5, 21450, 6.67, 8, 256, 'Cosmic Black', '2021-03-25 14:20:00', '2023-10-30 14:50:00'),
(36, 3, 'POCO X5 Pro', 'Mid-range performer', 12, 9900, 6.67, 8, 128, 'Yellow', '2021-04-01 11:10:00', '2023-12-15 09:30:00'),
(37, 3, 'Redmi 12C', 'Budget friendly', 20, 5940, 6.71, 4, 64, 'Ocean Blue', '2021-04-05 08:45:00', '2023-11-10 15:20:00'),
(38, 3, '12 Lite', 'Slim mid-range', 8, 13200, 6.55, 8, 128, 'Lite Pink', '2021-04-10 10:25:00', '2023-10-20 10:45:00'),
(39, 3, 'POCO M5', 'Budget gaming', 14, 7590, 6.58, 6, 128, 'Power Black', '2021-04-15 13:30:00', '2023-11-15 14:15:00'),
(40, 3, 'Redmi Note 11', 'Previous gen mid-range', 10, 8250, 6.43, 6, 128, 'Star Blue', '2021-04-20 09:50:00', '2023-10-10 11:30:00'),
(46, 4, 'P60 Pro', 'กล้องเรือธงระดับเทพ เซ็นเซอร์ใหญ่พิเศษ ถ่ายภาพกลางคืนสวยเยี่ยม', 5, 36300, 6.67, 12, 256, 'Rococo Pearl', '2021-05-05 14:15:00', '2024-01-05 15:45:00'),
(47, 4, 'Mate 50 Pro', 'เรือธงตระกูล Mate จอ OLED คมชัด ดีไซน์พรีเมียม', 4, 42900, 6.74, 8, 256, 'Silver Black', '2021-05-10 11:40:00', '2023-12-20 10:30:00'),
(48, 4, 'nova 11 Pro', 'สมาร์ทโฟนดีไซน์สวย กล้องหน้าคู่ เน้นเซลฟี่ ชาร์จไว', 8, 19800, 6.78, 8, 256, 'Green', '2021-05-15 10:20:00', '2023-12-15 14:25:00'),
(49, 4, 'P50 Pro', 'Previous flagship', 6, 29700, 6.6, 8, 256, 'Cocoa Gold', '2021-05-20 09:30:00', '2023-11-25 16:10:00'),
(50, 4, 'nova 10', 'Stylish mid-range', 10, 16500, 6.67, 8, 128, 'Starry Silver', '2021-05-25 13:45:00', '2023-11-30 11:20:00'),
(51, 4, 'Mate X3', 'Premium foldable', 3, 66000, 7.85, 12, 512, 'Feather Gold', '2021-06-01 15:10:00', '2024-01-10 13:30:00'),
(52, 4, 'nova 9', 'Previous mid-range', 12, 13200, 6.57, 8, 128, 'Starry Blue', '2021-06-05 10:50:00', '2023-10-25 09:45:00'),
(53, 4, 'P50 Pocket', 'Foldable fashion', 4, 46200, 6.9, 8, 256, 'Premium Gold', '2021-06-10 14:30:00', '2023-12-05 15:15:00'),
(54, 4, 'nova Y70', 'Budget friendly', 15, 9900, 6.75, 4, 128, 'Crystal Blue', '2021-06-15 08:40:00', '2023-11-20 10:50:00'),
(55, 4, 'Mate 40 Pro', 'Classic flagship', 5, 26400, 6.76, 8, 256, 'Mystic Silver', '2021-06-20 11:15:00', '2023-10-15 14:40:00'),
(61, 12, 'ROG Phone 7', 'สมาร์ทโฟนเกมมิ่งสเปคโหด จอ 165Hz เสียงสเตอริโอคู่ แบตอึด', 4, 33000, 6.78, 16, 512, 'Phantom Black', '2021-07-05 10:45:00', '2024-01-08 16:30:00'),
(62, 12, 'ROG Phone 6D', 'เกมมิ่งโฟนพลังแรง CPU Dimensity ระบายความร้อนเยี่ยม', 5, 29700, 6.78, 16, 256, 'Space Gray', '2021-07-10 13:20:00', '2023-12-18 11:45:00'),
(63, 12, 'Zenfone 9', 'มือถือกะทัดรัด สเปคแรง กล้องกันสั่น ใช้ง่ายมือเดียว', 8, 23100, 5.9, 8, 128, 'Midnight Black', '2021-07-15 09:30:00', '2023-11-28 14:20:00'),
(64, 12, 'ROG Phone 6', 'Previous gaming flagship', 6, 29700, 6.78, 12, 256, 'Storm White', '2021-07-20 11:15:00', '2023-12-10 10:30:00'),
(65, 12, 'Zenfone 8', 'Previous compact flagship', 7, 19800, 5.9, 8, 128, 'Obsidian Black', '2021-07-25 14:40:00', '2023-10-25 15:10:00'),
(66, 12, 'ROG Phone 5s', 'Gaming performance', 5, 26400, 6.78, 12, 256, 'Phantom Black', '2021-08-01 10:20:00', '2023-11-15 13:45:00'),
(67, 12, 'Zenfone 8 Flip', 'Flip camera flagship', 4, 26400, 6.67, 8, 256, 'Galactic Black', '2021-08-05 13:30:00', '2023-10-30 09:50:00'),
(68, 12, 'ROG Phone 5', 'Classic gaming phone', 6, 23100, 6.78, 12, 256, 'Storm White', '2021-08-10 09:45:00', '2023-10-20 14:15:00'),
(69, 12, 'Zenfone 7', 'Flip camera classic', 5, 19800, 6.67, 8, 128, 'Aurora Black', '2021-08-15 11:50:00', '2023-09-25 10:40:00'),
(70, 12, 'ROG Phone 3', 'Legacy gaming phone', 3, 16500, 6.59, 12, 256, 'Black Glare', '2021-08-20 14:25:00', '2023-09-15 16:20:00'),
(76, 10, 'Find X6 Pro', 'กล้องเทพระดับมืออาชีพ ชิป Snapdragon 8 Gen 2 ชาร์จไว 100W', 5, 33000, 6.82, 12, 256, 'Cosmos Black', '2021-09-05 10:30:00', '2024-01-05 14:50:00'),
(77, 10, 'Reno9 Pro+', 'OPPO Reno9 Pro+ 5G 256GB สี Glossy Purple สวยสะดุดตา ใช้งานลื่นสุดๆ แบต 4700 mAh รองรับชาร์จไว ครบกล่อง + ใบเสร็จศูนย์ ส่งฟรี Flash Express', 8, 23100, 6.7, 12, 256, 'Eternal Gold', '2021-09-10 13:15:00', '2023-12-20 11:30:00'),
(78, 10, 'Find N2 Flip', 'สมาร์ทโฟนพับได้สุดหรู จอนอกใหญ่พิเศษ กล้องคู่คมชัด', 4, 33000, 6.8, 8, 256, 'Astral Black', '2021-09-15 14:40:00', '2024-01-08 15:25:00'),
(79, 10, 'Reno8 Pro', 'ดีไซน์บางเบา กล้องคมชัด ชาร์จเร็วสุด ระบบเสียงดี', 10, 19800, 6.7, 8, 256, 'Glazed Green', '2021-09-20 09:20:00', '2023-11-25 10:15:00'),
(80, 10, 'Find X5 Pro', 'Previous flagship', 6, 29700, 6.7, 12, 256, 'Ceramic White', '2021-09-25 11:45:00', '2023-12-15 14:30:00'),
(81, 10, 'A78', 'Mid-range performer', 15, 9900, 6.56, 8, 128, 'Glowing Black', '2021-09-30 08:30:00', '2023-11-20 09:40:00'),
(82, 10, 'Reno7', 'Style focused mid-range', 12, 13200, 6.43, 8, 128, 'Startrails Blue', '2021-10-05 10:10:00', '2023-10-25 11:20:00'),
(83, 10, 'Find X5 Lite', 'Previous gen lite', 8, 14850, 6.43, 8, 128, 'Starry Black', '2021-10-10 13:25:00', '2023-11-15 15:10:00'),
(84, 10, 'A77', 'Budget friendly', 20, 8250, 6.56, 6, 128, 'Ocean Blue', '2021-10-15 09:50:00', '2023-10-30 10:45:00'),
(85, 10, 'Reno6 Pro', 'Classic premium', 7, 16500, 6.55, 12, 256, 'Arctic Blue', '2021-10-20 14:15:00', '2023-10-20 13:30:00');


