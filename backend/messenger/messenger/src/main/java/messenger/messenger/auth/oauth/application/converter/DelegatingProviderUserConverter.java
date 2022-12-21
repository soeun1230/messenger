package messenger.messenger.auth.oauth.application.converter;

import io.jsonwebtoken.lang.Assert;
import messenger.messenger.auth.oauth.domain.social.ProviderUser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
public class DelegatingProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    private List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> converters;

    public DelegatingProviderUserConverter() {

        List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> converters = Arrays.asList(
                new UserDetailsProviderUserConverter(),
                new OAuth2GoogleProviderUserConverter(),
                new OAuth2NaverProviderUserConverter()
        );

        this.converters = Collections.unmodifiableList(new LinkedList<>(converters));
    }

    @Override
    public ProviderUser converter(ProviderUserRequest providerUserRequest) {

        Assert.notNull(providerUserRequest, "providerUserRequest cannot be null");

        for (ProviderUserConverter<ProviderUserRequest, ProviderUser> converter : converters) {
            ProviderUser providerUser = converter.converter(providerUserRequest);

            if (providerUser != null) return providerUser;
        }

        return null;
    }
}