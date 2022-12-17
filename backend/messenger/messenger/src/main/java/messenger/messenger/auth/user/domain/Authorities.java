package messenger.messenger.auth.user.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Authorities {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String authoritiesName;

}
