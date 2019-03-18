package no.nav.dolly.aareg;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.dolly.cxf.TimeoutFeature;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.sts.StsConfigUtil;
import no.nav.dolly.sts.StsProps;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.BehandleArbeidsforholdPortType;

@Service
public class BehandleArbeidsforholdV1Proxy {

    private static final long TIMEOUT = 4 * 60 * 60 * 1000L;

    private static final int DEFAULT_TIMEOUT = 5_000;

    private static final String WSDL_URL = "wsdl/no/nav/tjeneste/domene/behandleArbeidsforhold/v1/BehandleArbeidsforhold.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/domene/behandleArbeidsforhold/v1/";
    private static final QName BEHANDLE_ARBEIDSFORHOLD_V1 = new QName(NAMESPACE, "BehandleArbeidsforhold_v1");

    @Autowired
    private StsProps stsProps;

    @Autowired
    private BehandleArbeidsforholdFasitConsumer behandleArbeidsforholdFasitConsumer;

    private Map<String, BehandleArbeidsforholdPortType> wsServiceByEnvironment = new HashMap();
    private long timestamp;

    public BehandleArbeidsforholdPortType getServiceByEnvironment(String environment) {

        if (timestamp < new Date().getTime() - TIMEOUT) {
            Map<String, String> urlByEnvironment = behandleArbeidsforholdFasitConsumer.fetchUrlsByEnvironment();
            urlByEnvironment.forEach((env, url) -> wsServiceByEnvironment.put(env, createBehandleArbeidsforholdPortType(url)));
            timestamp = new Date().getTime();
        }

        if (wsServiceByEnvironment.containsKey(environment)) {
            return wsServiceByEnvironment.get(environment);
        } else {
            throw new DollyFunctionalException("Ugyldig miljø/miljø ikke funnet.");
        }
    }

    private BehandleArbeidsforholdPortType createBehandleArbeidsforholdPortType(String url) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL_URL);
        factoryBean.setServiceName(BEHANDLE_ARBEIDSFORHOLD_V1);
        factoryBean.setEndpointName(BEHANDLE_ARBEIDSFORHOLD_V1);
        factoryBean.setServiceClass(BehandleArbeidsforholdPortType.class);
        factoryBean.setAddress(url);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new TimeoutFeature(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT));
        BehandleArbeidsforholdPortType behandleArbeidsforholdPortType = factoryBean.create(BehandleArbeidsforholdPortType.class);
        StsConfigUtil.configureStsRequestSamlToken(behandleArbeidsforholdPortType, stsProps);

        return behandleArbeidsforholdPortType;
    }
}
