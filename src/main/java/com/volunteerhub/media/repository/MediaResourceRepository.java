package com.volunteerhub.media.repository;

import com.volunteerhub.media.model.MediaResource;
import com.volunteerhub.media.model.MediaRefType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface MediaResourceRepository extends JpaRepository<MediaResource, UUID> {
    List<MediaResource> findByRefTypeAndRefId(MediaRefType refType, UUID refId);
}
