package org.elsys.ip.web;

import org.elsys.ip.error.BaseException;
import org.elsys.ip.error.RoomAlreadyExistException;
import org.elsys.ip.error.RoomAlreadyStartedException;
import org.elsys.ip.error.RoomNotExistException;
import org.elsys.ip.service.QuestionService;
import org.elsys.ip.service.RoomService;
import org.elsys.ip.service.UserService;
import org.elsys.ip.web.model.QuestionDto;
import org.elsys.ip.web.model.RoomDto;
import org.elsys.ip.web.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private QuestionService questionService;

    @Value("${question.time}")
    private String questionTime;

    @GetMapping("/rooms")
    public String allRooms(WebRequest request, Model model) {
        model.addAttribute("room", new RoomDto());
        model.addAttribute("rooms", roomService.getRooms());

        return "rooms";
    }

    @PostMapping("/rooms")
    public String createRoom(@ModelAttribute("room") @Valid RoomDto roomDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.getRooms());
            return "rooms";
        }

        try {
            RoomDto room = roomService.createRoom(roomDto.getName());
            return "redirect:/room?id=" + room.getId();
        } catch (RoomAlreadyExistException e) {
            bindingResult.rejectValue("name", "room", "A room with that name already exists.");
            model.addAttribute("rooms", roomService.getRooms());
            return "rooms";
        }
    }

    @GetMapping("/room")
    public String singleRoom(Model model, @RequestParam("id") String roomId) {
        RoomDto room;
        try {
            room = roomService.getRoomById(roomId);
        } catch (RoomNotExistException e) {
            model.addAttribute("message", "There is no room with id " + roomId);
            return "error";
        }

        model.addAttribute("room", room);

        if (room.getStartedTime() != null) {
            // Game mode
            int questionTimeSeconds = Integer.parseInt(questionTime);
            long secondsTillStart = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - room.getStartedTime().toEpochSecond(ZoneOffset.UTC);
            int index = (int) (secondsTillStart / questionTimeSeconds);
            Optional<QuestionDto> question = questionService.getQuestionByIndex(index);
            if (question.isPresent()) {
                model.addAttribute("question", question.get());
                return "question";
            }

            return "summary";
        }


        return "room";
    }

    @PostMapping("/room")
    public String joinRoom(Model model, @RequestParam boolean join, @RequestParam("id") String roomId) {
        try {
            if (join) {
                roomService.addMyselfAsParticipant(roomId);
            } else {
                roomService.removeMyselfAsParticipant(roomId);
            }
        } catch (RoomNotExistException e) {
            model.addAttribute("message", "There is no room with id " + roomId);
            return "error";
        }
        return "redirect:/room?id=" + roomId;
    }

    @PostMapping("/room/start")
    public String startGame(Model model, @RequestParam("id") String roomId) {
        try {
            roomService.startGame(roomId);
        } catch (BaseException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "redirect:/room?id=" + roomId;
    }

    @PostMapping("/room/answer")
    public String startGame(Model model, @RequestParam("roomId") String roomId, @RequestParam("questionId") String questionId, @RequestParam("answerId") String answerId) {
        try {
            roomService.answer(roomId, questionId, answerId);
        } catch (BaseException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "redirect:/room?id=" + roomId;
    }
}
