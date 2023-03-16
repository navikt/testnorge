package no.nav.udistub.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean(name = "udistub")
    public DefaultWsdl11Definition defaultWsdl11Definition() {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setSchema(new SimpleXsdSchema(new ClassPathResource("resources/wsdl/schema/MT_1067_NAV_Data_v1.xsd")));
        wsdl11Definition.setTargetNamespace("http://udi.no/MT_1067_NAV_Data/v1");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setPortTypeName("UdistubPort");
        return wsdl11Definition;
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

//    @Bean
//    public Wss4jSecurityInterceptor securityInterceptor() {
//        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
//        securityInterceptor.setValidationActions("Timestamp UsernameToken Encrypt Signature NoSecurity");
//        securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
//        return securityInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(List interceptors) {
//        interceptors.add(securityInterceptor());
//    }
//
//    @Bean
//    public SimplePasswordValidationCallbackHandler securityCallbackHandler() {
//        return new SimplePasswordValidationCallbackHandler();
//    }
}
