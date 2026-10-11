
package com.example.task1_1.service;

import com.example.task1_1.entity.Task;
import com.example.task1_1.model.TaskModel;
import com.example.task1_1.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskModel> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskModel findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Задача с ID " + id + " не найдена"
                        ));

        return toModel(task);
    }

    public TaskModel save(TaskModel model) {
        Task task;

        if (model.getId() != null) {
            task = taskRepository.findById(model.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Задача не найдена"
                            ));
        } else {
            task = new Task();
        }

        task.setTitle(model.getTitle().trim());
        task.setDescription(model.getDescription());
        task.setCompleted(model.isCompleted());
        task.setDueDate(model.getDueDate());

        Task saved = taskRepository.save(task);
        return toModel(saved);
    }

    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Задача с ID " + id + " не найдена"
            );
        }

        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskModel> findByCompleted(boolean completed) {
        return taskRepository.findAllByCompleted(completed)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskModel> findFiltered(
            Boolean completed,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Task> tasks;

        if (startDate != null && endDate != null) {
            if (completed != null) {
                tasks = taskRepository
                        .findAllByCompletedAndDueDateBetween(
                                completed, startDate, endDate
                        );
            } else {
                tasks = taskRepository
                        .findAllByDueDateBetween(startDate, endDate);
            }
        } else if (completed != null) {
            tasks = taskRepository.findAllByCompleted(completed);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private TaskModel toModel(Task task) {
        TaskModel model = new TaskModel();

        model.setId(task.getId());
        model.setTitle(task.getTitle());
        model.setDescription(task.getDescription());
        model.setCompleted(task.isCompleted());
        model.setDueDate(task.getDueDate());

        return model;
    }
}
