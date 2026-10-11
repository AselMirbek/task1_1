package com.example.task1_1.controller;

import com.example.task1_1.model.TaskModel;
import com.example.task1_1.service.TaskService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        model.addAttribute("taskModel", new TaskModel());
        model.addAttribute("pageTitle", "Создание задачи");
        model.addAttribute("minDate", LocalDate.now().toString());
        return "task-form";
    }

    @GetMapping("/tasks/edit/{id}")
    public String editTask(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("taskModel", taskService.findById(id));
        model.addAttribute("pageTitle", "Редактирование задачи");
        model.addAttribute("minDate", LocalDate.now().toString());
        return "task-form";
    }



    @PostMapping("/task/save")
    public String saveTask(
            @Valid @ModelAttribute("taskModel") TaskModel taskModel,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверка: срок выполнения не может быть в прошлом
        if (taskModel.getDueDate() != null
                && taskModel.getDueDate().isBefore(LocalDate.now())) {

            bindingResult.rejectValue(
                    "dueDate",
                    "PastDate",
                    "Срок выполнения не может быть раньше сегодняшней даты"
            );
        }

        // Если есть ошибки, возвращаем форму
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "pageTitle",
                    taskModel.getId() == null
                            ? "Создание задачи"
                            : "Редактирование задачи"
            );
            return "task-form";
        }

        taskService.save(taskModel);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Задача успешно сохранена"
        );

        return "redirect:/tasks";
    }
    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        taskService.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Задача удалена"
        );

        return "redirect:/tasks";
    }

    @GetMapping("/tasks/filter")
    public String filterTasks(
            @RequestParam(required = false) String completed,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            Model model
    ) {
        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            model.addAttribute(
                    "filterError",
                    "Начальная дата не может быть позже конечной"
            );
            model.addAttribute("tasks", taskService.findAll());
        } else {
            Boolean status = null;

            if ("true".equals(completed)) {
                status = true;
            } else if ("false".equals(completed)) {
                status = false;
            }

            model.addAttribute(
                    "tasks",
                    taskService.findFiltered(status, startDate, endDate)
            );
        }

        model.addAttribute("selectedCompleted", completed);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "tasks";
    }
}
