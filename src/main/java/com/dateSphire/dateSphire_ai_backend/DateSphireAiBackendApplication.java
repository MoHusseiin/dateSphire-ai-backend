package com.dateSphire.dateSphire_ai_backend;

import com.dateSphire.dateSphire_ai_backend.conversations.ChatMessage;
import com.dateSphire.dateSphire_ai_backend.conversations.Conversation;
import com.dateSphire.dateSphire_ai_backend.conversations.ConversationRepository;
import com.dateSphire.dateSphire_ai_backend.profiles.Gender;
import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class DateSphireAiBackendApplication implements CommandLineRunner {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ConversationRepository conversationRepository;

	public static void main(String[] args) {
		SpringApplication.run(DateSphireAiBackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		profileRepository.deleteAll();
		conversationRepository.deleteAll();

		Profile profile = new Profile(
				"1",
				"Mohamed",
				"Shika",
				31,
				"White",
				Gender.MALE,
				"Full Stack Engineer",
				"mo.jpg",
				"INTP"
		);
		profileRepository.save(profile);
		System.out.println("Profile saved successfully...");
		profileRepository.findAll().forEach(System.out::println);

		Conversation  conversation = new Conversation(
				"1",
				profile.id(),  // will be AI created Profiles
				List.of(new ChatMessage(
						"Hello",
						profile.id(),
						LocalDateTime.now()))
		);
		conversationRepository.save(conversation);
		System.out.println("Conversation saved successfully...");
		conversationRepository.findAll().forEach(System.out::println);
	}
}
