package com.darwin.demowebflux_260225;

import com.darwin.demowebflux_260225.model.Todo;
import com.darwin.demowebflux_260225.repository.TodoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoTestController {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void getAll() {
        webTestClient.get()
                .uri("/api/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Todo.class)
                .consumeWith(result -> {
                    var todos = result.getResponseBody();

                    assertThat(todos).isNotEmpty();
                    assertThat(todos.get(0).getId()).isNotNull();
                });
    }

    @Test
    void get() {
        Todo todo = createSampleTodo();

        webTestClient.get()
                .uri("/api/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(todo.getName())
                .jsonPath("$.completed").isEqualTo(todo.isCompleted())
                .jsonPath("$.id").isEqualTo(todo.getId());
    }

    @Test
    void getNotFound() {
        webTestClient.get()
                .uri("/api/todos/{id}", "id que no existe")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void create() {
        Todo todo = new Todo();
        todo.setName("prueba create");
        todo.setCompleted(false);

        webTestClient.post()
                .uri("/api/todos")
                .bodyValue(todo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(todo.getName())
                .jsonPath("$.completed").isEqualTo(todo.isCompleted());
    }

    @Test
    void delete() {
        Todo todo = createSampleTodo();

        webTestClient.delete()
                .uri("/api/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void deleteNotFound() {
        webTestClient.delete()
                .uri("/api/todos/{id}", "id que no existe")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    private Todo createSampleTodo() {
        Todo todo = new Todo();
        todo.setName("repasar la clase");
        todo.setCompleted(false);

        return todoRepository.save(todo).block();
    }

}
