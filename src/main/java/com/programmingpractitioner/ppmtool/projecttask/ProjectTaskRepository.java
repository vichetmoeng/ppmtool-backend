package com.programmingpractitioner.ppmtool.projecttask;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {
    List<ProjectTask> findProjectTaskByProjectIdentifierOrderByPriority(String projectIdentifier);

    ProjectTask findProjectTaskByProjectSequence(String projectSequence);
}
