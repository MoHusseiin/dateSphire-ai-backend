package com.dateSphire.dateSphire_ai_backend.matches;


import com.dateSphire.dateSphire_ai_backend.profiles.Profile;

public record Match(String id,
                    Profile profile,
                    String conversationId) {
}
