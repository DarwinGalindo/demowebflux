package com.darwin.demowebflux_260225.controller;

import com.darwin.demowebflux_260225.model.Todo;
import com.darwin.demowebflux_260225.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Flux<Todo> getAll() {
        return todoService.findAll();
//                .delayElements(Duration.ofSeconds(2));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Todo>> get(@PathVariable String id) {
//        return todoService.findById(id);
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(
                        Mono.just(ResponseEntity.notFound().build())
                );
    }

    @PostMapping
    public Mono<ResponseEntity<Todo>> create(@RequestBody Todo todo) {
        return todoService.create(todo)
                .map(t -> ResponseEntity
                        .created(URI.create("/api/todos/" + todo.getId()))
                        .body(t)
                );
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
//        return todoService.findById(id)
//                .flatMap(todoService::delete);

        return todoService.findById(id)
                .flatMap(todo -> todoService.delete(todo)
                        .then(Mono.just(ResponseEntity
                                .noContent()
                                .<Void>build())
                        ))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }
}
