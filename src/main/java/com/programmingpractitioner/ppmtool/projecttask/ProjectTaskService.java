package com.programmingpractitioner.ppmtool.projecttask;

import com.programmingpractitioner.ppmtool.backlog.Backlog;
import com.programmingpractitioner.ppmtool.backlog.BacklogRepository;
import com.programmingpractitioner.ppmtool.project.Project;
import com.programmingpractitioner.ppmtool.project.ProjectIdException;
import com.programmingpractitioner.ppmtool.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    private final BacklogRepository backlogRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectRepository projectRepository;

    public ProjectTaskService(BacklogRepository backlogRepository, ProjectTaskRepository projectTaskRepository, ProjectRepository projectRepository) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        Backlog backlog;
        try {
            // PTs to be added to a specific project, project != null, Backlog exists
            backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            projectTask.setBacklog(backlog);

            // We want our project sequence to be like this IDPRO-1 IDPRO-2 ...
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);

            projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            // INITIAL status when status is null
            if (projectTask.getStatus() == null) {
                projectTask.setStatus(ProjectTaskStatus.TO_DO);
            }

            if (projectTask.getPriority() == null) {
                projectTask.setPriority(ProjectTaskPriority.LOW);
            }
            backlogRepository.save(backlog);
            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectIdException("Project with ID: '" + projectIdentifier + "' does not exist");
        }

    }

    public Iterable<ProjectTask> findProjectTaskFromBacklogByProjectIdentifier(String projectIdentifier) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier);
        if (project == null) throw new ProjectIdException("Project with ID: '" + projectIdentifier + "' does not exist");
        return projectTaskRepository.findProjectTaskByProjectIdentifierOrderByPriority(projectIdentifier);
    }

    public ProjectTask findProjectTaskByProjectSequence(String projectIdentifier, String projectSequence) {

        // make sure backlog/project exist
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        if (backlog == null) throw new ProjectIdException("Project with ID: '" + projectIdentifier + "' does not exist");

        return findProjectTaskByProjectSequenceHelper(projectIdentifier, projectSequence);
    }

    public ProjectTask updateProjectTaskByProjectSequence(ProjectTask updatedProjectTask, String projectIdentifier, String projectSequence) {
        ProjectTask projectTask = findProjectTaskByProjectSequenceHelper(projectIdentifier, projectSequence);
        projectTask = updatedProjectTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deleteProjectTaskByProjectSequence(String projectIdentifier, String projectSequence) {
        ProjectTask projectTask = findProjectTaskByProjectSequenceHelper(projectIdentifier, projectSequence);
        projectTaskRepository.delete(projectTask);
    }

    private ProjectTask findProjectTaskByProjectSequenceHelper(String projectIdentifier, String projectSequence){
        ProjectTask projectTask = projectTaskRepository.findProjectTaskByProjectSequence(projectSequence);
        if (projectTask == null) throw new ProjectIdException("Project task '" + projectSequence + "' not found");
        if (!projectTask.getProjectIdentifier().equals(projectIdentifier)) throw new ProjectIdException("Project task '" + projectSequence + "' does not exist in project: '" + projectIdentifier);
        return projectTask;
    }
}
