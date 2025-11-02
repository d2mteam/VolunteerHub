package com.volunteerhub.community.repository.mv;

import com.volunteerhub.community.entity.mv.UserProfileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileDetailRepository extends JpaRepository<UserProfileDetail, UUID> {
}
