package coop.stlma.tech.protocolsn.commonlib.security;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.security.config.SecurityConfiguration;
import io.micronaut.security.oauth2.client.OpenIdProviderMetadata;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.oauth2.endpoint.endsession.request.AuthorizationServerResolver;
import io.micronaut.security.oauth2.endpoint.endsession.request.EndSessionEndpoint;
import io.micronaut.security.oauth2.endpoint.endsession.request.EndSessionEndpointResolver;
import io.micronaut.security.oauth2.endpoint.endsession.request.OktaEndSessionEndpoint;
import io.micronaut.security.oauth2.endpoint.endsession.response.EndSessionCallbackUrlBuilder;
import io.micronaut.security.token.reader.TokenResolver;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Override default session end behavior to be compatible with Keycloak.
 *
 * @author John Meyerin
 */
@Singleton
@Replaces(EndSessionEndpointResolver.class)
@Requires(property = "coop.stlma.tech.protocolsn.security", value = "keycloak", defaultValue = "keycloak")
public class KeycloakEndSessionEndpointResolverReplacement extends EndSessionEndpointResolver {

    private final TokenResolver tokenResolver;
    private final SecurityConfiguration securityConfiguration;

    /**
     * @param beanContext The bean context
     */
    public KeycloakEndSessionEndpointResolverReplacement(BeanContext beanContext,
                                                         SecurityConfiguration securityConfiguration,
                                                         TokenResolver tokenResolver,
                                                         AuthorizationServerResolver authorizationServerResolver) {
        super(beanContext, authorizationServerResolver);
        this.tokenResolver = tokenResolver;
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Resolve logging out of keycloak
     * @param oauthClientConfiguration      config
     * @param openIdProviderMetadata        oidc meta
     * @param endSessionCallbackUrlBuilder  url builder
     * @return
     */
    @Override
    public Optional<EndSessionEndpoint> resolve(OauthClientConfiguration oauthClientConfiguration,
                                                Supplier<OpenIdProviderMetadata> openIdProviderMetadata,
                                                EndSessionCallbackUrlBuilder endSessionCallbackUrlBuilder) {
        return Optional.of(new OktaEndSessionEndpoint(endSessionCallbackUrlBuilder,
                oauthClientConfiguration,
                openIdProviderMetadata,
                securityConfiguration,
                tokenResolver));
    }
}
