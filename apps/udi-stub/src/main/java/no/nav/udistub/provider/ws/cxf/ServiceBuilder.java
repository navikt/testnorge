package no.nav.udistub.provider.ws.cxf;


import no.nav.consumer.gosys.utils.SecurityProps;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.List;

/**
 * Builder klasse for å lage en porttype.
 *
 * @param <T> klassen det lages for
 */
public final class ServiceBuilder<T> {

    public static final int RECEIVE_TIMEOUT = 10000;
    public static final int CONNECTION_TIMEOUT = 10000;
    public Class<T> resultClass;
    private JaxWsProxyFactoryBean factoryBean;
    

    public ServiceBuilder(Class<T> resultClass) {
        factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(resultClass);
        this.resultClass = resultClass;
    }

    public ServiceBuilder<T> withExtraClasses(Class[] classes) {
        factoryBean.getProperties().put("jaxb.additionalContextClasses", classes);
        return this;
    }

    public ServiceBuilder<T> withWsdl(String wsdl) {
        factoryBean.setWsdlURL(wsdl);
        return this;
    }

    public ServiceBuilder<T> withServiceName(QName name) {
        factoryBean.setServiceName(name);
        return this;
    }

    public ServiceBuilder<T> withEndpointName(QName name) {
        factoryBean.setEndpointName(name);
        return this;
    }

    public ServiceBuilder<T> withAddress(String address) {
        factoryBean.setAddress(address);
        return this;
    }

    public ServiceBuilder<T> withLogging() {
        factoryBean.getFeatures().add(new LoggingFeatureUtenBinaryOgUtenSamlTokenLogging(true));
        return this;
    }

    public ServiceBuilder<T> withAddressing() {
        factoryBean.getFeatures().add(new WSAddressingFeature());
        return this;
    }


    public ServiceBuilder<T> withTimeout() {
        factoryBean.getFeatures().add(new TimeoutFeature(RECEIVE_TIMEOUT, CONNECTION_TIMEOUT));
        return this;
    }
    public ServiceBuilder<T> withTimeout(int recieveTimeout, int connectionTimeout) {
        factoryBean.getFeatures().add(new TimeoutFeature(recieveTimeout,connectionTimeout));
        return this;
    }

    public JaxWsProxyFactoryBean get() {
        return factoryBean;
    }



    public PortTypeBuilder<T> build() {
        return new PortTypeBuilder<>(factoryBean.create(resultClass));
    }

    public ServiceBuilder<T> asStandardService() {
        return this.withAddressing()
                .withLogging()
                .withTimeout();
    }

    public final class PortTypeBuilder<R> {
        public final R portType;

        private PortTypeBuilder(R factoryBean) {
            this.portType = factoryBean;
        }

        public PortTypeBuilder<R> withUserSecurity() {
            STSConfigurationUtil.configureStsForISSO(ClientProxy.getClient(portType));
            return this;
        }

        public PortTypeBuilder<R> withSystemSecurity() {
            STSConfigurationUtil.configureStsForSystemUser(ClientProxy.getClient(portType));
            return this;
        }

        public PortTypeBuilder<R> withServiceUserSecurity(SecurityProps securityProps) {
            ClientProxy.getClient(portType).getOutInterceptors().add(new WSS4JOutInterceptor(securityProps));
            return this;
        }

        public PortTypeBuilder<R> withHandlers(Handler ...handlers) {
            ((BindingProvider) portType).getBinding().setHandlerChain(List.of(handlers));
            return this;
        }

        public PortTypeBuilder<R> withInterceptor(Interceptor interceptor) {
            ClientProxy.getClient(portType).getOutInterceptors().add(interceptor);
            return this;
        }

        public R get() {
            return portType;
        }
    }
}
