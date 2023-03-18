DROP TABLE IF EXISTS ACCOUNT_LOGIN CASCADE;
DROP TABLE IF EXISTS ACCOUNT_OTP CASCADE;
DROP TABLE IF EXISTS YARD_PICTURE CASCADE;
DROP TABLE IF EXISTS VOTES CASCADE;
DROP TABLE IF EXISTS BOOKING_HISTORY CASCADE;
DROP TABLE IF EXISTS BOOKING CASCADE;
DROP TABLE IF EXISTS SLOTS CASCADE;
DROP TABLE IF EXISTS SUB_YARDS CASCADE;
DROP TABLE IF EXISTS TYPE_YARDS CASCADE;
DROP TABLE IF EXISTS YARDS CASCADE;
DROP TABLE IF EXISTS DISTRICTS CASCADE;
DROP TABLE IF EXISTS PROVINCES CASCADE;
DROP TABLE IF EXISTS VOUCHERS CASCADE;
DROP TABLE IF EXISTS ACCOUNTS CASCADE;
DROP TABLE IF EXISTS ROLES CASCADE;
DROP TABLE IF EXISTS YARD_REPORT CASCADE;

CREATE TABLE ROLES
(
    ID        SERIAL PRIMARY KEY,
    ROLE_NAME VARCHAR(50)
);
ALTER SEQUENCE roles_id_seq RESTART 1000;

CREATE TABLE PROVINCES
(
    ID            INTEGER NOT NULL PRIMARY KEY,
    PROVINCE_NAME VARCHAR(50)
);

CREATE TABLE DISTRICTS
(
    ID            SERIAL PRIMARY KEY,
    PROVINCE_ID   INTEGER REFERENCES PROVINCES ON DELETE CASCADE,
    DISTRICT_NAME VARCHAR(50)
);
ALTER SEQUENCE districts_id_seq RESTART WITH 1000;

CREATE TABLE TYPE_YARDS
(
    ID        SERIAL PRIMARY KEY,
    TYPE_NAME VARCHAR(10) DEFAULT '3 VS 3'::CHARACTER VARYING
);
ALTER SEQUENCE type_yards_id_seq RESTART WITH 1000;

CREATE TABLE ACCOUNTS
(
    ID           VARCHAR(50) NOT NULL PRIMARY KEY,
    ROLE_ID      INTEGER REFERENCES ROLES ON DELETE CASCADE,
    EMAIL        VARCHAR(50) UNIQUE,
    PHONE        VARCHAR(10) UNIQUE,
    FULL_NAME    VARCHAR(100),
    PASSWORD     VARCHAR(200),
    AVATAR_URL   VARCHAR(200),
    IS_CONFIRMED BOOLEAN DEFAULT FALSE,
    IS_ACTIVE    BOOLEAN DEFAULT TRUE,
    CREATE_AT    TIMESTAMP
);

CREATE TABLE ACCOUNT_LOGIN
(
    ID           SERIAL PRIMARY KEY,
    ACCOUNT_ID   VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    ACCESS_TOKEN TEXT,
    IS_LOGOUT    BOOLEAN
);
ALTER SEQUENCE account_login_id_seq RESTART WITH 1000;

CREATE TABLE ACCOUNT_OTP
(
    ID         SERIAL PRIMARY KEY,
    ACCOUNT_ID VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    OTP_CODE   VARCHAR(6),
    EXPIRE_AT  TIMESTAMP,
    CREATE_AT  TIMESTAMP,
    USED       BOOLEAN DEFAULT FALSE
);
ALTER SEQUENCE account_otp_id_seq RESTART WITH 1000;

CREATE TABLE YARDS
(
    ID                 VARCHAR(50) NOT NULL PRIMARY KEY,
    NAME               VARCHAR(45) NOT NULL,
    CREATE_AT          TIMESTAMP,
    ADDRESS            TEXT,
    DISTRICT_ID        INTEGER REFERENCES DISTRICTS ON DELETE CASCADE,
    IS_ACTIVE          BOOLEAN   DEFAULT TRUE,
    IS_DELETED         BOOLEAN   DEFAULT FALSE,
    OWNER_ID           VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    OPEN_AT            TIME,
    CLOSE_AT           TIME,
    SLOT_DURATION      INTEGER,
    SCORE              INTEGER   DEFAULT 0,
    NUMBER_OF_VOTE     INTEGER   DEFAULT 0,
    REFERENCE          SERIAL,
    CREATED_AT         TIMESTAMP DEFAULT NOW(),
    NUMBER_OF_BOOKINGS INTEGER   DEFAULT 0
);
ALTER SEQUENCE yards_reference_seq RESTART WITH 1000;

CREATE TABLE YARD_PICTURE
(
    ID     SERIAL PRIMARY KEY,
    REF_ID VARCHAR(50),
    IMAGE  VARCHAR(200)
);
ALTER SEQUENCE yard_picture_id_seq RESTART WITH 1000;

CREATE TABLE SUB_YARDS
(
    ID               VARCHAR(50) NOT NULL PRIMARY KEY,
    NAME             VARCHAR(100),
    PARENT_YARD      VARCHAR(50) REFERENCES YARDS ON DELETE CASCADE,
    TYPE_YARD        INTEGER REFERENCES TYPE_YARDS ON DELETE CASCADE,
    CREATED_AT       TIMESTAMP,
    IS_ACTIVE        BOOLEAN DEFAULT TRUE,
    REFERENCE        SERIAL,
    IS_PARENT_ACTIVE BOOLEAN DEFAULT TRUE,
    IS_DELETED       BOOLEAN DEFAULT FALSE,
    UPDATED_AT       TIMESTAMP
);
ALTER SEQUENCE sub_yards_reference_seq RESTART WITH 1000;

CREATE TABLE SLOTS
(
    ID               SERIAL PRIMARY KEY,
    IS_ACTIVE        BOOLEAN DEFAULT TRUE,
    REF_YARD         VARCHAR(50) REFERENCES SUB_YARDS ON DELETE CASCADE,
    PRICE            INTEGER,
    START_TIME       TIME,
    END_TIME         TIME,
    IS_PARENT_ACTIVE BOOLEAN DEFAULT TRUE
);
ALTER SEQUENCE slots_id_seq RESTART WITH 1000;

CREATE TABLE BOOKING
(
    ID             VARCHAR(50) NOT NULL PRIMARY KEY,
    SLOT_ID        INTEGER REFERENCES SLOTS,
    ACCOUNT_ID     VARCHAR(100),
    STATUS         VARCHAR(20),
    DATE           TIMESTAMP,
    NOTE           VARCHAR(200),
    PRICE          INTEGER,
    BOOK_AT        TIMESTAMP,
    REFERENCE      SERIAL,
    SUB_YARD_ID    VARCHAR(50),
    BIG_YARD_ID    VARCHAR(50),
    ORIGINAL_PRICE INTEGER,
    VOUCHER_CODE   VARCHAR(20)
);
ALTER SEQUENCE booking_reference_seq RESTART WITH 1000;

CREATE TABLE VOTES
(
    ID         VARCHAR(50)             NOT NULL PRIMARY KEY,
    BOOKING_ID VARCHAR(50) REFERENCES BOOKING ON DELETE CASCADE,
    SCORE      INTEGER,
    COMMENT    VARCHAR(200),
    DATE       TIMESTAMP DEFAULT NOW() NOT NULL,
    IS_DELETED BOOLEAN   DEFAULT FALSE
);


CREATE TABLE BOOKING_HISTORY
(
    ID             VARCHAR(50),
    BOOKING_ID     VARCHAR(50) REFERENCES BOOKING,
    CREATED_BY     VARCHAR(50) REFERENCES ACCOUNTS,
    BOOKING_STATUS VARCHAR(20),
    CREATED_AT     TIMESTAMP DEFAULT NOW(),
    NOTE           VARCHAR(200),
    REFERENCE      SERIAL
);
ALTER SEQUENCE booking_history_reference_seq RESTART WITH 1000;

CREATE TABLE VOUCHERS
(
    ID           VARCHAR(50) NOT NULL PRIMARY KEY,
    TYPE         VARCHAR(20),
    TITLE        VARCHAR(200),
    DESCRIPTION  TEXT,
    IS_ACTIVE    BOOLEAN   DEFAULT TRUE,
    MAX_QUANTITY INTEGER,
    USAGES       INTEGER   DEFAULT 0,
    VOUCHER_CODE VARCHAR(20) UNIQUE,
    DISCOUNT     DOUBLE PRECISION,
    START_DATE   TIMESTAMP,
    END_DATE     TIMESTAMP,
    STATUS       VARCHAR(20),
    CREATED_BY   VARCHAR(50) REFERENCES ACCOUNTS,
    CREATED_AT   TIMESTAMP DEFAULT NOW(),
    REFERENCE    SERIAL
);
ALTER SEQUENCE vouchers_reference_seq RESTART WITH 1000;

CREATE TABLE YARD_REPORT
(
    ID         VARCHAR(50) NOT NULL PRIMARY KEY,
    USER_ID    VARCHAR(50),
    YARD_ID    VARCHAR(50),
    REASON     VARCHAR(300),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,
    STATUS     VARCHAR(50),
    REFERENCE  SERIAL
);
ALTER SEQUENCE yard_report_reference_seq RESTART WITH 1000;

INSERT INTO type_yards (id, type_name) VALUES (DEFAULT, '3 VS 3');
INSERT INTO type_yards (id, type_name) VALUES (DEFAULT, '5 VS 5');

INSERT INTO roles (role_name) VALUES ('user');
INSERT INTO roles (role_name) VALUES ('admin');
INSERT INTO roles (role_name) VALUES ('owner');

INSERT INTO provinces (id, province_name) VALUES (58, 'Thành phố Hồ Chí Minh');
INSERT INTO provinces (id, province_name) VALUES (24, 'Hà Nội');
INSERT INTO provinces (id, province_name) VALUES (15, 'Đà Nẵng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Bình Chánh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Cần Giờ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Củ Chi');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Hóc Môn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Nhà Bè');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 1');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 10');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 11');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 12');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 3');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 4');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 5');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 6');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 7');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 8');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Bình Tân');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Bình Thạnh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Gò Vấp');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Phú Nhuận');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Tân Bình');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Tân Phú');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Thành phố Thủ Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Ba Đình');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hoàn Kiếm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Tây Hồ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Long Biên');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Cầu Giấy');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Đống Đa');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hai Bà Trưng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hoàng Mai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Thanh Xuân');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Sóc Sơn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Đông Anh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Gia Lâm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Nam Từ Liêm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thanh Trì');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Bắc Từ Liêm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Mê Linh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hà Đông');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Thị xã Sơn Tây');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Ba Vì');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Phúc Thọ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Đan Phượng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Hoài Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Quốc Oai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thạch Thất');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Chương Mỹ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thanh Oai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thường Tín');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Phú Xuyên');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Ứng Hòa');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Mỹ Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Liên Chiểu');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Thanh Khê');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Hải Châu');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Sơn Trà');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Ngũ Hành Sơn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Cẩm Lệ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Huyện Hòa Vang');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Huyện Hoàng Sa');

INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('2274575e-8991-44a1-b85f-685baf27a72b', 1002, 'trungnmse150182@fpt.edu.vn', '0335840116', 'Minh Trung', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', null, true, true, '2022-07-31 01:01:11.778229');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('172afebb-319f-4260-b6c1-08db8ee18226', 1001, 'toannbse150270@fpt.edu.vn', '0337850114', 'Bảo Toàn', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', null, true, true, '2022-07-31 02:01:03.475134');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 1000, 'maianh@gmail.com', '0334840120', 'Mai Anh', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', null, true, true, '2022-07-31 01:49:49.269197');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('2a1d060d-58b4-40a7-92ab-82dd56f59373', 1000, 'giangphse150249@fpt.edu.vn', '0336984556', 'Hà Giang', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', null, true, true, '2022-07-31 17:10:05.000000');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('8025ca69-50eb-4df7-8a28-a1416439d7e6', 1000, 'tantqse150208@fpt.edu.vn', '0334840119', 'Quan Tân', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/e6b3430b-0346-4410-9f69-efa78536c452?alt=media', true, true, '2022-07-31 01:49:49.269197');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('38c1ab75-6e7a-4899-8879-41b8a52eb0c9', 1002, 'nguyenminhtrungfacebook@gmail.com', '0337895401', 'Đức Quang', '$2a$10$T/lmAsMouZpznEt4YkJkAe53zwqZH1NDfUc.OsjhsoCKmdNOmoAp6', null, true, true, '2022-07-31 17:59:48.713486');

INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('f1292a0c-7874-4cb9-991b-d85597b04dbe', 'Sân A', '2022-08-01 00:28:12.191213', '756 Đường Nguyễn Trãi, Huyện Bình Chánh, Thành phố Hồ Chí Minh', 1000, true, true, '2274575e-8991-44a1-b85f-685baf27a72b', '00:00:00', '23:30:00', 30, 0, 0, '2022-07-31 17:28:12.207091', 0);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'Sân bóng rổ Thanh Xuân', '2022-07-31 18:03:11.337222', '166 Khuất Duy Tiến, Quận Thanh Xuân, Hà Nội', 1030, true, false, '38c1ab75-6e7a-4899-8879-41b8a52eb0c9', '07:00:00', '22:00:00', 60, 0, 0, '2022-07-31 11:03:11.341336', 0);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'Sân Vân Đồn', '2022-07-31 01:14:13.975192', '120-122 Đường Khánh Hội, Quận 4, TP.Hồ Chí Minh', 1010, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '20:00:00', 60, 100, 2, '2022-07-30 18:14:13.976058', 5);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'Sân Quận 8', '2022-07-31 01:39:10.903149', '8 Đường Quang Đông, Quận 8, TP.Hồ Chí Minh', 1014, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 80, 1, '2022-07-30 18:39:10.908071', 4);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'Sân HUTECH', '2022-07-31 09:39:32.610207', '475A Điện Biên Phủ, Phường 25, Quận Bình Thạnh, Thành phố Hồ Chí Minh', 1016, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '07:00:00', '22:00:00', 60, 0, 0, '2022-07-31 02:39:32.612555', 5);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'Sân Quận Thủ Đức', '2022-07-31 01:42:54.668541', '402A Lê Văn Việt, Tăng Nhơn Phú A, Thủ Đức, Thành phố Hồ Chí Minh', 1021, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 90, 1, '2022-07-30 18:42:54.670530', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'Sân Bóng Rổ Quận 11', '2022-07-31 01:41:14.927749', '8 Đường Quang Đông, Quận 8, Thành phố Hồ Chí Minh', 1007, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '07:00:00', '20:00:00', 60, 80, 2, '2022-07-30 18:41:14.929550', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'Sân Quận 7', '2022-07-31 01:17:09.761265', '702 Nguyễn Văn Linh, Tân Hưng, Quận 7, Thành phố Hồ Chí Minh', 1013, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '05:00:00', '22:00:00', 60, 85, 2, '2022-07-30 18:17:09.762290', 2);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'Sân Bóng Rổ Hoa Lư', '2022-07-31 01:09:31.752206', '2 Đinh Tiên Hoàng, Đài Kao, Quận 1, TP.Hồ Chí Minh', 1005, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '22:00:00', 60, 85, 2, '2022-07-30 18:09:31.757080', 4);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'Sân Tinh Võ', '2022-07-31 01:16:13.440919', '756 Đường Nguyễn Trãi, Quận 5, TP.Hồ Chí Minh', 1011, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 100, 2,'2022-07-30 18:16:13.442096', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'Sân Phan Đình Phùng', '2022-07-31 01:12:08.853909', '8 Võ Văn Tần, Quận 3, TP.Hồ Chí Minh', 1009, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '05:00:00', '22:00:00', 60, 65, 2, '2022-07-30 18:12:08.854834', 3);

INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 'Sân 2', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 1001, '2022-07-31 01:09:31.762472', true, true, false, '2022-07-31 01:09:31.762513');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('b2decd20-fd5b-4afe-85dc-c0c649db0acc', 'Sân 1', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 1000, '2022-07-31 01:09:31.763498', true, true, false, '2022-07-31 01:09:31.763555');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('444f7626-940b-4d42-9c42-d4696d4a7557', 'Sân 2', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 1001, '2022-07-31 01:12:08.857146', true, true, false, '2022-07-31 01:12:08.857179');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 'Sân 1', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 1000, '2022-07-31 01:12:08.857657', true, true, false, '2022-07-31 01:12:08.857671');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 'Sân 2', '95681556-4fd5-4504-9fbe-2471fd322702', 1001, '2022-07-31 01:14:13.977904', true, true, false, '2022-07-31 01:14:13.977933');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('e1843835-2c16-4764-8295-5b45215a9913', 'Sân 1', '95681556-4fd5-4504-9fbe-2471fd322702', 1000, '2022-07-31 01:14:13.978334', true, true, false, '2022-07-31 01:14:13.978346');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('12728671-60ff-4658-944e-225eb4a866cd', 'Sân 2', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 1000, '2022-07-31 01:16:13.444366', true, true, false, '2022-07-31 01:16:13.444415');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 'Sân 1', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 1000, '2022-07-31 01:16:13.444963', true, true, false, '2022-07-31 01:16:13.444992');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('d21d1788-87d4-4bf5-9ef3-c6881693e557', 'Sân 3', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.763620', true, true, false, '2022-07-31 01:37:16.763670');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('e1954053-1123-459d-809f-c50a86de626d', 'Sân 2', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.764618', true, true, false, '2022-07-31 01:37:16.764648');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('81281461-1816-4b13-964d-abdfdad52e37', 'Sân 1', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.765148', true, true, false, '2022-07-31 01:37:16.765165');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('625b9cb4-f186-45da-95ed-e14b1d5ff600', 'Sân 2', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 1001, '2022-07-31 01:39:10.909692', true, true, false, '2022-07-31 01:39:10.909723');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('98fcb70a-a40e-4659-b568-d8da28256976', 'Sân 1', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 1000, '2022-07-31 01:39:10.910237', true, true, false, '2022-07-31 01:39:10.910252');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 'Sân 2', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 1001, '2022-07-31 01:41:14.930998', true, true, false, '2022-07-31 01:41:14.931033');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('4b417364-437a-4044-9c8f-95c6db5dd84c', 'Sân 1', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 1000, '2022-07-31 01:41:14.931389', true, true, false, '2022-07-31 01:41:14.931404');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('b3a1d704-a1e4-478e-944a-d63d3703fb9a', 'Sân 3', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.672158', true, true, false, '2022-07-31 01:42:54.672236');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 'Sân 2', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.672765', true, true, false, '2022-07-31 01:42:54.672790');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('956d7e9c-594c-4f44-b8fd-fd70169f8366', 'Sân 1', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.673294', true, true, false, '2022-07-31 01:42:54.673318');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('f0a88a8b-550f-4de3-b80d-93e734111a84', 'Sân 1', '7db1754b-23f0-41f6-9e18-08bc904c1881', 1000, '2022-07-31 09:39:32.613616', true, true, false, '2022-07-31 09:39:32.613655');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('67cc05a9-8ebc-482c-a2ff-0cd258014716', 'Sân 2', '7db1754b-23f0-41f6-9e18-08bc904c1881', 1001, '2022-07-31 09:40:50.802402', true, true, false, '2022-07-31 09:40:50.802452');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('4c9a6473-885a-45d3-b415-7e709b185d9f', 'Sân 1', 'ef782200-1289-4ff7-a91d-9e66edab015f', 1000, '2022-07-31 18:03:11.345739', true, true, false, '2022-07-31 18:03:11.345780');

INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '21:00:00', '22:00:00', true);


INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/84ad02b9-2052-445a-bb5f-cba0dea885a9?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4f258f4d-ea7c-46b5-a79e-14b91903d3ed?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/53a254ec-c7fa-4ed5-b2f2-99ca32c3a0db?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/b368a5ff-ce21-49eb-bbb8-4cf918ca8ff3?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/9c50f7d4-f1ba-4d60-ace9-a98b8ae8709d?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4c2a3a0f-4f8a-441c-ba25-8e6bf2878ce7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/d4a51be7-e477-4bd8-9bd2-c60ca6acc18f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/1dfaa749-1317-4824-a668-18f5e8aca605?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/2acc32fd-e0af-465f-bbc8-551b481e670a?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/7f488ae9-c82b-4bec-a036-1ee814638b48.jpg?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/761eca26-ba5b-4103-8e87-c2327d103892.jpg?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a3892ac2-b3c9-481a-a912-8f10a62cd243.jpg?alt=media&token=4059c770-38be-427f-8d69-332d85bcc7e6');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4402fdc8-4f9d-4841-a0f3-ba37af05cbf7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/3592fb3e-fd32-4342-b021-555880834336?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/9c2ee165-d32c-4ba3-b191-d02e89bcd9a3?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/ba3b477d-22f5-4a59-aa70-e54a9900d4c8?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/73caa268-852f-4940-9972-cafd35879212?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/5c811291-c66e-450b-9c3c-f34f5eb2c373?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/bcf7e038-2a75-479d-88e2-67612cb1c93b?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a6a8c75d-01f0-4e4d-8d5d-4f6e4bff8094?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/583cdf85-014c-4a2a-9f54-7c9963118f6f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/f91dfc19-7b56-4454-bacf-27c0543e426e?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/77d5d692-f2c2-4506-893d-dc9f1699153f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a028fde6-aa86-49b7-a912-0418e35435d7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/1a81c14e-3973-4fbb-b2a2-d95e632db38f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/6a1c3841-2df3-419a-90fc-2c43eb626895?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a256a55a-20fa-49c0-a095-d690dbb01ec7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/3235d742-ad38-4443-b6ef-eb3ef4acceef?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/dab11c4c-485b-4a0a-9fa7-1a9d7cf10902?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('f1292a0c-7874-4cb9-991b-d85597b04dbe', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/20f6e2e0-2c40-4d8c-9eb8-13bfb0c74a65?alt=media');