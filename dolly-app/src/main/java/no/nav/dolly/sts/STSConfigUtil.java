package no.nav.dolly.sts;

import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;

public class STSConfigUtil {

    private static final String STS_REQUEST_SAML_POLICY = "classpath:policy/requestSamlPolicy.xml";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = "classpath:policy/untPolicy.xml";

    private STSConfigUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void configureStsRequestSamlToken(Object port, STSConfig stsConfig) {

        Client client = ClientProxy.getClient(port);
        STSClient stsClient = new STSClient(client.getBus());
        configureSTSClient(stsClient, stsConfig);

        client.getRequestContext().put(SecurityConstants.STS_CLIENT, stsClient);
        //Using CXF cache
        client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);
        Policy policy = resolvePolicyReference(client);
        setClientEndpointPolicy(client, policy);
    }

    private static void configureSTSClient(STSClient stsClient, STSConfig stsConfig) {

        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(stsConfig.getStsUrl());

        Map<String, Object> properties = new HashMap<>();
        properties.put(SecurityConstants.USERNAME, stsConfig.getServiceUsername());
        properties.put(SecurityConstants.PASSWORD, stsConfig.getServiceUserPassword());

        stsClient.setProperties(properties);

        //used for the STS client to authenticate itself to the STS provider.
        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
    }

    private static Policy resolvePolicyReference(Client client) {
        PolicyBuilder policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        ReferenceResolver resolver = new RemoteReferenceResolver("", policyBuilder);
        return resolver.resolveReference(STSConfigUtil.STS_REQUEST_SAML_POLICY);
    }

    private static void setClientEndpointPolicy(Client client, Policy policy) {
        Endpoint endpoint = client.getEndpoint();
        EndpointInfo endpointInfo = endpoint.getEndpointInfo();

        PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
        SoapMessage message = new SoapMessage(Soap12.getInstance());
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }
}
