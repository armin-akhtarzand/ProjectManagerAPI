package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final AuthorizationService authorizationService;

}
