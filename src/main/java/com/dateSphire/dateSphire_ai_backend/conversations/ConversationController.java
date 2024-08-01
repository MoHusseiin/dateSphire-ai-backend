package com.dateSphire.dateSphire_ai_backend.conversations;

import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final ProfileRepository profileRepository;

    public ConversationController(ConversationRepository conversationRepository, ProfileRepository profileRepository) {
        this.conversationRepository = conversationRepository;
        this.profileRepository = profileRepository;
    }

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

    @PostMapping("/conversations/{conversationId}")
    public Conversation createConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Conversation with id = " +conversationId));
        profileRepository.findById(chatMessage.authorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Profile with id = " + chatMessage.authorId()));

        ChatMessage message = new ChatMessage(
                chatMessage.messageText(),
                chatMessage.authorId(),
                LocalDateTime.now()
        );
        conversation.messages().add(message);
        return conversationRepository.save(conversation);
    }

    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversationById(@PathVariable String conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find Conversation with id = " +conversationId));
    }

    public record CreateConversationRequest (String profileId){}
}