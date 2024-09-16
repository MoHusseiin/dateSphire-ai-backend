package com.dateSphire.dateSphire_ai_backend.conversations;

import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final ProfileRepository profileRepository;
    private final ConversationService conversationService;

    public ConversationController(ConversationRepository conversationRepository, ProfileRepository profileRepository, ConversationService conversationService) {
        this.conversationRepository = conversationRepository;
        this.profileRepository = profileRepository;
        this.conversationService = conversationService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/conversations")
    public Conversation createConversation(@RequestBody CreateConversationRequest request) {
        Profile profile = profileRepository.findById(request.profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Profile with id = " + request.profileId));
        Conversation  conversation = new Conversation(
                UUID.randomUUID().toString(),
                profile.id(),
                new ArrayList<>()
        );
        return conversationRepository.save(conversation);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/conversations/{conversationId}")
    public Conversation createConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Conversation with id = " +conversationId));
        Profile profile = profileRepository.findById(conversation.profileId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Profile with id = " + chatMessage.authorId()));

        ChatMessage message = new ChatMessage(
                chatMessage.messageText(),
                chatMessage.authorId(),
                LocalDateTime.now()
        );
        conversation.messages().add(message);
        Profile me =  profileRepository.findById(chatMessage.authorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Profile with id = user"));
        conversationService.generateProfileResponse(conversation, profile, me);
        return conversationRepository.save(conversation);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversationById(@PathVariable String conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find Conversation with id = " +conversationId));
    }

    public record CreateConversationRequest (String profileId){}
}