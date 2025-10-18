package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
