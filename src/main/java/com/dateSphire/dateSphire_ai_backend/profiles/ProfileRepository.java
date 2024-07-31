package com.dateSphire.dateSphire_ai_backend.profiles;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
}
