package com.dateSphire.dateSphire_ai_backend;

import com.dateSphire.dateSphire_ai_backend.profiles.Gender;
import com.dateSphire.dateSphire_ai_backend.profiles.Profile;
import com.dateSphire.dateSphire_ai_backend.profiles.ProfileRepository;
import org.codehaus.groovy.control.ProcessingUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DateSphireAiBackendApplication implements CommandLineRunner {

	@Autowired
	private ProfileRepository profileRepository;

	public static void main(String[] args) {
		SpringApplication.run(DateSphireAiBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
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
	}
}
