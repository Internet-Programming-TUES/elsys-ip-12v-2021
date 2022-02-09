package org.elsys.ip.rest;

import org.elsys.ip.model.Answer;
import org.elsys.ip.model.Question;
import org.elsys.ip.model.QuestionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestionControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuestionRepository repository;

    private String questionId;

    @Test
    public void getSingleQuestion() throws Exception {
        Question question = this.restTemplate.getForObject("http://localhost:" + port + "/?questionId=" + questionId,
                Question.class);

        assertThat(question.getText()).isEqualTo("Kolko e 2+2?");
    }

    @Test
    public void getNotExistingQuestion() throws Exception {
        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + port + "/?questionId=c1792663-84c4-4b38-aa78-f1a185836177", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @BeforeAll
    public void setUp() {
        Question question = new Question();
        question.setText("Kolko e 2+2?");
        Answer answer1 = new Answer();
        answer1.setText("1");
        Answer answer2 = new Answer();
        answer2.setText("2");
        Answer answer3 = new Answer();
        answer3.setText("3");
        Answer answer4 = new Answer();
        answer4.setText("4");
        answer4.setCorrect(true);
        question.setAnswers(List.of(answer1, answer2, answer3, answer4));

        repository.save(question);

        questionId = question.getId().toString();
    }
}