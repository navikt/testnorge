package no.nav.udistub.provider.ws.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.springframework.security.authentication.Pac4jAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

public class STSConfigurationUtil {


    /**
     * Configures endpoint to get SAML token for the system user from STS.
     * The SAML token will be added as a SupportingToken to the WS-Security headers.
     * <p/>
     * 1. Binds a WS-SecurityPolicy to the endpoint/client.
     * The policy requires a SupportingToken of type IssuedToken.
     * <p/>
     * 2. Configures the location and credentials of the STS.
     *
     * @param client CXF client
     */
    public static void configureStsForSystemUser(Client client) {
        Supplier<String> samlXmlSupplier = () -> {
            var stsRestClient = SpringContextAccessor.getBean(NavStsRestClient.class);
            return stsRestClient.getSystemSaml().decodedToken();
        };

        configureSts(client, samlXmlSupplier);
    }

    private static void configureSts(Client client, Supplier<String> samlXmlSupplier) {
        var interceptor = new AttachSamlHeaderOutInterceptor(samlXmlSupplier);
        client.getOutInterceptors().add(interceptor);

        new WSAddressingFeature().initialize(client, client.getBus());
    }

    /**
     * Configures endpoint to get SAML token for the end user from STS in exchange for OpenAM token.
     * The SAML token will be added as a SupportingToken to the WS-Security headers.
     * <p/>
     * 1. Binds a WS-SecurityPolicy to the endpoint/client.
     * The policy requires a SupportingToken of type IssuedToken.
     * <p/>
     * 2. Configures the location and credentials of the STS.
     *
     * @param client CXF client
     */
    public static void configureStsForISSO(Client client) {
        Supplier<String> samlXmlSupplier = () -> {
            var stsRestClient = SpringContextAccessor.getBean(NavStsRestClient.class);
           // for AzureAD String userToken = ((OidcProfile) ((Pac4jAuthentication) SecurityContextHolder.getContext().getAuthentication()).getProfile()).getAccessToken().getValue();

            String userToken = ((OidcProfile) ((Pac4jAuthentication) SecurityContextHolder.getContext().getAuthentication()).getProfile()).getIdTokenString();


            return stsRestClient.exchangeForSaml(userToken).decodedToken();
        };

        configureSts(client, samlXmlSupplier);
    }



}
