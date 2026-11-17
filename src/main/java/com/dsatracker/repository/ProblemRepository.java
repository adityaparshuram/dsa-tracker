package com.dsatracker.repository;

import com.dsatracker.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    @Query("""
        SELECT p FROM Problem p
        WHERE p.userId = :uid
          AND (:topic IS NULL OR p.topic = :topic)
          AND (:difficulty IS NULL OR p.difficulty = :difficulty)
          AND (:status IS NULL OR p.status = :status)
          AND (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
        ORDER BY p.id DESC
        """)
    List<Problem> search(@Param("uid") Long uid, @Param("topic") String topic, @Param("difficulty") Difficulty difficulty,
                         @Param("status") Status status, @Param("q") String q);

    List<Problem> findByUserId(Long userId);
    Optional<Problem> findByIdAndUserId(Long id, Long userId);
}