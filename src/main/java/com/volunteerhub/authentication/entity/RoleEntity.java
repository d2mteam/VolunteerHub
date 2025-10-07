package com.volunteerhub.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;  //ADMIN //USER
}
