package no.nav.registre.aareg.consumer.ws;

import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.cxf.TimeoutFeature;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.security.sts.StsSamlTokenService;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.BehandleArbeidsforholdPortType;

@Component
@RequiredArgsConstructor
public class BehandleArbeidsforholdV1Proxy {

    private static final int DEFAULT_TIMEOUT = 5_000;

    private static final String WSDL_URL = "wsdl/no/nav/tjeneste/domene/behandleArbeidsforhold/v1/BehandleArbeidsforhold.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/domene/behandleArbeidsforhold/v1/";
    private static final QName BEHANDLE_ARBEIDSFORHOLD_V1 = new QName(NAMESPACE, "BehandleArbeidsforhold_v1");

    private final StsSamlTokenService stsSamlTokenService;
    private final AaregBehandleArbeidsforholdFasitConsumer behandleArbeidsforholdFasitConsumer;

    private Map<String, BehandleArbeidsforholdPortType> wsServiceByEnvironment = new HashMap<>();
    private LocalDateTime expiry;

    public BehandleArbeidsforholdPortType getServiceByEnvironment(String environment) {
        if (hasExpired()) {
            synchronized (this) {
                if (hasExpired()) {
                    Map<String, String> urlByEnvironment = behandleArbeidsforholdFasitConsumer.fetchWsUrlsAllEnvironments();
                    urlByEnvironment.forEach((env, url) -> wsServiceByEnvironment.put(env, createBehandleArbeidsforholdPortType(env, url)));
                    expiry = LocalDateTime.now().plusHours(4);
                }
            }
        }

        if (wsServiceByEnvironment.containsKey(environment)) {
            return wsServiceByEnvironment.get(environment);
        } else {
            throw new TestnorgeAaregFunctionalException("Ugyldig miljø/miljø ikke funnet.");
        }
    }

    private boolean hasExpired() {
        return isNull(expiry) || LocalDateTime.now().isAfter(expiry);
    }

    private BehandleArbeidsforholdPortType createBehandleArbeidsforholdPortType(String env, String url) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL_URL);
        factoryBean.setServiceName(BEHANDLE_ARBEIDSFORHOLD_V1);
        factoryBean.setEndpointName(BEHANDLE_ARBEIDSFORHOLD_V1);
        factoryBean.setServiceClass(BehandleArbeidsforholdPortType.class);
        factoryBean.setAddress(url);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new TimeoutFeature(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT));
        BehandleArbeidsforholdPortType behandleArbeidsforholdPortType = factoryBean.create(BehandleArbeidsforholdPortType.class);
        stsSamlTokenService.configureStsRequestSamlToken(behandleArbeidsforholdPortType, env);

        return behandleArbeidsforholdPortType;
    }
}
