package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Perminov_Ilya.NauJava.DTO.UserChatDto;
import ru.Perminov_Ilya.NauJava.repository.DirectChatRepository;
import ru.Perminov_Ilya.NauJava.repository.GroupChatMemberRepository;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("common")
public class UserChatsController {

    private final UserRepository userRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;
    private final DirectChatRepository directChatRepository;

    public UserChatsController(
            UserRepository userRepository,
            GroupChatMemberRepository groupChatMemberRepository,
            DirectChatRepository directChatRepository) {
        this.userRepository = userRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
        this.directChatRepository = directChatRepository;
    }

    @GetMapping("/chats")
    public String findUserChats(@RequestParam UUID userId, Model model) {
        var answer = new ArrayList<UserChatDto>();
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new ResourceNotFoundException("Requested user is not exist");
        directChatRepository
                .findAllByUserAOrUserB(user.get())
                .forEach(directChat ->
                        answer.add(
                                new UserChatDto(
                                        directChat.getTitle(),
                                        directChat.getTag(),
                                        directChat.getType().getUserFriendlyName(),
                                        "-",
                                        directChat.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())));

        groupChatMemberRepository.findAllByUser(user.get())
                .forEach(groupChatMember ->
                        answer.add(
                                new UserChatDto(
                                        groupChatMember.getChat().getTitle(),
                                        groupChatMember.getChat().getTag(),
                                        groupChatMember.getChat().getType().getUserFriendlyName(),
                                        groupChatMember.getRole().getHumanFriendlyName(),
                                        groupChatMember.getChat().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())));

        model.addAttribute("chats", answer);
        return "userChats";
    }
}
