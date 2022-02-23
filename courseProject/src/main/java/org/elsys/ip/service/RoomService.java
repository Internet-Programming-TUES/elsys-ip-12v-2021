package org.elsys.ip.service;

import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.error.UserAlreadyExistException;
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
import org.unbescape.properties.PropertiesKeyEscapeLevel;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public RoomDto createRoom(RoomDto roomDto) throws RoomAlreadyExistException {
        if (roomRepository.findByName(roomDto.getName()).isPresent()) {
            throw new RoomAlreadyExistException("Room with name " + roomDto.getName() + " already exists.");
        }

        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(currentUser.getUsername());

        Room room = new Room();
        room.setName(roomDto.getName());
        room.setAdmin(user);

        roomRepository.save(room);
        roomDto.setId(room.getId().toString());
        return roomDto;
    }

    public RoomDto getRoomById(String id) throws RoomNotExistException {
        Optional<Room> room = roomRepository.findById(UUID.fromString(id));
        if (room.isEmpty()) {
            throw new RoomNotExistException("There is no room with id " + id);
        }
        return convertRoom(room.get());
    }

    public List<RoomDto> getRooms() {
        return StreamSupport.stream(roomRepository.findAll().spliterator(), false).
                map(x -> convertRoom(x)).collect(Collectors.toList());
    }

    private RoomDto convertRoom(Room room) {
        RoomDto dto = new RoomDto();
        dto.setName(room.getName());
        dto.setId(room.getId().toString());
        return dto;
    }
}