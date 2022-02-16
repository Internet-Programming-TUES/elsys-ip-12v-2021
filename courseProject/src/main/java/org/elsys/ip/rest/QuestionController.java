package org.elsys.ip.rest;

import org.elsys.ip.model.Question;
import org.elsys.ip.model.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {
    private final QuestionRepository repository;

    public QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Question getQuestion(String questionId) {
        Optional<Question> question = repository.findById(UUID.fromString(questionId));
        if (question.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return question.get();
    }
}
