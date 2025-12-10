package com.volunteerhub.community.repository.permission;

import com.volunteerhub.community.model.permission.Permission;
import com.volunteerhub.community.model.permission.PermissionGrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PermissionGrantRepository extends JpaRepository<PermissionGrant, Long> {

    @Query(value = """
            WITH RECURSIVE ancestors AS (
                SELECT rn.resource_id
                FROM resource_nodes rn
                WHERE rn.resource_id = :resourceId
                UNION ALL
                SELECT e.parent_id
                FROM resource_edges e
                JOIN ancestors a ON a.resource_id = e.child_id
            )
            SELECT DISTINCT pg.permission
            FROM ancestors a
            JOIN permission_grants pg ON pg.resource_id = a.resource_id
            WHERE pg.active = true
              AND (
                    (pg.subject_type = 'USER' AND pg.subject_id = CAST(:userId AS VARCHAR))
                 OR (pg.subject_type = 'ROLE' AND pg.subject_id IN (:roles))
              )
            """, nativeQuery = true)
    List<String> findEffectivePermissions(@Param("resourceId") Long resourceId,
                                          @Param("userId") String userId,
                                          @Param("roles") Collection<String> roles);

    List<PermissionGrant> findByResource_ResourceId(Long resourceId);
}
