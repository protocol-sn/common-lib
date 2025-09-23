package coop.stlma.tech.protocolsn.commonlib.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.oauth2.client.IdTokenClaimsValidator;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.token.Claims;
import jakarta.inject.Singleton;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The azp claim is optional and should match the issuing client, but does not have to match the client id of the resource server. Micronaut appears to disagree and will fail validation of the azp claim if it does not match this server's client ID. Creating a replacement that will instead match against the audience *or* the azp.
 *
 * @param <T>
 * @author John Meyerin
 */
@Singleton
@Replaces(IdTokenClaimsValidator.class)
public class IgnoreAzpIdTokenClaimsValidator<T> extends IdTokenClaimsValidator<T> {

    /**
     * @param oauthClientConfigurations OpenId client configurations
     */
    public IgnoreAzpIdTokenClaimsValidator(Collection<OauthClientConfiguration> oauthClientConfigurations) {
        super(oauthClientConfigurations);
    }

    @Override
    protected boolean validateAzp(@NonNull Claims claims,
                                  @NonNull String clientId,
                                  @NonNull List<String> audiences) {
        if (audiences.size() < 2) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("{} claim is not required for single audiences", AUTHORIZED_PARTY);
            }
            return true;
        }
        Optional<String> azpOptional = parseAzpClaim(claims);
        if (azpOptional.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("azp claim not present");
            }
            return false;
        }
        String azp = azpOptional.get();
        boolean result = azp.equalsIgnoreCase(clientId) || audiences.contains(clientId);
        if (!result && LOG.isDebugEnabled()) {
            LOG.debug("{} claim does not match client id {}", AUTHORIZED_PARTY, clientId);
        }
        return result;
    }
}
