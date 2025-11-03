package com.volunteerhub.schedule;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@AllArgsConstructor
public class DemoRefresh {

    private final JdbcTemplate jdbcTemplate;

    public void refreshEventDetail() {
        jdbcTemplate.execute("SELECT refresh_event_detail()");
    }

    public void refreshPostDetail() {
        jdbcTemplate.execute("SELECT refresh_post_detail()");
    }

    public void refreshCommentDetail() {
        jdbcTemplate.execute("SELECT refresh_comment_detail()");
    }

    public void refreshUserProfileDetail() {
        jdbcTemplate.execute("SELECT refresh_user_profile_detail()");
    }


    @Scheduled(cron = "0 * * * * *")
    public void updateViews() {
        System.out.println("Running job at: " + LocalDateTime.now());
        refreshEventDetail();
        refreshPostDetail();
        refreshCommentDetail();
        refreshUserProfileDetail();
    }
}
