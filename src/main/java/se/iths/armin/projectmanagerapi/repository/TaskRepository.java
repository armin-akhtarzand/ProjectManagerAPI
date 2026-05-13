package se.iths.armin.projectmanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProject(Project project);
}
