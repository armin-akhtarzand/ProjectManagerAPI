package se.iths.armin.projectmanagerapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "project")
    private List<ProjectUserEntity> projectUsers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "project_status")
    private ProjectStatus projectStatus = ProjectStatus.PLANNED;

    @Column(name = "project_created", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "project_updated")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
