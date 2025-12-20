//package com.volunteerhub.configuration.security;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.hibernate.Session;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class SoftDeleteHibernateFilter extends OncePerRequestFilter {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/api/auth/");
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        Session session = entityManager.unwrap(Session.class);
//        session.enableFilter("notDeleted")
//                .setParameter("isDeleted", false);
//
//        try {
//            filterChain.doFilter(request, response);
//        } finally {
//            session.disableFilter("notDeleted");
//        }
//    }
//}