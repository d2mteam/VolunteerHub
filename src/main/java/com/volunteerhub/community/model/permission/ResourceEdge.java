package com.volunteerhub.community.model.permission;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_edges", uniqueConstraints = {
        @UniqueConstraint(name = "uq_resource_edge", columnNames = {"parent_id", "child_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEdge {
    @Id
    @Column(name = "edge_id")
    private Long edgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ResourceNode parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ResourceNode child;
}
