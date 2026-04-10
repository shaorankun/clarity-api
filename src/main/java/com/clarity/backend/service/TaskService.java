package com.clarity.backend.service;

import com.clarity.backend.dto.TaskRequest;
import com.clarity.backend.dto.TaskResponse;
import com.clarity.backend.model.Task;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // Helper method to convert from task to taskResponse
    private TaskResponse convertTaskToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getUser().getId(),
                task.getTitle(),
                task.getLabel(),
                task.isCompleted()
        );
    }

    // For handling error when getting with taskID (exist by taskID, task belong to user)
    private Task getTaskByIdAndUser(UUID taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new RuntimeException("Task id mismatch");
        }

        return task;
    }

    // Getting a list of tasks
    public List<TaskResponse> getTasks(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .map(this::convertTaskToResponse) // .map(task -> convertTaskToResponse(task))
                .toList();
    }

    // Create a new task
    public TaskResponse createTask(User user, TaskRequest taskRequest) {
        Task task = new Task();
        task.setUser(user);
        task.setTitle(taskRequest.getTitle());
        task.setLabel(taskRequest.getLabel());

        taskRepository.save(task);
        return convertTaskToResponse(task);
    }

    // Update a task
    public TaskResponse updateTask(UUID id,User user, TaskRequest taskRequest) {
        Task task = getTaskByIdAndUser(id,user);

        task.setTitle(taskRequest.getTitle());
        task.setLabel(taskRequest.getLabel());
        taskRepository.save(task);

        return convertTaskToResponse(task);
    }

    // Delete a task
    public void deleteTask(UUID id, User user) {
        Task task = getTaskByIdAndUser(id,user);
        taskRepository.delete(task);
    }

    // Checkmark a (in)complete task
    public TaskResponse completeTask(UUID id, User user) {
        Task task = getTaskByIdAndUser(id,user);
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);

        return convertTaskToResponse(task);
    }
}
