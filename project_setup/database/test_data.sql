-- =============================================
-- SAMPLE DATA FOR CHARITY EVENT MANAGEMENT
-- Database: PostgreSQL
-- All passwords: 12345 (BCrypt encoded)
-- =============================================

-- 1. INSERT INTO user_profiles
INSERT INTO user_profiles (user_id, avatar_url, created_at, email, full_name, password, role, status, username)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'https://example.com/avatars/admin.jpg', NOW(), 'admin@charity.com', 'System Administrator',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_ADMIN', 'ACTIVE', 'admin'),

    ('b2c3d4e5-f6a7-8901-bcde-f2345678901a', 'https://example.com/avatars/manager.jpg', NOW(), 'manager@charity.com', 'Nguyễn Văn Quản Lý',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_EVENT_MANAGER', 'ACTIVE', 'eventmanager'),

    ('c3d4e5f6-a7b8-9012-cdef-3456789012bc', 'https://example.com/avatars/van.jpg', NOW(), 'van@volunteer.com', 'Trần Thị Vân',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_VOLUNTEER', 'ACTIVE', 'van_vol'),

    ('d4e5f6a7-b8c9-0123-def0-4567890123cd', 'https://example.com/avatars/minh.jpg', NOW(), 'minh@volunteer.com', 'Lê Hoàng Minh',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_VOLUNTEER', 'ACTIVE', 'minh_vol'),

    ('e5f6a7b8-c9d0-1234-ef01-5678901234de', 'https://example.com/avatars/lan.jpg', NOW(), 'lan@volunteer.com', 'Phạm Thị Lan',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_VOLUNTEER', 'ACTIVE', 'lan_vol'),

    ('f6a7b8c9-d0e1-2345-f012-6789012345ef', 'https://example.com/avatars/tuan.jpg', NOW(), 'tuan@volunteer.com', 'Hoàng Anh Tuấn',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye6N5Z1c9c9j1q2f3g4h5i6j7k8l9m0n1p2q', 'ROLE_VOLUNTEER', 'ACTIVE', 'tuan_vol');


-- 2. INSERT INTO events
INSERT INTO events (event_id, created_at, event_description, event_location, event_name, event_state, updated_at, created_by)
VALUES
    (1, NOW(), 'Thu gom quần áo cũ và sách vở cho trẻ em vùng cao tại Hà Giang. Cần 50 tình nguyện viên hỗ trợ phân loại và vận chuyển.',
     'Trường Tiểu học Nà Chì, Xín Mần, Hà Giang', 'Áo Ấm Cho Em - Hà Giang 2025', 'Accepted', NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a'),

    (2, NOW(), 'Phát quà Tết cho 200 hộ nghèo tại quận Thủ Đức. Bao gồm gạo, dầu ăn, mì gói và bánh chưng.',
     'Nhà văn hóa phường Linh Chiểu, TP. Thủ Đức, TP.HCM', 'Xuân Yêu Thương 2025', 'Accepted', NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a');


-- 3. INSERT INTO role_in_event
INSERT INTO role_in_event (id, created_at, event_role, updated_at, event_id, user_profile_id)
VALUES
    (1, NOW(), 'EVENT_ADMIN', NOW(), 1, 'b2c3d4e5-f6a7-8901-bcde-f2345678901a'),
    (2, NOW(), 'EVENT_MEMBER', NOW(), 1, 'c3d4e5f6-a7b8-9012-cdef-3456789012bc'),
    (3, NOW(), 'EVENT_MEMBER', NOW(), 1, 'd4e5f6a7-b8c9-0123-def0-4567890123cd'),

    (4, NOW(), 'EVENT_ADMIN', NOW(), 2, 'b2c3d4e5-f6a7-8901-bcde-f2345678901a'),
    (5, NOW(), 'EVENT_MEMBER', NOW(), 2, 'e5f6a7b8-c9d0-1234-ef01-5678901234de'),
    (6, NOW(), 'EVENT_MEMBER', NOW(), 2, 'f6a7b8c9-d0e1-2345-f012-6789012345ef');


-- 4. INSERT INTO posts
INSERT INTO posts (post_id, content, created_at, updated_at, created_by, event_id)
VALUES
    (1, 'Chúng tôi đã chuẩn bị 10 thùng quần áo và 500 cuốn sách. Cần thêm tình nguyện viên ngày 10/11 để phân loại!', NOW(), NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a', 1),
    (2, 'Cập nhật: Đã có 30 tình nguyện viên đăng ký. Cảm ơn các bạn! Vẫn cần thêm 20 người nữa.', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 1),

    (3, 'Danh sách quà Tết đã được chuẩn bị: 200 phần quà (gạo, mì, dầu ăn). Phát vào ngày 25/01/2025.', NOW(), NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a', 2),
    (4, 'Cần 15 tình nguyện viên hỗ trợ gói quà và phát quà tại chỗ. Ai tham gia comment bên dưới nhé!', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 2);


-- 5. INSERT INTO comments (10 per post)
-- Post 1
INSERT INTO comments (comment_id, content, created_at, updated_at, created_by, post_id) VALUES
                                                                                            (1, 'Mình có thể tham gia ngày 10/11 được không ạ?', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 1),
                                                                                            (2, 'Mình mang thêm sách cũ được không?', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 1),
                                                                                            (3, 'Cho mình đăng ký ạ!', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 1),
                                                                                            (4, 'Có cần mang theo dụng cụ gì không?', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 1),
                                                                                            (5, 'Mình ở Hà Nội, có thể gửi đồ qua bưu điện được không?', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 1),
                                                                                            (6, 'Cảm ơn chương trình rất ý nghĩa!', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 1),
                                                                                            (7, 'Mình sẽ mang theo 2 thùng quần áo trẻ em.', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 1),
                                                                                            (8, 'Có lịch trình cụ thể chưa ạ?', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 1),
                                                                                            (9, 'Mình muốn làm trưởng nhóm phân loại, có được không?', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 1),
                                                                                            (10, 'Tuyệt vời! Mình ủng hộ!', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 1);

-- Post 2
INSERT INTO comments (comment_id, content, created_at, updated_at, created_by, post_id) VALUES
                                                                                            (11, 'Mình đăng ký thêm 2 người bạn nữa nhé!', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 2),
                                                                                            (12, 'Còn thiếu bao nhiêu người nữa ạ?', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 2),
                                                                                            (13, 'Mình có xe máy, có thể chở đồ được.', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 2),
                                                                                            (14, 'Cập nhật danh sách tình nguyện viên ở đâu ạ?', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 2),
                                                                                            (15, 'Mình ở xa, có thể hỗ trợ tiền xăng không?', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 2),
                                                                                            (16, 'Cảm ơn các bạn đã nhiệt tình!', NOW(), NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a', 2),
                                                                                            (17, 'Mình sẽ đến sớm để setup.', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 2),
                                                                                            (18, 'Có cần áo đồng phục không?', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 2),
                                                                                            (19, 'Mình mang theo nước uống cho mọi người nhé.', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 2),
                                                                                            (20, 'Chương trình rất tuyệt vời!', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 2);

-- Post 3
INSERT INTO comments (comment_id, content, created_at, updated_at, created_by, post_id) VALUES
                                                                                            (21, 'Mình muốn tham gia phát quà!', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 3),
                                                                                            (22, 'Có danh sách hộ nhận quà chưa ạ?', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 3),
                                                                                            (23, 'Mình có thể mang bánh chưng nhà làm góp vào.', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 3),
                                                                                            (24, 'Thời gian phát quà từ mấy giờ?', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 3),
                                                                                            (25, 'Mình ở quận 7, có thể đi chung xe không?', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 3),
                                                                                            (26, 'Cảm ơn ban tổ chức đã giúp người nghèo.', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 3),
                                                                                            (27, 'Mình đăng ký 3 người.', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 3),
                                                                                            (28, 'Có cần mang theo giấy tờ gì không?', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 3),
                                                                                            (29, 'Mình sẽ chụp ảnh lưu giữ khoảnh khắc.', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 3),
                                                                                            (30, 'Rất mong được tham gia!', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 3);

-- Post 4
INSERT INTO comments (comment_id, content, created_at, updated_at, created_by, post_id) VALUES
                                                                                            (31, 'Mình comment để đăng ký!', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 4),
                                                                                            (32, 'Cần hỗ trợ gói quà từ bao giờ?', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 4),
                                                                                            (33, 'Mình mang theo băng rôn được không?', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 4),
                                                                                            (34, 'Có chỗ giữ xe không ạ?', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 4),
                                                                                            (35, 'Mình sẽ mang nước suối cho mọi người.', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 4),
                                                                                            (36, 'Cảm ơn các bạn đã lan tỏa yêu thương!', NOW(), NOW(), 'b2c3d4e5-f6a7-8901-bcde-f2345678901a', 4),
                                                                                            (37, 'Mình muốn làm nhiếp ảnh tình nguyện.', NOW(), NOW(), 'd4e5f6a7-b8c9-0123-def0-4567890123cd', 4),
                                                                                            (38, 'Có cần mặc áo nhóm không?', NOW(), NOW(), 'e5f6a7b8-c9d0-1234-ef01-5678901234de', 4),
                                                                                            (39, 'Mình ở gần đó, sẽ đến sớm.', NOW(), NOW(), 'f6a7b8c9-d0e1-2345-f012-6789012345ef', 4),
                                                                                            (40, 'Chương trình quá ý nghĩa!', NOW(), NOW(), 'c3d4e5f6-a7b8-9012-cdef-3456789012bc', 4);


-- 6. INSERT INTO likes (một vài comment + post được like)
INSERT INTO likes (like_id, created_at, target_type, target_id, created_by)
VALUES
    (1, NOW(), 'COMMENT', 1, 'd4e5f6a7-b8c9-0123-def0-4567890123cd'),
    (2, NOW(), 'COMMENT', 1, 'e5f6a7b8-c9d0-1234-ef01-5678901234de'),
    (3, NOW(), 'COMMENT', 6, 'c3d4e5f6-a7b8-9012-cdef-3456789012bc'),
    (4, NOW(), 'COMMENT', 16, 'f6a7b8c9-d0e1-2345-f012-6789012345ef'),
    (5, NOW(), 'COMMENT', 21, 'e5f6a7b8-c9d0-1234-ef01-5678901234de'),
    (6, NOW(), 'COMMENT', 23, 'f6a7b8c9-d0e1-2345-f012-6789012345ef'),
    (7, NOW(), 'COMMENT', 31, 'd4e5f6a7-b8c9-0123-def0-4567890123cd'),
    (8, NOW(), 'COMMENT', 36, 'c3d4e5f6-a7b8-9012-cdef-3456789012bc'),
    (9, NOW(), 'POST', 1, 'e5f6a7b8-c9d0-1234-ef01-5678901234de'),
    (10, NOW(), 'POST', 3, 'f6a7b8c9-d0e1-2345-f012-6789012345ef');


-- =============================================
-- HOÀN TẤT: Dữ liệu mẫu đã sẵn sàng!
-- Tổng: 6 users | 2 events | 4 posts | 40 comments | 10 likes
-- Mật khẩu: 12345 (BCrypt)
-- =============================================


-- bcrypt for password "123456789abc"
-- $2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'admin@charity.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'ADMIN',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f2345678901a',
        'manager@charity.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'EVENT_MANAGER',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('f6a7b8c9-d0e1-2345-f012-6789012345ef',
        'tuan@volunteer.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('d4e5f6a7-b8c9-0123-def0-4567890123cd',
        'minh@volunteer.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('c3d4e5f6-a7b8-9012-cdef-3456789012bc',
        'van@volunteer.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('e5f6a7b8-c9d0-1234-ef01-5678901234de',
        'lan@volunteer.com',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');

-- auto-generated email users
INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('208eafed-390f-4f8a-abbe-db3d56a2f3c9',
        '208eafed@auto.local',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');

INSERT INTO user_auth (user_id, email, email_verified, password_hash, role, status)
VALUES ('35040e69-cdd4-4c3e-a74d-544ba46ce572',
        '35040e69@auto.local',
        true,
        '$2b$10$5LHgOeGZwoMPmxjSPdZnegBtWDypYyXAmd6O9GOVBLe7gOkg5WAdm',
        'USER',
        'ACTIVE');
