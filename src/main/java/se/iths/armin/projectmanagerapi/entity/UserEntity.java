package se.iths.armin.projectmanagerapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userid;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_position")
    private UserPosition userPosition = UserPosition.EMPLOYEE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_status")
    private UserStatus userStatus = UserStatus.ACTIVE;
    @Enumerated(EnumType.STRING)

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    List<ProjectUserEntity> projectUsers;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
