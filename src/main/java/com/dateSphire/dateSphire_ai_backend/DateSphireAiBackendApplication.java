package com.dateSphire.dateSphire_ai_backend;

import com.dateSphire.dateSphire_ai_backend.profiles.ProfileCreationService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DateSphireAiBackendApplication implements CommandLineRunner {

	@Autowired
	private ProfileCreationService profileCreationService;

	@Autowired
	private OpenAiChatModel chatModel;

	public static void main(String[] args) {
		SpringApplication.run(DateSphireAiBackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		profileCreationService.saveProfilesToDB();
	}
}
