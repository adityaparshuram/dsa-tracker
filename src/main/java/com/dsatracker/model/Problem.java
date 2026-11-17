package com.dsatracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 200)
    private String title;

    @NotBlank @Size(max = 60)
    private String topic;

    @NotNull @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @NotNull @Enumerated(EnumType.STRING)
    private Status status = Status.TODO;

    @Size(max = 40) private String platform;
    @Size(max = 500) private String link;
    @Column(columnDefinition = "TEXT") private String notes;
    private LocalDate solvedDate;
    private LocalDateTime createdAt = LocalDateTime.now();
    @com.fasterxml.jackson.annotation.JsonIgnore private Long userId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getSolvedDate() { return solvedDate; }
    public void setSolvedDate(LocalDate solvedDate) { this.solvedDate = solvedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}