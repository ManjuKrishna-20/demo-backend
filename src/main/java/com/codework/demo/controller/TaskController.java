package com.codework.demo.controller;

import com.codework.demo.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // In-memory store just for testing the deployment pipeline.
    // Swap this out for a real repository (JPA + Postgres) once
    // you're ready to wire in a database.
    private final Map<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @GetMapping
    public List<Task> getAll() {
        return List.copyOf(store.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        Task task = store.get(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task incoming) {
        long id = idGenerator.getAndIncrement();
        Task task = new Task(id, incoming.getTitle(), incoming.isCompleted());
        store.put(id, task);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task incoming) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        Task task = new Task(id, incoming.getTitle(), incoming.isCompleted());
        store.put(id, task);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (store.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
