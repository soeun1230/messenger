package messenger.messenger.auth.oauth.application;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

import static org.springframework.web.servlet.view.AbstractView.DEFAULT_CONTENT_TYPE;

public class CustomOAuth2UserRequestEntityConverter implements Converter<OAuth2UserRequest, RequestEntity<?>> {

    @Override
    public RequestEntity<?> convert(OAuth2UserRequest userRequest) {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        HttpMethod httpMethod = getHttpMethod(clientRegistration);

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri());

        RequestEntity<?> request;
        if (HttpMethod.POST.equals(httpMethod)) {
            headers.setContentType(MediaType.valueOf(DEFAULT_CONTENT_TYPE));
            MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
            formParameters.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());
            request = new RequestEntity<>(formParameters, headers, httpMethod, uriBuilder.build().toUri());
        } else {
            URI uri = uriBuilder.queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                    .queryParam("output", "json").build().toUri();
            request = new RequestEntity<>(headers, httpMethod, uri);
        }

        return request;
    }

    /**
     * Method that can be used to find out actual input (source) type; this
     * usually can be determined from type parameters, but may need
     * to be implemented differently from programmatically defined
     * converters (which cannot change static type parameter bindings).
     *
     * @param typeFactory
     * @since 2.2
     */
    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    /**
     * Method that can be used to find out actual output (target) type; this
     * usually can be determined from type parameters, but may need
     * to be implemented differently from programmatically defined
     * converters (which cannot change static type parameter bindings).
     *
     * @param typeFactory
     * @since 2.2
     */
    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }

    private HttpMethod getHttpMethod(ClientRegistration clientRegistration) {
        return null;
    }

}