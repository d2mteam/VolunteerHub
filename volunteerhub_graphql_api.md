# 📘 VolunteerHub API Guide — REST & GraphQL (Read-only)

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

> Tách toàn bộ thao tác **create / update / delete** khỏi GraphQL; mọi ghi chép dùng REST.

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
  "result": "SUCCESS",
  "action": "BAN_USER",
  "targetType": "USER",
  "targetId": "c5b05670-5f6d-4e5b-9d82-5c34a8b9bf9b",
  "status": "BANNED",
  "message": "User c5b05670-5f6d-4e5b-9d82-5c34a8b9bf9b has been banned",
  "reasonCode": null,
  "moderatedAt": "2025-11-04T07:52:12.124Z"
}
```

---

## 🔹 GraphQL API

**Base URL:**

```
GRAPHQL http://localhost:8080/graphql
Authorization: Bearer <accessToken>  # Optional for queries
```

- `UserId` sử dụng **UUID**
- Các `ID` khác (Post, Comment, Event) là **Snowflake ID dạng string**
- **Anonymous user**: chỉ query được; mọi thao tác ghi đã chuyển sang REST

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

> Lưu ý: GraphQL **chỉ hỗ trợ đọc**; mọi mutation trước đây đã bị gỡ bỏ. Ghi sử dụng REST ở phần trên.

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

Ví dụ GraphQL query thành công:

```json
{
  "data": {
    "getPost": {
      "postId": "1",
      "content": "...",
      "commentCount": 3
    }
  }
}
```