package org.elsys.ip.service;

import org.assertj.core.util.Lists;
import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.error.UserAlreadyExistException;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Repeat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class RoomServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    private UserDto myself;
    private UserDto anotherUser;
    private UserDetails userDetailsMock;

    @Test
    public void createAndRead() throws RoomNotExistException, RoomAlreadyExistException {
        assertThat(roomService.getRooms()).hasSize(0);
        RoomDto createdRoom = roomService.createRoom("New room");

        assertThat(createdRoom.getName()).isEqualTo("New room");
        assertThat(createdRoom.getId()).isNotEmpty();
        assertThat(createdRoom.getParticipants()).hasSize(1);
        assertThat(createdRoom.getParticipants().get(0)).isEqualTo(myself);

        RoomDto roomById = roomService.getRoomById(createdRoom.getId());
        assertThat(roomById).isEqualTo(createdRoom);
    }

    @Test
    public void createMoreAndRead() throws RoomNotExistException, RoomAlreadyExistException {
        RoomDto createdRoom1 = roomService.createRoom("New room");
        RoomDto createdRoom2 = roomService.createRoom("Second room");

        assertThat(createdRoom1.getId()).isNotEqualTo(createdRoom2.getId());
        assertThat(createdRoom2.getParticipants()).hasSize(1);
        assertThat(createdRoom2.getParticipants().get(0)).isEqualTo(myself);

        List<RoomDto> rooms = roomService.getRooms();
        assertThat(rooms).hasSize(2);
        assertThat(rooms.stream().collect(Collectors.toSet())
                .containsAll(Lists.list(createdRoom1, createdRoom2))).isTrue();
    }

    @Test
    public void alreadyExist() throws RoomNotExistException, RoomAlreadyExistException {
        roomService.createRoom("New room");

        assertThatThrownBy(() -> roomService.createRoom("New room"))
                .isInstanceOf(RoomAlreadyExistException.class);

        List<RoomDto> rooms = roomService.getRooms();
        assertThat(rooms).hasSize(1);
    }

    @Test
    public void invalidId() {
        assertThatThrownBy(() -> roomService.getRoomById("invalid"))
                .isInstanceOf(RoomNotExistException.class);
    }

    @Test
    public void joinRoomSingleUser() throws RoomAlreadyExistException, RoomNotExistException {
        RoomDto newRoom = roomService.createRoom("New room");

        RoomDto updatedRoom = roomService.addMyselfAsParticipant(newRoom.getId());
        assertThat(updatedRoom.getParticipants()).hasSize(1);
        assertThat(updatedRoom.getParticipants().get(0)).isEqualTo(myself);

        RoomDto roomById = roomService.getRoomById(newRoom.getId());
        assertThat(roomById).isEqualTo(newRoom);

        updatedRoom = roomService.removeMyselfAsParticipant(newRoom.getId());
        assertThat(updatedRoom.getParticipants()).hasSize(0);

        roomById = roomService.getRoomById(newRoom.getId());
        assertThat(roomById).isEqualTo(newRoom);
    }

    @Test
    public void joinRoomTwoUsers() throws RoomAlreadyExistException, RoomNotExistException {
        RoomDto newRoom = roomService.createRoom("New room");

        // Login as another user
        Mockito.when(userDetailsMock.getUsername()).thenReturn("another@email.com");
        RoomDto updatedRoom = roomService.addMyselfAsParticipant(newRoom.getId());

        assertThat(updatedRoom.getParticipants()).hasSize(2);
        assertThat(updatedRoom.getParticipants().containsAll(Arrays.asList(myself, anotherUser))).isTrue();

        RoomDto roomById = roomService.getRoomById(newRoom.getId());
        assertThat(roomById).isEqualTo(updatedRoom);
    }

    @BeforeEach
    public void setUp() throws UserAlreadyExistException {
        myself = new UserDto();
        myself.setFirstName("Test");
        myself.setLastName("User");
        myself.setEmail("email@email.com");
        myself.setPassword("password");
        userService.registerNewUserAccount(myself);

        anotherUser = new UserDto();
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser.setEmail("another@email.com");
        anotherUser.setPassword("password");
        userService.registerNewUserAccount(anotherUser);

        userDetailsMock = Mockito.mock(UserDetails.class);
        Mockito.when(userDetailsMock.getUsername()).thenReturn("email@email.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetailsMock);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
