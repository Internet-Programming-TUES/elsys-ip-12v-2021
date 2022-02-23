package org.elsys.ip.service;

import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.error.UserAlreadyExistException;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
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

    @Test
    public void createAndRead() throws RoomNotExistException, RoomAlreadyExistException {
        assertThat(roomService.getRooms()).hasSize(0);
        RoomDto newRoom = new RoomDto();
        newRoom.setName("New room");
        RoomDto createdRoom = roomService.createRoom(newRoom);

        assertThat(createdRoom.getName()).isEqualTo("New room");
        assertThat(createdRoom.getId()).isNotEmpty();
        assertThat(createdRoom).isSameAs(newRoom);

        RoomDto roomById = roomService.getRoomById(createdRoom.getId());
        assertThat(roomById).isEqualTo(createdRoom);
    }

    @Test
    public void createMoreAndRead() throws RoomNotExistException, RoomAlreadyExistException {
        RoomDto newRoom = new RoomDto();
        newRoom.setName("New room");
        RoomDto createdRoom1 = roomService.createRoom(newRoom);

        RoomDto secondRoom = new RoomDto();
        secondRoom.setName("Second room");
        RoomDto createdRoom2 = roomService.createRoom(secondRoom);

        assertThat(createdRoom1.getId()).isNotEqualTo(createdRoom2.getId());
        List<RoomDto> rooms = roomService.getRooms();
        assertThat(rooms).hasSize(2);
        assertThat(rooms.stream().collect(Collectors.toSet())
                .containsAll(Lists.list(createdRoom1, createdRoom2))).isTrue();
    }

    @Test
    public void alreadyExist() throws RoomNotExistException, RoomAlreadyExistException {
        RoomDto newRoom = new RoomDto();
        newRoom.setName("New room");
        roomService.createRoom(newRoom);

        assertThatThrownBy(() -> roomService.createRoom(newRoom))
                .isInstanceOf(RoomAlreadyExistException.class);

        List<RoomDto> rooms = roomService.getRooms();
        assertThat(rooms).hasSize(1);
    }

    @BeforeEach
    public void setUp() throws UserAlreadyExistException {
        UserDto user = new UserDto();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("email@email.com");
        user.setPassword("password");
        userService.registerNewUserAccount(user);

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("email@email.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
