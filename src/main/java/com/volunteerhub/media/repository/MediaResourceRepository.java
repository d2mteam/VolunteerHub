package com.volunteerhub.media.repository;

import com.volunteerhub.media.model.MediaResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaResourceRepository extends JpaRepository<MediaResource, UUID> {
}
