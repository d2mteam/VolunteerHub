# 📘 VolunteerHub API Guide — REST & GraphQL (project endpoints)

## 🔹 REST API

**Base URL:** `http://localhost:8080`

> Tất cả REST endpoints trả về JSON. Các thao tác ghi (tạo/sửa/xóa) trả về `ModerationResponse` với các trường:
> `result` (`SUCCESS|DENIED|INVALID|NOT_FOUND|ERROR`), `action`, `targetType`, `targetId`, `status`, `message`, `reasonCode?`, `moderatedAt`.

### 🧾 Authentication & Account

| Method & Path | Mô tả | Payload | Phản hồi chính |
| --- | --- | --- | --- |
| `POST /api/auth/signup` | Đăng ký tài khoản (có thể đánh dấu event manager) | `{ "email", "password", "eventManager" }` (`eventManager` boolean) | `{ "message": "Signup successful. Please check your email." }`【F:src/main/java/com/volunteerhub/authentication/controller/SignupController.java†L15-L25】【F:src/main/java/com/volunteerhub/authentication/dto/request/SignUpRequest.java†L10-L23】 |
| `GET /api/auth/verify-email?token=...` | Xác minh email từ link gửi về mail | query `token` | `{ "message": "Verify successful" }`【F:src/main/java/com/volunteerhub/authentication/controller/SignupController.java†L27-L33】 |
| `GET /api/auth/resend?email=...` | Gửi lại email xác minh | query `email` | `{ "message": "Resend successful" }`【F:src/main/java/com/volunteerhub/authentication/controller/SignupController.java†L35-L39】 |
| `POST /api/auth/login` | Đăng nhập, đặt `refresh_token` cookie (HttpOnly, Secure, SameSite=Strict) | `{ "email", "password" }` | `{ "accessToken": "..." }`【F:src/main/java/com/volunteerhub/authentication/controller/LoginController.java†L24-L38】【F:src/main/java/com/volunteerhub/authentication/dto/request/LoginRequest.java†L8-L19】 |
| `POST /api/auth/refresh` | Lấy access token mới bằng cookie `refresh_token` | Cookie `refresh_token` gửi kèm request | `{ "accessToken": "..." }` (và cookie refresh mới)【F:src/main/java/com/volunteerhub/authentication/controller/LoginController.java†L40-L52】 |

### 👤 User Profiles (USER / EVENT_MANAGER)

`Authorization: Bearer <accessToken>`

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/user-profiles` | `{ "email"?, "fullName", "username", "avatarId"?, "bio"? }` | Tạo hồ sơ người dùng【F:src/main/java/com/volunteerhub/community/controller/rest/UserProfileController.java†L17-L26】【F:src/main/java/com/volunteerhub/community/dto/rest/request/EditUserProfileInput.java†L8-L25】 |
| `PUT /api/user-profiles` | Như trên | Cập nhật hồ sơ【F:src/main/java/com/volunteerhub/community/controller/rest/UserProfileController.java†L28-L35】 |

### 📝 Posts (USER)

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/posts` | `{ "eventId", "content" }` | Tạo bài viết trong sự kiện【F:src/main/java/com/volunteerhub/community/controller/rest/PostController.java†L18-L27】【F:src/main/java/com/volunteerhub/community/dto/rest/request/CreatePostInput.java†L10-L17】 |
| `PUT /api/posts` | `{ "postId", "content" }` | Sửa bài viết【F:src/main/java/com/volunteerhub/community/controller/rest/PostController.java†L29-L36】【F:src/main/java/com/volunteerhub/community/dto/rest/request/EditPostInput.java†L10-L17】 |
| `DELETE /api/posts/{postId}` | — | Xóa bài viết【F:src/main/java/com/volunteerhub/community/controller/rest/PostController.java†L38-L46】 |

### 💬 Comments (USER)

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/comments` | `{ "postId", "content" }` | Tạo bình luận【F:src/main/java/com/volunteerhub/community/controller/rest/CommentController.java†L18-L27】【F:src/main/java/com/volunteerhub/community/dto/rest/request/CreateCommentInput.java†L8-L12】 |
| `PUT /api/comments` | `{ "commentId", "content" }` | Sửa bình luận【F:src/main/java/com/volunteerhub/community/controller/rest/CommentController.java†L29-L36】【F:src/main/java/com/volunteerhub/community/dto/rest/request/EditCommentInput.java†L8-L17】 |
| `DELETE /api/comments/{commentId}` | — | Xóa bình luận【F:src/main/java/com/volunteerhub/community/controller/rest/CommentController.java†L38-L45】 |

### 🎟️ Events (EVENT_MANAGER, một số route yêu cầu ADMIN)

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/events` | `{ "eventName", "eventDescription", "eventLocation" }` | Tạo sự kiện【F:src/main/java/com/volunteerhub/community/controller/rest/EventController.java†L18-L28】【F:src/main/java/com/volunteerhub/community/dto/rest/request/CreateEventInput.java†L12-L23】 |
| `PUT /api/events` | `{ "eventId", "eventName", "eventDescription", "eventLocation" }` | Cập nhật sự kiện【F:src/main/java/com/volunteerhub/community/controller/rest/EventController.java†L30-L37】【F:src/main/java/com/volunteerhub/community/dto/rest/request/EditEventInput.java†L12-L25】 |
| `DELETE /api/events/{eventId}` | — | Xóa sự kiện【F:src/main/java/com/volunteerhub/community/controller/rest/EventController.java†L39-L47】 |
| `POST /api/events/{eventId}/approve` | — | Duyệt sự kiện (ADMIN)【F:src/main/java/com/volunteerhub/community/controller/rest/EventController.java†L49-L55】 |

### 🤝 Event Registration (USER / EVENT_MANAGER)

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/events/{eventId}/registrations` | — | Đăng ký tham gia sự kiện (USER)【F:src/main/java/com/volunteerhub/community/controller/rest/EventRegistrationController.java†L18-L26】 |
| `DELETE /api/events/{eventId}/registrations` | — | Hủy đăng ký (USER)【F:src/main/java/com/volunteerhub/community/controller/rest/EventRegistrationController.java†L28-L35】 |
| `POST /api/event-registrations/{registrationId}/approve` | — | Phê duyệt đăng ký (EVENT_MANAGER)【F:src/main/java/com/volunteerhub/community/controller/rest/EventRegistrationController.java†L37-L43】 |
| `POST /api/event-registrations/{registrationId}/reject` | — | Từ chối đăng ký (EVENT_MANAGER)【F:src/main/java/com/volunteerhub/community/controller/rest/EventRegistrationController.java†L45-L51】 |

### ❤️ Likes (USER / EVENT_MANAGER)

`targetType` nhận giá trị từ enum `COMMENT`, `POST`, `EVENT`, `LIKE`.

| Method & Path | Payload | Mô tả |
| --- | --- | --- |
| `POST /api/likes` | `{ "targetId", "targetType" }` | Thích nội dung【F:src/main/java/com/volunteerhub/community/controller/rest/LikeController.java†L17-L25】【F:src/main/java/com/volunteerhub/community/model/db_enum/TableType.java†L3-L5】 |
| `DELETE /api/likes` | `{ "targetId", "targetType" }` | Bỏ thích【F:src/main/java/com/volunteerhub/community/controller/rest/LikeController.java†L27-L34】 |

### 🛡️ User Moderation (ADMIN)

| Method & Path | Mô tả |
| --- | --- |
| `POST /api/users/{userId}/ban` | Khóa người dùng【F:src/main/java/com/volunteerhub/community/controller/rest/UserManagementController.java†L15-L22】 |
| `DELETE /api/users/{userId}/ban` | Mở khóa người dùng【F:src/main/java/com/volunteerhub/community/controller/rest/UserManagementController.java†L24-L30】 |

### 📤 Exports (ADMIN)

| Method & Path | Payload | Phản hồi |
| --- | --- | --- |
| `POST /api/exports/event-volunteers` | `{ "eventId", "format": "CSV"|"JSON" }` | CSV (kèm header download) hoặc JSON danh sách volunteer tùy `format`【F:src/main/java/com/volunteerhub/export_data/ExportController.java†L19-L37】 |

---

## 🔹 GraphQL API (read-only)

**Endpoint:** `POST http://localhost:8080/graphql`  
**Auth:** Tùy query; `@AuthenticationPrincipal` chỉ dùng cho `userHistory`.

### Top-level Queries

| Query | Args | Trả về |
| --- | --- | --- |
| `getUserProfile(userId: ID!)` | `userId` (UUID) | `UserProfile`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/UserProfileResolver.java†L21-L32】 |
| `getEvent(eventId: ID!)` | — | `Event`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/EventResolver.java†L30-L35】 |
| `getPost(postId: ID!)` | — | `Post`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/PostResolver.java†L31-L35】 |
| `findEvents(page: Int = 0, size: Int = 10, filter: JSON = null)` | phân trang | `OffsetPage<Event>`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/EventResolver.java†L37-L52】【F:src/main/resources/graphql/schema.graphqls†L41-L53】 |
| `findPosts(page: Int = 0, size: Int = 10)` | phân trang | `OffsetPage<Post>`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/PostResolver.java†L37-L49】【F:src/main/resources/graphql/schema.graphqls†L55-L59】 |
| `findUserProfiles(page: Int = 0, size: Int = 10)` | phân trang | `OffsetPage<UserProfile>`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/UserProfileResolver.java†L17-L32】【F:src/main/resources/graphql/schema.graphqls†L61-L66】 |
| `listMemberInEvent(eventId: ID!, page: Int = 0, size: Int = 10)` | — | `OffsetPage<RoleInEvent>`【F:src/main/java/com/volunteerhub/community/controller/graphql/query/HistoryResolver.java†L19-L33】【F:src/main/resources/graphql/schema.graphqls†L68-L70】 |
| `userHistory(page: Int = 0, size: Int = 10)` | requires login | Lịch sử tham gia (`OffsetPage<RoleInEvent>`)【F:src/main/java/com/volunteerhub/community/controller/graphql/query/HistoryResolver.java†L35-L49】【F:src/main/resources/graphql/schema.graphqls†L70-L71】 |
| `dashboardOverview(hours: Int = 24, size: Int = 5)` | bộ lọc thời gian & size | `DashboardOverview` (trending/new posts, v.v.)【F:src/main/java/com/volunteerhub/community/controller/graphql/query/DashboardResolver.java†L20-L47】【F:src/main/resources/graphql/schema.graphqls†L72-L74】 |

### Schema Highlights & Pagination

- `PageInfo` trong `OffsetPage` gồm `page`, `size`, `totalElements`, `totalPages`, `hasNext`, `hasPrevious`【F:src/main/resources/graphql/schema.graphqls†L25-L48】.
- `Event` có các field phân trang: `listPost(page,size)` và `listMember(page,size)`; `Post` có `listComment(page,size)`; các field `likeCount` và `createBy` được resolver tính toán.【F:src/main/java/com/volunteerhub/community/controller/graphql/query/EventResolver.java†L54-L91】【F:src/main/java/com/volunteerhub/community/controller/graphql/query/PostResolver.java†L51-L69】

### Ví dụ Query (event + post/comment)

```graphql
query ExampleEvent($eventId: ID!) {
  getEvent(eventId: $eventId) {
    eventId
    eventName
    eventDescription
    eventLocation
    likeCount
    createBy { userId username avatarId }
    listPost(page: 0, size: 10) {
      pageInfo { page size totalElements totalPages hasNext hasPrevious }
      content {
        postId
        content
        likeCount
        createBy { userId username }
        listComment(page: 0, size: 5) {
          pageInfo { page size totalElements totalPages hasNext hasPrevious }
          content { commentId content likeCount createBy { userId username } }
        }
      }
    }
  }
}
```

> Lưu ý: Các thao tác ghi (tạo/sửa/xóa) hiện diện dưới REST, GraphQL chỉ phục vụ đọc dữ liệu.
