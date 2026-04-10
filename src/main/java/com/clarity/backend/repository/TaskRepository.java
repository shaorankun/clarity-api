package com.clarity.backend.repository;

import com.clarity.backend.model.Task;
import com.clarity.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByUser(User user);

}
