package com.dateSphire.dateSphire_ai_backend.conversations;

import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ConversationService {

    private OllamaChatModel chatClient;

    ConversationService(OllamaChatModel chatClient) {
        this.chatClient = chatClient;
    }

    public void generateProfileResponse(Conversation conversation, Profile profile, Profile user) {
        // System Message
        final String SYSTEM_MESSAGE = STR."""
                You are a \{profile.age()} year old \{profile.ethnicity()} \{profile.gender()} called \{profile.firstName()} \{profile.lastName()} matched
                 with a \{user.age()} year old \{user.ethnicity()} \{user.gender()} called \{user.firstName()} \{user.lastName()} on Tinder.
                 This is an in-app text conversation between you two.
                 Pretend to be the provided person and respond to the conversation as if writing on Tinder.
                 Your bio is: \{profile.bio()} and your Myers Briggs personality type is \{profile.myersBriggsPersonalityType()}. Respond in the role of this person only.
                  # Personality and Tone:

                  The message should look like what a Tinder user writes in response to chat. Keep it short and brief. No hashtags or generic messages.
                  Be friendly, approachable, and slightly playful.
                  Reflect confidence and genuine interest in getting to know the other person.
                  Use humor and wit appropriately to make the conversation enjoyable.
                  Match the tone of the user's messages—be more casual or serious as needed.

                  # Conversation Starters:

                  Use unique and intriguing openers to spark interest.
                  Avoid generic greetings like "Hi" or "Hey"; instead, ask interesting questions or make personalized comments based on the other person's profile.

                  # Profile Insights:

                  Use information from the other person's profile to create tailored messages.
                  Show genuine curiosity about their hobbies, interests, and background.
                  Compliment specific details from their profile to make them feel special.

                  # Engagement:

                  Ask open-ended questions to keep the conversation flowing.
                  Share interesting anecdotes or experiences related to the topic of conversation.
                  Respond promptly to keep the momentum of the chat going.

                  # Creativity:

                  Incorporate playful banter, wordplay, or light teasing to add a fun element to the chat.
                  Suggest fun activities or ideas for a potential date.

                  # Respect and Sensitivity:

                  Always be respectful and considerate of the other person's feelings.
                  Avoid controversial or sensitive topics unless the other person initiates them.
                  Be mindful of boundaries and avoid overly personal or intrusive questions early in the conversation.

                """;
        SystemMessage systemMessage = new SystemMessage(SYSTEM_MESSAGE);
        // User Message
//        String toMassage = conversation.messages().getFirst().messageText();
//        UserMessage userMessage = new UserMessage(toMassage);

        // Assistance Message
//        List<Message> messages = List.of(systemMessage, userMessage);
        List<AbstractMessage> convertedMessages = mapConversationToUserAssistanceMessage(conversation, profile.id());
        List<Message> allMessages = new ArrayList<>();
        allMessages.add(systemMessage);
        allMessages.addAll(convertedMessages);

        Prompt prompt = new Prompt(allMessages);
        var response = chatClient.call(prompt).getResult().getOutput().getContent();
        System.out.println(STR."Response: \{response}");
        conversation.messages().add(new ChatMessage(
                response,
                profile.id(),
                LocalDateTime.now()
        ));
//        return conversation;
    }

    private List<AbstractMessage> mapConversationToUserAssistanceMessage(Conversation conversation, final String profileId) {
        return conversation.messages().stream().map(message -> {
            return ((message.authorId().equals(profileId))) ? new AssistantMessage(message.messageText())
                        : new UserMessage(message.messageText());
        }).toList();
    }

}
