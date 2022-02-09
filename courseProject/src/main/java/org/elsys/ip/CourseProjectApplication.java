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

import static org.hibernate.cache.spi.support.SimpleTimestamper.next;

@SpringBootApplication
public class CourseProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseProjectApplication.class, args);
    }
}
