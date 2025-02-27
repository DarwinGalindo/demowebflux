package com.darwin.demowebflux_260225.repository;

import com.darwin.demowebflux_260225.model.Todo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TodoRepository extends ReactiveMongoRepository<Todo, String> {
}
