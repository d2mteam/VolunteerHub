//package com.volunteerhub.community.service.read_service;
//
//import com.volunteerhub.community.dto.graphql.output.EventDto;
//import com.volunteerhub.community.repository.mv.EventReadRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@AllArgsConstructor
//@Transactional(readOnly = true)
//public class EventReadService {
//    private final EventReadRepository repo;
//
//    public Optional<EventDto> getEventById(Long eventId) {
//        return repo.findById(eventId);
//    }
//}
