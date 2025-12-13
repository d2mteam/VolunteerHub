# 📘 VolunteerHub API Guide — REST & GraphQL

## 🔹 REST API Endpoints

### 🧾 Authentication

#### 🔑 Login

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

**Request body:**dơn

```json
{
  "email": "",
  "password": ""
}
```

**Response:**

```json
{
  "accessToken": "xxx",
  "refreshToken": "yyy",
  "tokenType": "Bearer"
}
```

> `refreshToken` thường set trong **HttpOnly cookie**; `accessToken` dùng cho Authorization header.

---

#### 🔄 Refresh Token

```
POST http://localhost:8080/api/auth/refresh
```

- Lấy `refreshToken` từ **cookie**
- **Response:**

```json
{
  "accessToken": "new_xxx",
  "tokenType": "Bearer"
}
```

---

#### 📝 Signup

```
POST http://localhost:8080/api/auth/signup
Content-Type: application/json
```

**Request body:**

```json
{
  "email": "",
  "password": ""
}
```

**Response:**

```json
{
  "ok": true,
  "message": "User registered successfully",
  "id": "uuid-generated"
}
```

---

### 🧾 User Profile

> Yêu cầu đăng nhập thành công (Authorization: Bearer `<accessToken>`)

```
PUT http://localhost:8080/api/user-profile
Content-Type: application/json
Authorization: Bearer <accessToken>
```

**Request body:**

```json
{
  "email": "",
  "fullName": "",
  "username": "",
  "avatarId": "",
  "bio": ""
}
```

**Response:**

```json
{
  "ok": true,
  "message": "Profile updated successfully",
  "id": "uuid-generated",
  "updatedAt": "2025-12-05T16:00:00Z"
}
```

---

## 🔹 REST API for **Write** Operations

> Tách các thao tác **create / update / delete** khỏi GraphQL; các route sau trả về schema giống `MutationResult` (`ok`, `id`, `message`, `createdAt`, `updatedAt`).

### 📝 Posts (`USER`)

```
POST   /api/posts                      # create post
PUT    /api/posts/{postId}             # edit post
DELETE /api/posts/{postId}             # delete post
```

**Request body (create/edit):**

```json
{
  "eventId": "<eventId>",
  "content": "<text>"
}
```

### 💬 Comments (`USER`)

```
POST   /api/comments                   # create comment
PUT    /api/comments/{commentId}       # edit comment
DELETE /api/comments/{commentId}       # delete comment
```

**Request body (create/edit):**

```json
{
  "postId": "<postId>",
  "content": "<text>"
}
```

### ❤️ Likes (`USER`)

```
POST   /api/likes                      # like (body: targetId, targetType)
DELETE /api/likes                      # unlike (body: targetId, targetType)
```

### 🎟️ Event Participation (`USER`)

```
POST   /api/events/{eventId}/registrations     # register
DELETE /api/events/{eventId}/registrations     # unregister
POST   /api/event-registrations/{id}/approve   # approve registration
POST   /api/event-registrations/{id}/reject    # reject registration
```

### 🧭 Event Management (`EVENT_MANAGER`)

```
POST   /api/events                     # create event
PUT    /api/events/{eventId}           # edit event
DELETE /api/events/{eventId}           # delete event
POST   /api/events/{eventId}/approve   # approve event (ADMIN)
```

**Request body (create/edit):**

```json
{
  "eventName": "<text>",
  "eventDescription": "<text>",
  "eventLocation": "<text>"
}
```

### 🛡️ Admin / Event Manager Moderation

```
POST   /api/users/{userId}/ban           # ban user
DELETE /api/users/{userId}/ban           # unban user
```

**Response (moderation route):**

```json
{
  "ok": true,
  "action": "BAN_USER",
  "targetType": "USER",
  "targetId": "c5b05670-5f6d-4e5b-9d82-5c34a8b9bf9b",
  "status": "BANNED",
  "message": "User c5b05670-5f6d-4e5b-9d82-5c34a8b9bf9b has been banned",
  "moderatedAt": "2025-11-04T07:52:12.124Z"
}
```

---

## 🔹 GraphQL API

**Base URL:**

```
GRAPHQL http://localhost:8080/graphql
Authorization: Bearer <accessToken>  # Optional for queries, required for mutations
```

- `UserId` sử dụng **UUID**
- Các `ID` khác (Post, Comment, Event) là **Snowflake ID dạng string**
- **Anonymous user**: chỉ query, mutation cần role (`USER`, `EVENT_MANAGER`, `ADMIN`)

---

## 🔸 Query Examples (Read)

### 🧱 1. Lấy chi tiết **Post**

```graphql
query {
    getPost(postId: "1") {
        postId
        eventId
        content
        createdAt
        updatedAt
        commentCount
        likeCount
        creatorInfo {
            userId
            username
            avatarId
        }
    }
}
```

---

### 🧱 2. Lấy chi tiết **Event** cùng danh sách Post & Comment

```graphql
query {
    getEvent(eventId: "1") {
        eventId
        eventName
        eventDescription
        eventLocation
        createdAt
        updatedAt
        memberCount
        postCount
        likeCount
        creatorInfo {
            userId
            username
            avatarId
        }

        listPosts(page: 0, size: 10) {
            pageInfo {
                page
                size
                totalElements
                totalPages
                hasNext
                hasPrevious
            }
            content {
                postId
                eventId
                content
                createdAt
                updatedAt
                commentCount
                likeCount
                creatorInfo {
                    userId
                    username
                    avatarId
                }

                listComment(page: 0, size: 5) {
                    pageInfo {
                        page
                        size
                        totalElements
                        totalPages
                        hasNext
                        hasPrevious
                    }
                    content {
                        commentId
                        postId
                        content
                        createdAt
                        updatedAt
                        likeCount
                        creatorInfo {
                            userId
                            username
                            avatarId
                        }
                    }
                }
            }
        }
    }
}
```

---

### 🧱 3. Lấy chi tiết **UserProfile** + Event tham gia

```graphql
query {
    getUserProfile(userId: "d4e5f6a7-b8c9-0123-def0-4567890123cd") {
        userId
        username
        fullName
        email
        status
        createdAt
        postCount
        commentCount
        eventCount

        listEvents(page: 0, size: 10) {
            pageInfo {
                page
                size
                totalElements
                totalPages
                hasNext
                hasPrevious
            }
            content {
                eventId
                eventName
                eventDescription
                eventLocation
                createdAt
                updatedAt
                memberCount
                postCount
                likeCount
                creatorInfo {
                    userId
                    username
                    avatarId
                }
            }
        }
    }
}
```

---

## 🔸 Mutation Examples (Write)

- Các mutation tạo/sửa/xóa (post/comment/event/like/register/unregister) trả về **MutationResult**:

```graphql
{
    ok: Boolean!
    id: ID
    message: String
    createdAt: String
    updatedAt: String
}
```

- Các mutation moderation (phê duyệt sự kiện, duyệt/ từ chối đăng ký, ban/unban user) trả về **ModerationResponse**:

```graphql
{
    ok: Boolean!
    action: ModerationAction!
    targetType: String!
    targetId: ID!
    status: ModerationStatus
    message: String
    moderatedAt: String
}
```

- **Authorization required**
- Anonymous user không thể thực hiện mutation

---

### 🧭 Event Mutations (`EVENT_MANAGER`)

```graphql
createEvent(input: CreateEventInput!)
editEvent(input: EditEventInput!)
deleteEvent(eventId: ID!)
approveEvent(eventId: ID!)
```

### 🧭 Post Mutations (`USER`)

```graphql
createPost(input: CreatePostInput!)
editPost(input: EditPostInput!)
deletePost(postId: ID!)
```

### 🧭 Comment Mutations (`USER`)

```graphql
createComment(input: CreateCommentInput!)
editComment(input: EditCommentInput!)
deleteComment(commentId: ID!)
```

### ❤️ Like / Unlike (`USER`)

```graphql
like(input: LikeInput!)
unlike(input: LikeInput!)
```

### 🧭 User Registration / Event Participation (`USER`)

```graphql
registerEvent(eventId: ID!)
unregisterEvent(eventId: ID!)
```

### 🧭 Admin / Event Manager Actions

```graphql
approveRegistration(registrationId: ID!)
rejectRegistration(registrationId: ID!)
banUser(userId: ID!)
unbanUser(userId: ID!)
```

---

## 🔹 Pagination & Nested Types

- `PageInfo` dùng cho query list (zero-based pagination):

```graphql
type PageInfo {
    page: Int!
    size: Int!
    totalElements: Int!
    totalPages: Int!
    hasNext: Boolean!
    hasPrevious: Boolean!
}
```

- Nested types ví dụ: `Event -> listPosts -> listComment`
- `creatorInfo` luôn trả về **UserProfileMini** (userId, username, avatarId)

---

## 🔹 Response Format

**Thành công:**

```json
{
  "data": {
    "createEvent": {
      "ok": true,
      "id": "773316679898759168",
      "message": "Success",
      "updatedAt": "2025-11-04T07:52:12.124Z"
    }
  }
}
```

**Lỗi hoặc không tìm thấy:**

```json
{
  "data": {
    "editEvent": {
      "ok": false,
      "message": "Event not found"
    }
  }
}
```