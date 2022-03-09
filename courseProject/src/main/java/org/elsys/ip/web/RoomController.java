package org.elsys.ip.web;

import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.service.RoomService;
import org.elsys.ip.service.UserService;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    public String allRooms(WebRequest request, Model model) {
        model.addAttribute("room", new RoomDto());
        model.addAttribute("rooms",  roomService.getRooms());

        return "rooms";
    }

    @PostMapping("/rooms")
    public String createRoom(@ModelAttribute("room") @Valid RoomDto roomDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms",  roomService.getRooms());
            return "rooms";
        }

        try {
            RoomDto room = roomService.createRoom(roomDto);
            return "redirect:/room?id=" + room.getId();
        } catch (RoomAlreadyExistException e) {
            bindingResult.rejectValue("name", "room", "A room with that name already exists.");
            model.addAttribute("rooms",  roomService.getRooms());
            return "rooms";
        }
    }

    @GetMapping("/room")
    public String singleRoom(WebRequest request, Model model, @RequestParam("id") String roomId) {
        RoomDto room;
        try {
            room = roomService.getRoomById(roomId);
        } catch (RoomNotExistException e) {
            model.addAttribute("message", "There is no room with id " + roomId);
            return "error";
        }

        model.addAttribute("room", room);
        return "room";
    }
}
