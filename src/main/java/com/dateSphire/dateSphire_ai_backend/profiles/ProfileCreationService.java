package com.dateSphire.dateSphire_ai_backend.profiles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@Service
public class ProfileCreationService {

    @Value("#{${dateSphire.ai.admin.user}}")
    private Map<String, String> userProfileProperties;

    private final ProfileRepository profileRepository;

    private OllamaChatModel chatClient;

    @Value("${dateSphire.ai.lookingForGender}")
    private String lookingForGender;

    @Value("${dateSphire.ai.initializeProfile}")
    private boolean initializeProfile;

    private static final String PROFILES_FILE_PATH = "profiles.json";

    public ProfileCreationService(ProfileRepository profileRepository, OllamaChatModel chatClient) {
        this.profileRepository = profileRepository;
        this.chatClient = chatClient;
    }

    public void saveProfilesToDB() {
        Gson gson = new Gson();
        try {
            List<Profile> existingProfiles = gson.fromJson(
                    new FileReader(PROFILES_FILE_PATH),
                    new TypeToken<ArrayList<Profile>>() {}.getType()
            );
            profileRepository.deleteAll();
            profileRepository.saveAll(existingProfiles);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Profile profile = new Profile(
                userProfileProperties.get("id"),
                userProfileProperties.get("firstName"),
                userProfileProperties.get("lastName"),
                Integer.parseInt(userProfileProperties.get("age")),
                userProfileProperties.get("ethnicity"),
                Gender.valueOf(userProfileProperties.get("gender")),
                userProfileProperties.get("bio"),
                userProfileProperties.get("imageUrl"),
                userProfileProperties.get("myersBriggsPersonalityType")
        );
        System.out.println(userProfileProperties);
        profileRepository.save(profile);

    }

    public void createProfiles() {
        if (!this.initializeProfile) return;
        var age = getRandomAge();
        var ethnicity = getRandomEthnicity();
        String promptContext = STR."""
                        Create a Tinder profile of a \{age} years old \{ethnicity} \{this.lookingForGender}
                         including the first Name, last Name, Myers Briggs Personality type and a tinder Bio. Save the profile using the saveProfile function
                        """;
        System.out.println(STR."Profile creation started ...\n \{promptContext}");
        ChatResponse chatResponse = chatClient.call(new Prompt(promptContext, OllamaOptions.builder().withFunction("saveProfile").build()));
    }

    @Bean
    @Description("Save Profile information")
    public Function<Profile, Boolean> saveProfile(){
        return (profile -> {
            System.out.println("This is the function we expect.. ");
            System.out.println(profile);
            return true;
        });
    }

    private String getRandomEthnicity() {
        final String[] ETHNICITIES = {
                "Asian", "Black", "Hispanic", "White", "Native American", "Pacific Islander", "Mixed"
        };
        Random random = new Random();
        int index = random.nextInt(ETHNICITIES.length);
        return ETHNICITIES[index];

    }

    private int getRandomAge() {
        Random random = new Random();
        return 18 + random.nextInt(28); // 28 is the range (45 - 18 + 1)
    }
}
