package org.elsys.ip;

import org.elsys.ip.model.Answer;
import org.elsys.ip.model.Question;
import org.elsys.ip.model.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.elsys.ip.model.Answer.correct;
import static org.elsys.ip.model.Answer.wrong;
import static org.elsys.ip.model.Question.question;
import static org.hibernate.cache.spi.support.SimpleTimestamper.next;

@SpringBootApplication
public class CourseProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseProjectApplication.class, args);
    }

    @Autowired
    private QuestionRepository questionRepository;

    @PostConstruct
    private void post() {
        if (!questionRepository.findAll().iterator().hasNext()) {
            questionRepository.save(question("Колко е 2+2", wrong("1"), wrong("3"), correct("4"), wrong("5")));
            questionRepository.save(question("Кой е най-якия клас?", wrong("12а"), wrong("12б"), correct("12в"), wrong("12ц")));
            questionRepository.save(question("Ще завърша ли по ИП?", correct("да"), wrong("не")));
        }
    }
}
