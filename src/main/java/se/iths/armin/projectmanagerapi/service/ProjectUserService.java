package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;
import se.iths.armin.projectmanagerapi.repository.ProjectRepository;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;

@Service
@RequiredArgsConstructor
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;


}
