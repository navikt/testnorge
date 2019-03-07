package no.nav.dolly.aareg;

import javax.xml.namespace.QName;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.dolly.cxf.TimeoutFeature;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.sts.STSConfig;
import no.nav.dolly.sts.STSConfigUtil;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.BehandleArbeidsforholdPortType;

@Configuration
public class BehandleArbeidsforholdV1Config {

    private static final int DEFAULT_TIMEOUT = 30_000;

    private static final String WSDL_URL = "wsdl/no/nav/tjeneste/domene/behandleArbeidsforhold/v1/BehandleArbeidsforhold.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/domene/behandleArbeidsforhold/v1/";
    private static final QName BEHANDLE_ARBEIDSFORHOLD_V1_PORT_QNAME = new QName(NAMESPACE, "BehandleArbeidsforhold_v1");
    private static final QName BEHANDLE_ARBEIDSFORHOLD_V1_SERVICE_QNAME = new QName(NAMESPACE, "BehandleArbeidsforhold_v1");

    @Autowired
    private ProvidersProps providersProps;

    @Autowired
    private STSConfig stsConfig;

    @Bean
    public BehandleArbeidsforholdPortType behandleArbeidsforholdPortType() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL_URL);
        factoryBean.setServiceName(BEHANDLE_ARBEIDSFORHOLD_V1_SERVICE_QNAME);
        factoryBean.setEndpointName(BEHANDLE_ARBEIDSFORHOLD_V1_PORT_QNAME);
        factoryBean.setAddress(providersProps.getAareg().getUrl());
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new TimeoutFeature(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT));
        BehandleArbeidsforholdPortType behandleArbeidsforholdPortType = factoryBean.create(BehandleArbeidsforholdPortType.class);
        STSConfigUtil.configureStsRequestSamlToken(behandleArbeidsforholdPortType, stsConfig);

        return behandleArbeidsforholdPortType;
    }
}
