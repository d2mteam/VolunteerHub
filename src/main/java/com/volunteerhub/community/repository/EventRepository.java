package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.table.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
