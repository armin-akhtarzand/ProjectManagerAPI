package se.iths.armin.projectmanagerapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;


}
