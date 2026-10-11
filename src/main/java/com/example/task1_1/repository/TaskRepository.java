
package com.example.task1_1.repository;

import com.example.task1_1.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByCompleted(boolean completed);

    List<Task> findAllByDueDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Task> findAllByCompletedAndDueDateBetween(
            boolean completed,
            LocalDate startDate,
            LocalDate endDate
    );
}
