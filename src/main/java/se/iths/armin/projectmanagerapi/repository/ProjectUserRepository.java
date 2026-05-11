package se.iths.armin.projectmanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.ProjectUser;

import java.util.List;

import java.util.Optional;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {


    boolean existsByAppUserAndProject(AppUser appUser, Project project);

    Optional<ProjectUser> findByAppUserAndProject(AppUser appUser, Project project);

    List<ProjectUser> findAllByProject(Project project);

    List<ProjectUser> findAllByAppUser(AppUser appUser);
}
