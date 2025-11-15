package com.volunteerhub.community.dto.rest.request;

import com.volunteerhub.community.entity.db_enum.SystemRole;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    private SystemRole newSystemRole;
}
