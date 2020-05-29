package no.nav.registre.aareg.security.sts;

import static no.nav.registre.aareg.properties.Environment.convertEnv;
import static org.apache.cxf.rt.security.SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT;
import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.STS_CLIENT;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.properties.CredentialsProps;
import no.nav.registre.aareg.properties.Environment;

@Service
@RequiredArgsConstructor
public class StsSamlTokenService {

    private static final String STS_REQUEST_SAML_POLICY = "classpath:policy/requestSamlPolicy.xml";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = "classpath:policy/untPolicy.xml";

    private final StsSamlFasitConsumer stsSamlFasitConsumer;
    private final CredentialsProps credentialsProps;

    private static Policy resolvePolicyReference(Client client) {
        var policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        var resolver = new RemoteReferenceResolver("", policyBuilder);
        return resolver.resolveReference(StsSamlTokenService.STS_REQUEST_SAML_POLICY);
    }

    private static void setClientEndpointPolicy(Client client, Policy policy) {
        var endpoint = client.getEndpoint();
        var endpointInfo = endpoint.getEndpointInfo();

        var policyEngine = client.getBus().getExtension(PolicyEngine.class);
        var message = new SoapMessage(Soap12.getInstance());
        var endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }

    public void configureStsRequestSamlToken(Object port, String env) {
        var client = ClientProxy.getClient(port);
        var stsClient = new STSClient(client.getBus());
        configureSTSClient(stsClient, convertEnv(env));

        client.getRequestContext().put(STS_CLIENT, stsClient);
        //Using CXF cache
        client.getRequestContext().put(CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);
        var policy = resolvePolicyReference(client);
        setClientEndpointPolicy(client, policy);
    }

    private void configureSTSClient(STSClient stsClient, Environment env) {
        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(stsSamlFasitConsumer.getStsSamlService(env));

        Map<String, Object> properties = new HashMap<>();
        properties.put(USERNAME, credentialsProps.getUsername(env));
        properties.put(PASSWORD, credentialsProps.getPassword(env));

        stsClient.setProperties(properties);

        //used for the STS client to authenticate itself to the STS provider.
        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
    }
}
