package org.elsys.ip.service;

import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomAlreadyStartedException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.model.*;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public RoomDto createRoom(String name) throws RoomAlreadyExistException {
        if (roomRepository.findByName(name).isPresent()) {
            throw new RoomAlreadyExistException("Room with name " + name + " already exists.");
        }

        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(currentUser.getUsername());

        Room room = new Room();
        room.setName(name);
        room.setAdmin(user);
        room.getParticipants().add(user);
        roomRepository.save(room);

        return convertRoom(room);
    }

    public RoomDto addMyselfAsParticipant(String roomId) throws RoomNotExistException {
        Room room = getRoomEntityById(roomId);

        room.getParticipants().add(getMyself());

        return convertRoom(room);
    }

    public RoomDto removeMyselfAsParticipant(String roomId) throws RoomNotExistException {
        Room room = getRoomEntityById(roomId);

        room.getParticipants().remove(getMyself());

        return convertRoom(room);
    }

    public void startGame(String roomId) throws RoomNotExistException, RoomAlreadyStartedException {
        Room room = getRoomEntityById(roomId);

        if (room.getStartedTime() != null) {
            throw new RoomAlreadyStartedException("Room with id " + roomId + " is already started.");
        }

        room.setStartedTime(LocalDateTime.now());
    }

    public void answer(String roomId, String questionId, String answerId) throws RoomNotExistException {
        Room room = getRoomEntityById(roomId);
        Question question = questionRepository.findById(UUID.fromString(questionId)).get();
        Answer answer = question.getAnswers().stream().filter(a -> a.getId().equals(UUID.fromString(answerId))).findFirst().get();
        User user = getMyself();

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setQuestion(question);
        userAnswer.setAnswer(answer);
        userAnswer.setUser(user);

        Set<UserAnswer> previousAttempts = room.getUserAnswers().stream().
                filter(a -> a.getUser().getId().equals(user.getId()) && a.getQuestion().getId().equals(question.getId())).collect(Collectors.toSet());

        room.getUserAnswers().removeAll(previousAttempts);
        room.getUserAnswers().add(userAnswer);
        roomRepository.save(room);
    }

    private Room getRoomEntityById(String roomId) throws RoomNotExistException {
        Optional<Room> room = Optional.empty();
        try {
            room = roomRepository.findById(UUID.fromString(roomId));
        } catch (IllegalArgumentException ex) {
            // Do nothing
        }
        if (room.isEmpty()) {
            throw new RoomNotExistException("There is no room with roomId " + roomId);
        }

        return room.get();
    }

    public RoomDto getRoomById(String id) throws RoomNotExistException {
        return convertRoom(getRoomEntityById(id));
    }

    public List<RoomDto> getRooms() {
        return StreamSupport.stream(roomRepository.findAll().spliterator(), false).
                map(x -> convertRoom(x)).collect(Collectors.toList());
    }

    private User getMyself() {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(currentUser.getUsername());
    }

    private RoomDto convertRoom(Room room) {
        RoomDto dto = new RoomDto();
        dto.setName(room.getName());
        dto.setId(room.getId().toString());
        dto.setParticipants(
                room.getParticipants().stream().map(x -> convertUser(x, room))
                        .collect(Collectors.toList()));
        dto.setCurrentUserJoined(room.getParticipants().contains(getMyself()));
        dto.setCurrentUserAdmin(getMyself().getId().equals(room.getAdmin().getId()));
        dto.setStartedTime(room.getStartedTime());
        return dto;
    }

    private UserDto convertUser(User user, Room room) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        long score = room.getUserAnswers().stream().filter(a -> a.getUser().getId().equals(user.getId()) && a.getAnswer().isCorrect()).count();
        dto.setScore((int) score);
        return dto;
    }
}