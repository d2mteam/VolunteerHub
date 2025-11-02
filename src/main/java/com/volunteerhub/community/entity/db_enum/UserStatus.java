package com.volunteerhub.community.entity.db_enum;

public enum UserStatus {
    PENDING,           // vừa đăng ký, chưa xác nhận
    ACTIVE,            // đang hoạt động bình thường
    INACTIVE,          // không hoạt động, nhưng có thể kích hoạt lại
    SUSPENDED,         // tạm khóa do vi phạm
    BANNED,            // bị cấm vĩnh viễn
    DEACTIVATED,       // user hủy kích hoạt
    LOCKED,            // khóa tạm thời (đăng nhập sai)
    ARCHIVED           // tài khoản lưu trữ, không còn dùng
}
