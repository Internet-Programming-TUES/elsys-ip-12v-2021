package org.elsys.ip.service;

import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.model.Room;
import org.elsys.ip.model.RoomRepository;
import org.elsys.ip.model.User;
import org.elsys.ip.model.UserRepository;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
                room.getParticipants().stream().map(x -> convertUser(x))
                        .collect(Collectors.toList()));
        dto.setCurrentUserJoined(room.getParticipants().contains(getMyself()));
        return dto;
    }

    private UserDto convertUser(User user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}