package com.volunteerhub.community.entity;

import com.volunteerhub.community.entity.db_enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Registration extends BaseEntity {
    @Id
    @Column(name = "registration_id")
    private Long registrationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", updatable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_info", columnDefinition = "jsonb")
    private Map<String, Object> extraInfo;
}
