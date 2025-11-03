# 📘 VolunteerHub GraphQL API — Read & Write Layer

**Base URL:**  
```
GRAPHQL http://localhost:8080/graphql
```

---

## 🔹 Overview

Hệ thống cung cấp schema GraphQL cho cả *đọc* (read model CQRS) và *ghi* (mutations CRUD).  
Tất cả thao tác được gửi qua **một endpoint duy nhất** bằng phương thức `POST`.

---

## 🔸 Query Examples (Read)

### 🧱 1. Lấy chi tiết **Post** cùng danh sách Comment

```graphql
query {
  getPost(postId: 1) {
    postId
    eventId
    content
    createdAt
    updatedAt
    creatorId
    creatorUsername
    creatorFullName
    creatorAvatar
    commentCount
    likeCount

    listComment(page: 0, size: 10) {
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
        creatorId
        creatorFullName
        creatorUsername
        creatorAvatar
        commentCount
        likeCount
      }
    }
  }
}
```

---

### 🧱 2. Lấy chi tiết **Event** cùng danh sách Post + Comment lồng nhau

```graphql
query {
  getEvent(eventId: 1) {
    eventId
    eventName
    eventDescription
    eventLocation
    createdAt
    updatedAt
    creatorId
    creatorFullName
    creatorUsername
    creatorAvatar
    memberCount
    postCount
    likeCount

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
        creatorId
        creatorUsername
        creatorFullName
        creatorAvatar
        commentCount
        likeCount

        listComment(page: 0, size: 10) {
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
            creatorId
            creatorFullName
            creatorUsername
            creatorAvatar
            commentCount
            likeCount
          }
        }
      }
    }
  }
}
```

---

### 🧱 3. Lấy chi tiết **UserProfile** cùng các Event tham gia

```graphql
query {
  getUserProfile(userId: "d4e5f6a7-b8c9-0123-def0-4567890123cd") {
    userId
    username
    fullName
    avatarUrl
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
        creatorId
        creatorFullName
        creatorUsername
        creatorAvatar
        memberCount
        postCount
        likeCount
      }
    }
  }
}
```

---

## 🔸 Mutation Examples (Write)

Mỗi mutation trả về `MutationResult`:

```graphql
{
  ok
  id
  message
  updatedAt
}
```

---

### 🧭 Event

#### ➕ Tạo Event
```graphql
mutation {
  createEvent(
    input: {
      eventName: "Dọn rác ven hồ"
      eventDescription: "Chiến dịch dọn rác khu vực hồ Tây"
      eventLocation: "Hồ Tây, Hà Nội"
      eventDate: "2025-12-01"
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ✏️ Sửa Event
```graphql
mutation {
  editEvent(
    input: {
      eventId: "773316679898759168"
      eventName: "Dọn rác ven hồ (tuần 2)"
      eventDescription: "Bổ sung thêm hoạt động trồng cây"
      eventLocation: "Hồ Tây khu Nhật Tân"
      eventDate: "2025-12-08"
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ❌ Xoá Event
```graphql
mutation {
  deleteEvent(eventId: "773316679898759168") {
    ok
    id
    message
    updatedAt
  }
}
```

---

### 🧭 Post

#### ➕ Tạo Post
```graphql
mutation {
  createPost(
    input: {
      eventId: "1"
      content: "Ai đi được sáng chủ nhật thì confirm giúp nhé!"
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ✏️ Sửa Post
```graphql
mutation {
  editPost(
    input: {
      postId: "773317579212062720"
      content: "Update: tập trung 7h tại bãi đỗ xe số 2."
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ❌ Xoá Post
```graphql
mutation {
  deletePost(postId: "773317579212062720") {
    ok
    id
    message
    updatedAt
  }
}
```

---

### 🧭 Comment

#### ➕ Tạo Comment
```graphql
mutation {
  createComment(
    input: {
      postId: "773317579212062720"
      content: "Tôi sẽ mang bao tay và nước uống."
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ✏️ Sửa Comment
```graphql
mutation {
  editComment(
    input: {
      commentId: "773318226313478144"
      content: "Mang thêm vài túi rác to nữa nhé."
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### ❌ Xoá Comment
```graphql
mutation {
  deleteComment(commentId: "773318226313478144") {
    ok
    id
    message
    updatedAt
  }
}
```

---

### ❤️ Like / Unlike

#### Like
```graphql
mutation {
  like(
    input: {
      targetType: "POST"
      targetId: "1"
    }
  ) {
    ok
    id
    message
    updatedAt
  }
}
```

#### Unlike
```graphql
mutation {
  unlike(targetId: "773322338421702656") {
    ok
    id
    message
    updatedAt
  }
}
```

---

## 🔹 Response Format

Thành công:
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

Lỗi hoặc không tìm thấy:
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

---

## 🔹 Pagination Response

```json
{
  "pageInfo": {
    "page": 0,
    "size": 10,
    "totalElements": 24,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  },
  "content": [
    { "postId": "1", "content": "..." }
  ]
}
```

---

## 🔹 Notes

- `page` bắt đầu từ **0** (zero-based pagination).  
- `ok = false` → nên hiển thị `message` cho người dùng.  
- `id` luôn trả về dạng **string** (Snowflake hoặc UUID).  
- Sau khi mutation thành công, frontend nên `refetch` query tương ứng (`getPost`, `getEvent`, v.v.).  
- Các truy vấn con như `listPosts`, `listComments` hỗ trợ phân trang và nested fetch.
