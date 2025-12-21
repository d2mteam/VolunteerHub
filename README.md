# VolunteerHub API

VolunteerHub là nền tảng API hỗ trợ quản lý hoạt động tình nguyện, cho phép người dùng tạo hồ sơ, đăng ký/tham gia sự kiện và trao đổi nội dung cộng đồng qua bài viết, bình luận và lượt thích.

## Công nghệ
- **Java 21** và **Spring Boot 3.5.6** làm nền tảng ứng dụng, kết hợp Spring MVC/Security/Validation, JPA và JDBC.【F:build.gradle†L2-L31】
- **GraphQL** (spring-boot-starter-graphql, graphql-java-extended-scalars) cung cấp kênh truy vấn dữ liệu đọc.【F:build.gradle†L37-L48】
- **Redis** cho cache, **PostgreSQL** làm cơ sở dữ liệu quan hệ, tích hợp **Lombok** và **Dotenv** hỗ trợ cấu hình.【F:build.gradle†L48-L63】

## Mục đích & phạm vi API
- **Xác thực & tài khoản**: đăng ký, xác minh email, đăng nhập và refresh token an toàn.【F:volunteerhub_graphql_api.md†L10-L34】
- **Hồ sơ & cộng đồng**: tạo/cập nhật hồ sơ người dùng, đăng bài viết, bình luận và quản lý lượt thích.【F:volunteerhub_graphql_api.md†L36-L81】【F:volunteerhub_graphql_api.md†L93-L104】
- **Sự kiện & tham gia**: event manager tạo/cập nhật/xóa hoặc phê duyệt sự kiện; người dùng đăng ký/hủy và event manager duyệt/ từ chối đăng ký.【F:volunteerhub_graphql_api.md†L83-L116】
- **Quản trị & báo cáo**: khóa/mở khóa người dùng và xuất danh sách tình nguyện viên theo định dạng CSV/JSON.【F:volunteerhub_graphql_api.md†L118-L132】
- **GraphQL đọc dữ liệu**: truy vấn hồ sơ, sự kiện, bài viết, danh sách phân trang và thống kê dashboard qua endpoint `/graphql`.【F:volunteerhub_graphql_api.md†L134-L183】