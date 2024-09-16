package com.dateSphire.dateSphire_ai_backend;

import com.dateSphire.dateSphire_ai_backend.conversations.ConversationRepository;
import com.dateSphire.dateSphire_ai_backend.matches.MatchRepository;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileCreationService;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DateSphireAiBackendApplication implements CommandLineRunner {

	@Autowired
	private ProfileCreationService profileCreationService;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private MatchRepository	matchRepository;
	@Autowired
	private ConversationRepository conversationRepository;

	public static void main(String[] args) {
		SpringApplication.run(DateSphireAiBackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		clearAllData();
//		profileCreationService.saveProfilesToDB();
		profileCreationService.createProfiles();
	}

	private void clearAllData() {
		conversationRepository.deleteAll();
		matchRepository.deleteAll();
		profileRepository.deleteAll();
	}
}
