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
import java.io.FileWriter;
import java.io.IOException;
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

    private final List<Profile> generatedProfiles = new ArrayList<>();

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

    public void createProfiles(int numberOfProfiles) {
        if (!this.initializeProfile) return;
        ChatResponse chatResponse;
        int age;
        String ethnicity, promptContext;
        for (int i = 1; i <= numberOfProfiles; i++) {
            age = getRandomAge();
            ethnicity = getRandomEthnicity();
            promptContext = STR."""
                        Create a \{this.lookingForGender} Tinder profile of a \{age} years old \{ethnicity}
                         including the first Name, last Name, Myers Briggs Personality type and Bio. Save the profile using the saveProfile function
                        """;
            chatResponse = chatClient.call(new Prompt(promptContext, OllamaOptions.builder().withFunction("saveProfile").build()));

//            System.out.println(chatResponse.getResult().getOutput());
        }
        if(!this.generatedProfiles.isEmpty()){
            saveProfilesToJson();
        }
    }

    @Bean
    @Description("Save Profile information")
    public Function<Profile, Boolean> saveProfile(){
        return (profile -> {
            System.out.println(profile);
            this.generatedProfiles.add(profile);
            return true;
        });
    }

    private String getRandomEthnicity() {
        final String[] ETHNICITIES = {
                "Asian", "Black", "Hispanic", "White", "Native American", "Pacific Islander", "Mixed", "Indian-American",
                "Chinese-Canadian", "French", "Germany", "African-Saudi Arabian", "Puerto Rican-Mexican-American"
        };
        Random random = new Random();
        int index = random.nextInt(ETHNICITIES.length);
        return ETHNICITIES[index];

    }

    private int getRandomAge() {
        Random random = new Random();
        return 18 + random.nextInt(28); // 28 is the range (45 - 18 + 1)
    }

    private void saveProfilesToJson() {
        Gson gson = new Gson();
        try {
            List<Profile> existingProfiles = gson.fromJson(
                    new FileReader("generatedProfiles.json"),
                    new TypeToken<ArrayList<Profile>>() {}.getType()
            );
            System.out.println(STR."\{existingProfiles.size()} Profiles found");
            this.generatedProfiles.addAll(existingProfiles);
            System.out.println(STR."Saving \{this.generatedProfiles.size()} profiles to JSON");
            String jsonString = new Gson().toJson(this.generatedProfiles);
            FileWriter writer = new FileWriter("generatedProfiles.json");
            writer.write(jsonString);
            System.out.println("Done Saving profiles to JSON !!");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
