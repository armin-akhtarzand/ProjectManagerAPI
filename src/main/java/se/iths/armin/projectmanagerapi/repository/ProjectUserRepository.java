package se.iths.armin.projectmanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.armin.projectmanagerapi.entity.ProjectUserEntity;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, Long> {
}
