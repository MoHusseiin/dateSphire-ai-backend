package com.dateSphire.dateSphire_ai_backend.matches;

import com.dateSphire.dateSphire_ai_backend.conversations.Conversation;
import com.dateSphire.dateSphire_ai_backend.conversations.ConversationRepository;
import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class MatchController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MatchRepository matchRepository;

    public record CreateMatchRequest(String profileId){}

    @PostMapping("/match")
    public Match createMatch(@RequestBody CreateMatchRequest matchRequest) {
        Profile profile = profileRepository.findById(matchRequest.profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Profile with id = " + matchRequest.profileId));
        // TODO : Make sure there are no existing conversations with this profile
        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                profile.id(),
                new ArrayList<>()
        );
        conversationRepository.save(conversation);
        Match match = new Match(
                UUID.randomUUID().toString(),
                profile,
                conversation.id());
        return matchRepository.save(match);
    }

    @GetMapping("/matches")
    public List<Match> getMatches() {
        return matchRepository.findAll();
    }
}
