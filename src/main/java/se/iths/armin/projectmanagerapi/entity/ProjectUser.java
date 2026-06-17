package se.iths.armin.projectmanagerapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_user")
@Getter
@Setter
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_user_id")
    private Long projectUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole = ProjectRole.PROJECT_MEMBER;
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}