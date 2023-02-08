package com.programmingpractitioner.ppmtool.backlog;

import com.programmingpractitioner.ppmtool.projecttask.ProjectTask;
import com.programmingpractitioner.ppmtool.projecttask.ProjectTaskService;
import com.programmingpractitioner.ppmtool.util.MapValidationErrorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {
    private final ProjectTaskService projectTaskService;
    private final MapValidationErrorService mapValidationErrorService;

    public BacklogController(ProjectTaskService projectTaskService, MapValidationErrorService mapValidationErrorService) {
        this.projectTaskService = projectTaskService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{projectIdentifier}")
    public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result, @PathVariable String projectIdentifier) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mavValidationService(result);
        if (errorMap != null) return errorMap;

        return new ResponseEntity<>(projectTaskService.addProjectTask(projectIdentifier, projectTask), HttpStatus.CREATED);
    }

    @GetMapping("/{projectIdentifier}")
    public Iterable<ProjectTask> getProjectTasksFromBacklog(@PathVariable String projectIdentifier) {
        return projectTaskService.findProjectTaskFromBacklogByProjectIdentifier(projectIdentifier);
    }

    @GetMapping("/{projectIdentifier}/{projectSequence}")
    public ResponseEntity<?> getProjectTask(@PathVariable String projectIdentifier, @PathVariable String projectSequence) {
        return new ResponseEntity<>(projectTaskService.findProjectTaskByProjectSequence(projectIdentifier, projectSequence), HttpStatus.OK);
    }

    @PatchMapping("/{projectIdentifier}/{projectSequence}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result, @PathVariable String projectIdentifier, @PathVariable String projectSequence) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mavValidationService(result);
        if (errorMap != null) return errorMap;
        return new ResponseEntity<>(projectTaskService.updateProjectTaskByProjectSequence(projectTask, projectIdentifier, projectSequence), HttpStatus.OK);
    }

    @DeleteMapping("/{projectIdentifier}/{projectSequence}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String projectIdentifier, @PathVariable String projectSequence) {
        projectTaskService.deleteProjectTaskByProjectSequence(projectIdentifier, projectSequence);
        return new ResponseEntity<>("Project task '" + projectSequence + "' was deleted successfully", HttpStatus.OK);
    }
}
