package org.elsys.ip.service;

import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomAlreadyStartedException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.model.*;
import org.elsys.ip.web.model.AnswerDto;
import org.elsys.ip.web.model.QuestionDto;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public Optional<QuestionDto> getQuestionByIndex(int index) {
        Optional<Question> question = StreamSupport.stream(questionRepository.findAll().spliterator(), false).skip(index).findFirst();

        return question.map(this::convert);
    }

    private QuestionDto convert(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId().toString());
        dto.setText(question.getText());
        dto.setAnswers(question.getAnswers().stream().map(this::convert).collect(Collectors.toList()));
        return dto;
    }

    private AnswerDto convert(Answer answer) {
        AnswerDto dto = new AnswerDto();
        dto.setId(answer.getId().toString());
        dto.setText(answer.getText());
        return dto;
    }
}